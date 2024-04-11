package io.openfuture.openmessanger.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.openfuture.openmessanger.repository.ChatParticipantRepository;
import io.openfuture.openmessanger.repository.MessageRepository;
import io.openfuture.openmessanger.repository.PrivateChatRepository;
import io.openfuture.openmessanger.repository.entity.ChatParticipant;
import io.openfuture.openmessanger.repository.entity.MessageEntity;
import io.openfuture.openmessanger.repository.entity.PrivateChat;
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
    private final PrivateChatRepository privateChatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatParticipantRepository chatParticipantRepository;

    @Override
    public void sendMessage(final MessageRequest request) {
        final PrivateChat privateChat = getPrivateChat(request);
        final MessageEntity message = new MessageEntity(request.getBody(),
                                                        request.getSender(),
                                                        request.getRecipient(),
                                                        request.getContentType(),
                                                        LocalDateTime.now(),
                                                        LocalDateTime.now(),
                                                        privateChat.getId());

        messageRepository.save(message);
        messagingTemplate.convertAndSendToUser(request.getRecipient(), "/direct", message);
    }

    private PrivateChat getPrivateChat(final MessageRequest request) {
        final Optional<PrivateChat> privateChat = privateChatRepository.findPrivateChatByParticipants(request.getSender(), request.getSender());

        if (privateChat.isPresent()) {
            return privateChat.get();
        }

        final PrivateChat newPrivateChat = privateChatRepository.save(new PrivateChat());

        final ChatParticipant sender = new ChatParticipant(newPrivateChat.getId(), request.getSender());
        final ChatParticipant recipient = new ChatParticipant(newPrivateChat.getId(), request.getRecipient());

        chatParticipantRepository.save(sender);
        chatParticipantRepository.save(recipient);

        newPrivateChat.setChatParticipants(List.of(sender, recipient));
        return newPrivateChat;
    }

    @Override
    public MessageResponse save(MessageRequest request) {
        log.info("POST REQUEST {}", request);

        final PrivateChat privateChat = getPrivateChat(request);

        final MessageEntity message = new MessageEntity(request.getBody(),
                                                        request.getSender(),
                                                        request.getRecipient(),
                                                        request.getContentType(),
                                                        LocalDateTime.now(),
                                                        LocalDateTime.now(),
                                                        privateChat.getId());
        messageRepository.save(message);
        return new MessageResponse(message.getId(),
                                   message.getSender(),
                                   message.getRecipient(),
                                   message.getBody(),
                                   message.getContentType(),
                                   message.getReceivedAt(),
                                   message.getSentAt(),
                                   message.getPrivateChatId());
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
    public List<MessageResponse> getLastMessagesByRecipient(final String recipient) {
        final List<MessageEntity> messageEntities = messageRepository.findLastMessagesByUsername(recipient);
        return convertToMessageResponse(messageEntities);
    }

    private List<MessageResponse> convertToMessageResponse(final List<MessageEntity> messageEntities) {
        return messageEntities.stream()
                              .map(message -> new MessageResponse(message.getId(),
                                                                  message.getSender(),
                                                                  message.getRecipient(),
                                                                  message.getBody(),
                                                                  message.getContentType(),
                                                                  message.getReceivedAt(),
                                                                  message.getSentAt(),
                                                                  message.getPrivateChatId()))
                              .toList();
    }

}
