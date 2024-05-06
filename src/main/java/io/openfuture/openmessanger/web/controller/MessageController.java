package io.openfuture.openmessanger.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.openfuture.openmessanger.service.MessageService;
import io.openfuture.openmessanger.web.request.GroupMessageRequest;
import io.openfuture.openmessanger.web.request.MessageRequest;
import io.openfuture.openmessanger.web.request.MessageToAssistantRequest;
import io.openfuture.openmessanger.web.response.GroupMessageResponse;
import io.openfuture.openmessanger.web.response.LastMessage;
import io.openfuture.openmessanger.web.response.MessageResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/messages")
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping(value = "/recipient/{username}")
    public List<MessageResponse> getByRecipient(@PathVariable("username") String recipient) {
        return messageService.getAllByRecipient(recipient);
    }

    @PostMapping
    public MessageResponse save(@RequestBody MessageRequest messageRequest) {
        return messageService.save(messageRequest);
    }

    @PostMapping("assistant")
    public MessageResponse saveAssistantMessage(@RequestBody MessageToAssistantRequest messageRequest) {
        return messageService.saveAssistant(messageRequest);
    }

    @PostMapping("/group")
    public GroupMessageResponse saveToGroup(@RequestBody GroupMessageRequest request) {
        return messageService.saveToGroup(request);
    }

    @GetMapping(value = "/recipient/{recipient}/from/{sender}")
    public List<MessageResponse> getByRecipientAndSender(@PathVariable("recipient") String recipient,
                                                         @PathVariable("sender") String sender) {
        return messageService.getAllByRecipientAndSender(recipient, sender);
    }

    @GetMapping(value = "/front-messages")
    public List<LastMessage> getFrontMessages(@RequestParam(value = "user") String username) {
        return messageService.getLastMessagesByRecipient(username);
    }

    @GetMapping(value = "/chat/{chatId}")
    public List<MessageResponse> getMessagesByChat(@PathVariable("chatId") Integer chatId,
                                                   @RequestParam(value = "group", defaultValue = "false") Boolean isGroup) {
        if (isGroup) {
            return messageService.getMessagesByChatId(chatId, "GROUP_CHAT");
        }
        return messageService.getMessagesByChatId(chatId, "PRIVATE_CHAT");
    }

}
