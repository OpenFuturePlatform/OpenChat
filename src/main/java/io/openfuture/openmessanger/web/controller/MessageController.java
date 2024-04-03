package io.openfuture.openmessanger.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.openfuture.openmessanger.service.MessageService;
import io.openfuture.openmessanger.web.request.MessageRequest;
import io.openfuture.openmessanger.web.response.MessageResponse;
import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/messages")
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping(value = "/recipient/{username}")
    public List<MessageResponse> getBySender(@PathVariable("username") String recipient) {
        return messageService.getAllByRecipient(recipient);
    }

    @PostMapping
    public MessageResponse save(@RequestBody MessageRequest messageRequest) {
        return messageService.save(messageRequest);
    }

    @GetMapping(value = "/recipient/{recipient}/from/{sender}")
    public List<MessageResponse> getByRecipientAndSender(@PathVariable("recipient") String recipient,
                                                         @PathVariable("sender") String sender) {
        return messageService.getAllByRecipientAndSender(recipient, sender);
    }

}
