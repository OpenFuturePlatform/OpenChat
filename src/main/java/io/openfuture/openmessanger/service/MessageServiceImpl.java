package io.openfuture.openmessanger.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.openfuture.openmessanger.repository.MessageRepository;
import io.openfuture.openmessanger.repository.entity.MessageEntity;
import io.openfuture.openmessanger.web.request.MessageRequest;
import io.openfuture.openmessanger.web.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessage(final MessageRequest request) {
        final MessageEntity message = new MessageEntity(request.getBody(), request.getSender(), request.getRecipient(), ZonedDateTime.now());

        messageRepository.save(message);
        messagingTemplate.convertAndSendToUser(request.getRecipient(), "/direct", message);
    }

    @Override
    public List<MessageResponse> getAllByRecipient(final String recipient) {
        final List<MessageEntity> messageEntities = messageRepository.findByRecipient(recipient);

        return convertToMessageResponse(messageEntities);
    }

    @Override
    public List<MessageResponse> getAllByRecipientAndSender(final String recipient, final String sender) {
        final List<MessageEntity> messageEntities = messageRepository.findByRecipientAndSender(recipient, sender);

        return convertToMessageResponse(messageEntities);
    }

    private List<MessageResponse> convertToMessageResponse(final List<MessageEntity> messageEntities) {
        return messageEntities.stream()
                              .map(message -> new MessageResponse(message.getId(),
                                                                  message.getSender(),
                                                                  message.getRecipient(),
                                                                  message.getReceivedAt()))
                              .toList();
    }

}
