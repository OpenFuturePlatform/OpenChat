package io.openfuture.openmessanger.web.controller;

import java.time.ZonedDateTime;
import java.util.List;

import io.openfuture.openmessanger.repository.entity.MessageEntity;
import io.openfuture.openmessanger.web.request.MessageRequest;
import org.springframework.web.bind.annotation.*;

import io.openfuture.openmessanger.service.MessageService;
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
