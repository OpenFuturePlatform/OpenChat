package io.openfuture.openmessenger.service.impl

import io.openfuture.openmessenger.assistant.gemini.GeminiService
import io.openfuture.openmessenger.repository.*
import io.openfuture.openmessenger.repository.entity.*
import io.openfuture.openmessenger.service.MessageService
import io.openfuture.openmessenger.service.PrivateChatService
import io.openfuture.openmessenger.service.UserService
import io.openfuture.openmessenger.web.request.GroupMessageRequest
import io.openfuture.openmessenger.web.request.MessageRequest
import io.openfuture.openmessenger.web.request.MessageToAssistantRequest
import io.openfuture.openmessenger.web.response.GroupMessageResponse
import io.openfuture.openmessenger.web.response.LastMessage
import io.openfuture.openmessenger.web.response.MessageResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
class MessageServiceImpl(
    val messageRepository: MessageRepository,
    val privateChatRepository: PrivateChatRepository,
    val messagingTemplate: SimpMessagingTemplate,
    val chatParticipantRepository: ChatParticipantRepository,
    val privateChatService: PrivateChatService,
    val userService: UserService,
    val groupParticipantRepository: GroupParticipantRepository,
    val groupChatRepository: GroupChatRepository,
    val geminiService: GeminiService,
    val messageAttachmentRepository: MessageAttachmentRepository
) : MessageService {

    companion object {
        val log: Logger = LoggerFactory.getLogger(MessageServiceImpl::class.java)
    }

    override fun sendMessage(request: MessageRequest) {
        val privateChat = getPrivateChat(request)
        val message = MessageEntity(
            request.body,
            request.sender!!,
            request.recipient,
            request.contentType!!,
            LocalDateTime.now(),
            LocalDateTime.now(),
            privateChat.id
        )
        messageRepository.save(message)
        messagingTemplate.convertAndSendToUser(request.recipient!!, "/direct", message)
    }

    private fun getPrivateChat(request: MessageRequest): PrivateChat {
        if (request.sender == request.recipient) {
            val selfChat = privateChatRepository.findSelfChat(request.sender)
            if (selfChat?.isPresent == true) {
                return selfChat.get()
            }
            val newPrivateChat = privateChatRepository.save(PrivateChat("SELF"))
            val singleParticipant = ChatParticipant(newPrivateChat.id, request.sender)
            chatParticipantRepository.save(singleParticipant)
            newPrivateChat.chatParticipants = listOf(singleParticipant)
            return newPrivateChat
        }
        val privateChat = privateChatRepository.findPrivateChatByParticipants(request.sender, request.recipient)
        if (privateChat?.isPresent == true) {
            return privateChat.get()
        }
        val newPrivateChat = privateChatRepository.save(PrivateChat("DEFAULT"))
        val sender = ChatParticipant(newPrivateChat.id, request.sender)
        val recipient = ChatParticipant(newPrivateChat.id, request.recipient)
        chatParticipantRepository.save(sender)
        chatParticipantRepository.save(recipient)
        newPrivateChat.chatParticipants = java.util.List.of(sender, recipient)
        return newPrivateChat
    }

    override fun save(request: MessageRequest): MessageResponse {
        val privateChat = getPrivateChat(request)
        val message = MessageEntity(
            request.body,
            request.sender!!,
            request.recipient,
            request.contentType!!,
            LocalDateTime.now(),
            LocalDateTime.now(),
            privateChat.id
        )

        messageRepository.save(message)
        request.attachments.forEach { attachment -> messageAttachmentRepository.save(MessageAttachment(attachment, message.id)) }

        return MessageResponse(
            message.id,
            message.sender,
            message.recipient!!,
            message.body!!,
            message.contentType,
            message.receivedAt!!,
            message.sentAt,
            message.privateChatId!!,
            null
        )
    }

    override fun saveAssistant(request: MessageToAssistantRequest): MessageResponse {
        log.info("POST REQUEST {}", request)
        val aiAssistant = MessageRequest(request.sender, "AI_ASSISTANT", request.contentType, request.body)
        val privateChat = getPrivateChat(aiAssistant)
        val message = MessageEntity(
            request.body,
            request.sender!!,
            "AI_ASSISTANT",
            request.contentType!!,
            LocalDateTime.now(),
            LocalDateTime.now(),
            privateChat.id
        )
        messageRepository.save(message)
        val response = geminiService.chat(request.body)
        val responseMessage = MessageEntity(
            response,
            "AI_ASSISTANT",  //he is a Sender
            request.sender,  //now he is recipient
            request.contentType,
            LocalDateTime.now(),
            LocalDateTime.now(),
            privateChat.id
        )
        val id = messageRepository.save(responseMessage)
        return MessageResponse(
            id,
            responseMessage.sender,
            responseMessage.recipient!!,
            response!!,
            responseMessage.contentType,
            responseMessage.receivedAt!!,
            responseMessage.sentAt,
            responseMessage.privateChatId!!,
            null
        )
    }

    override fun saveToGroup(request: GroupMessageRequest): GroupMessageResponse {
        val message = MessageEntity(
            request.body,
            request.sender!!,
            request.contentType!!,
            LocalDateTime.now(),
            request.groupId
        )
        messageRepository.save(message)
        request.attachments.forEach { attachment -> messageAttachmentRepository.save(MessageAttachment(attachment, message.id)) }

        return GroupMessageResponse(
            message.id,
            message.sender,
            message.body,
            message.contentType,
            message.sentAt,
            message.groupChatId
        )
    }

    override fun getAllByRecipient(recipient: String?): List<MessageResponse>? {
        val messageEntities = messageRepository.findByRecipient(recipient)
        return convertToMessageResponse(messageEntities)
    }

    override fun getAllByRecipientAndSender(recipient: String?, sender: String?): List<MessageResponse>? {
        val messageEntities = messageRepository.findByRecipientAndSender(recipient, sender)
        return convertToMessageResponse(messageEntities)
    }

    override fun getLastMessagesByRecipient(recipient: String): List<LastMessage> {
        val messageEntities = messageRepository.findLastMessagesByUsername(recipient)
        val filtered = messageEntities.filter { message: MessageEntity? -> message?.recipient != "AI_ASSISTANT" }
            .filter { message: MessageEntity? -> message?.sender != "AI_ASSISTANT" }
        val messages = convertToMessageResponse(filtered)
        val lastPrivateMessages = messages
            ?.map { m: MessageResponse ->
                val otherUser = privateChatService.getOtherUser(recipient, m.privateChatId)
                val user = userService.getByEmail(otherUser?.username)
                LastMessage(
                    m.privateChatId.toString(),
                    false,
                    otherUser?.username,
                    0,
                    m.sender,
                    m.content,
                    m.sentAt,
                    null
                )
            }
        val groupMessages = messageRepository.findGroupMessages(recipient)
        val lastGroupMessages = groupMessages
            .stream()
            .map { m: MessageEntity? ->
                val memberCount = groupParticipantRepository.findAllByGroupChat(GroupChat(m?.groupChatId)).size
                val groupChat = groupChatRepository.findById(m?.groupChatId!!)
                LastMessage(
                    m.groupChatId.toString(),
                    true,
                    groupChat.get().name,
                    memberCount,
                    m.sender,
                    m.body,
                    m.sentAt,
                    ""
                )
            }
            .toList()
        val allLastMessages = ArrayList<LastMessage>()
        allLastMessages.addAll(lastPrivateMessages!!)
        allLastMessages.addAll(lastGroupMessages)
        return allLastMessages
    }

    override fun getMessagesByChatId(chatId: Int?, type: String): List<MessageResponse>? {
        if (type == "PRIVATE_CHAT") {
            return convertToMessageResponse(messageRepository.findByPrivateChatId(chatId))
        }
        return if (type == "GROUP_CHAT") {
            convertToMessageResponse(messageRepository.findByGroupChatId(chatId))
        } else emptyList()
    }

    private fun convertToMessageResponse(messageEntities: List<MessageEntity?>?): List<MessageResponse>? {
        return messageEntities?.map { message: MessageEntity? ->
            val attachments = messageAttachmentRepository.findAllByMessageId(message!!.id)
            MessageResponse(
                message.id,
                message.sender,
                message.recipient!!,
                message.body!!,
                message.contentType,
                message.receivedAt!!,
                message.sentAt,
                message.privateChatId,
                message.groupChatId,
                attachments.map { messageAttachment -> messageAttachment.attachmentId!! }
            )
        }
    }
}