package io.openfuture.openmessanger.service;

import java.sql.Types;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import io.openfuture.openmessanger.web.response.UserMessageResponse;
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
        final MessageEntity message = new MessageEntity(request.getBody(), request.getSender(), request.getRecipient(), request.getContentType(), LocalDateTime.now());

        messageRepository.save(message);
        messagingTemplate.convertAndSendToUser(request.getRecipient(), "/direct", message);
    }

    @Override
    public MessageResponse save(MessageRequest request) {
        log.info("POST REQUEST {}", request);

        final MessageEntity message = new MessageEntity(request.getBody(), request.getSender(), request.getRecipient(), request.getContentType(), LocalDateTime.now());
        messageRepository.save(message);
        return new MessageResponse(message.getId(),
                message.getSender(),
                message.getRecipient(),
                message.getBody(),
                message.getContentType(),
                message.getReceivedAt());
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

    @Override
    public List<UserMessageResponse> getGroupRecipient(String recipient) {
        UserMessageResponse userMessageResponse1 = new UserMessageResponse("1","rasul", "https://gravatar.com/avatar/90f50f07004d8f08758cbf5cb1edb999?s=400&d=robohash&r=x", "Hello Me", LocalDateTime.now().minusDays(1));
        //UserMessageResponse userMessageResponse2 = new UserMessageResponse("2","beksultan", "https://gravatar.com/avatar/a15258729953efef537cedd260da3cde?s=400&d=robohash&r=x", "Hello Mister", LocalDateTime.now().minusDays(1));

        return List.of(userMessageResponse1);
    }

    private List<MessageResponse> convertToMessageResponse(final List<MessageEntity> messageEntities) {
        return messageEntities.stream()
                              .map(message -> new MessageResponse(message.getId(),
                                                                  message.getSender(),
                                                                  message.getRecipient(),
                                                                  message.getBody(),
                                                                  message.getContentType(),
                                                                  message.getReceivedAt()))
                              .toList();
    }

}
