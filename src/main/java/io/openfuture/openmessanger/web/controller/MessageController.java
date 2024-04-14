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
import io.openfuture.openmessanger.web.response.FrontMessagesResponse;
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
    public FrontMessagesResponse getFrontMessages(@RequestParam(value = "user") String username) {
        final List<MessageResponse> messages = messageService.getLastMessagesByRecipient(username);
        final List<LastMessage> lastMessages = messages.stream()
                                                       .map(m -> new LastMessage(String.valueOf(m.privateChatId()),
                                                                                 false,
                                                                                 "chatRoomName",
                                                                                 0,
                                                                                 m.sender(),
                                                                                 m.content(),
                                                                                 m.sentAt(),
                                                                                 "")
                                                       )
                                                       .toList();
        return new FrontMessagesResponse(lastMessages);
    }

}
