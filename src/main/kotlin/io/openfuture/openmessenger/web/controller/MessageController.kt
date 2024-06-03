package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.service.MessageService
import io.openfuture.openmessenger.web.request.GroupMessageRequest
import io.openfuture.openmessenger.web.request.MessageRequest
import io.openfuture.openmessenger.web.request.MessageToAssistantRequest
import io.openfuture.openmessenger.web.response.GroupMessageResponse
import io.openfuture.openmessenger.web.response.LastMessage
import io.openfuture.openmessenger.web.response.MessageResponse
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/messages")
@RestController
class MessageController(
    val messageService: MessageService
) {

    @GetMapping(value = ["/recipient/{username}"])
    fun getByRecipient(@PathVariable("username") recipient: String?): List<MessageResponse?>? {
        return messageService.getAllByRecipient(recipient)
    }

    @PostMapping
    fun save(@RequestBody messageRequest: MessageRequest): MessageResponse? {
        return messageService.save(messageRequest)
    }

    @PostMapping("assistant")
    fun saveAssistantMessage(@RequestBody messageRequest: MessageToAssistantRequest): MessageResponse? {
        return messageService.saveAssistant(messageRequest)
    }

    @PostMapping("/group")
    fun saveToGroup(@RequestBody request: GroupMessageRequest): GroupMessageResponse? {
        return messageService.saveToGroup(request)
    }

    @GetMapping(value = ["/recipient/{recipient}/from/{sender}"])
    fun getByRecipientAndSender(
        @PathVariable("recipient") recipient: String?,
        @PathVariable("sender") sender: String?
    ): List<MessageResponse?>? {
        return messageService.getAllByRecipientAndSender(recipient, sender)
    }

    @GetMapping(value = ["/front-messages"])
    fun getFrontMessages(@RequestParam(value = "user") username: String): List<LastMessage?>? {
        return messageService.getLastMessagesByRecipient(username)
    }

    @GetMapping(value = ["/chat/{chatId}"])
    fun getMessagesByChat(
        @PathVariable("chatId") chatId: Int?,
        @RequestParam(value = "group", defaultValue = "false") isGroup: Boolean
    ): List<MessageResponse?>? {
        return if (isGroup) {
            messageService.getMessagesByChatId(chatId, "GROUP_CHAT")
        } else messageService.getMessagesByChatId(chatId, "PRIVATE_CHAT")
    }
}