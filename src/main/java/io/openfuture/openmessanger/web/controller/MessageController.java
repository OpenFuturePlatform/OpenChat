package io.openfuture.openmessanger.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
