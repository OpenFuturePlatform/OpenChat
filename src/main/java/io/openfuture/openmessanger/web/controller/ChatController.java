package io.openfuture.openmessanger.web.controller;

import java.util.UUID;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.openfuture.openmessanger.domain.Message;
import io.openfuture.openmessanger.domain.User;
import io.openfuture.openmessanger.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;

    @PostMapping("/send")
    public void greeting(Message message) {
        log.info("Received message {}", message);
        final User user = new User();
        user.setUsername(UUID.randomUUID().toString());
        messageService.sendMessage(user, "Messsaasssssge");
    }

}
