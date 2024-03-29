package io.openfuture.openmessanger.web.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import io.openfuture.openmessanger.service.MessageService;
import io.openfuture.openmessanger.web.request.MessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;

    @MessageMapping("/direct-message")
    public void sendMessage(@Payload MessageRequest request) {
        log.info("REQUEST {}", request);
        messageService.sendMessage(request);
    }

}