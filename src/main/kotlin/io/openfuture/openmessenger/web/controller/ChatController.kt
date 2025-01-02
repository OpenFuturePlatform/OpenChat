package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.service.MessageService
import io.openfuture.openmessenger.web.request.MessageRequest
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller

@Controller
class ChatController(
    val messageService: MessageService
) {

    @MessageMapping("/direct-message")
    fun sendMessage(@Payload request: MessageRequest) {
        messageService.sendMessage(request)
    }

}