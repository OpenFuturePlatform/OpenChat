package io.openfuture.openmessenger.service

import io.openfuture.openmessenger.web.request.GroupMessageRequest
import io.openfuture.openmessenger.web.request.MessageRequest
import io.openfuture.openmessenger.web.request.MessageToAssistantRequest
import io.openfuture.openmessenger.web.response.GroupMessageResponse
import io.openfuture.openmessenger.web.response.LastMessage
import io.openfuture.openmessenger.web.response.MessageResponse

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