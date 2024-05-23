package io.openfuture.openmessanger.service

import io.openfuture.openmessanger.web.request.GroupMessageRequest
import io.openfuture.openmessanger.web.request.MessageRequest
import io.openfuture.openmessanger.web.request.MessageToAssistantRequest
import io.openfuture.openmessanger.web.response.GroupMessageResponse
import io.openfuture.openmessanger.web.response.LastMessage
import io.openfuture.openmessanger.web.response.MessageResponse

interface MessageService {
    fun sendMessage(request: MessageRequest)
    fun save(request: MessageRequest): MessageResponse
    fun saveAssistant(request: MessageToAssistantRequest): MessageResponse
    fun saveToGroup(request: GroupMessageRequest): GroupMessageResponse
    fun getAllByRecipient(recipient: String?): List<MessageResponse>?
    fun getAllByRecipientAndSender(recipient: String?, sender: String?): List<MessageResponse>?
    fun getLastMessagesByRecipient(recipient: String): List<LastMessage>
    fun getMessagesByChatId(chatId: Int?, type: String): List<MessageResponse>?
}