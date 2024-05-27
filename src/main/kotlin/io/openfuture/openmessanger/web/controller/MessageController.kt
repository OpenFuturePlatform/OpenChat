package io.openfuture.openmessanger.web.controller

import io.openfuture.openmessanger.service.MessageService
import io.openfuture.openmessanger.web.request.GroupMessageRequest
import io.openfuture.openmessanger.web.request.MessageRequest
import io.openfuture.openmessanger.web.request.MessageToAssistantRequest
import io.openfuture.openmessanger.web.response.GroupMessageResponse
import io.openfuture.openmessanger.web.response.LastMessage
import io.openfuture.openmessanger.web.response.MessageResponse
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