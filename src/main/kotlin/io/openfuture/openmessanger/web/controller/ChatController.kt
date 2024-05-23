package io.openfuture.openmessanger.web.controller

import io.openfuture.openmessanger.service.MessageService
import io.openfuture.openmessanger.web.request.MessageRequest
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