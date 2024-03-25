package io.openfuture.openmessanger.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import io.openfuture.openmessanger.domain.Message;
import io.openfuture.openmessanger.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessage(final User sender, final String content) {
        Message message = new Message();
        message.setSender(sender.getUsername());
        message.setBody(content);
        log.info("Sent message {}", message);

        messagingTemplate.convertAndSend("/topic/chat", message);
    }

}
