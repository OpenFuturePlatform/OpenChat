package io.openfuture.openmessanger.service;

import java.time.LocalDateTime;
import java.util.Collections;
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
import io.openfuture.openmessanger.repository.entity.User;
import io.openfuture.openmessanger.web.request.GroupMessageRequest;
import io.openfuture.openmessanger.web.request.MessageRequest;
import io.openfuture.openmessanger.web.response.GroupMessageResponse;
import io.openfuture.openmessanger.web.response.LastMessage;
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
    private final PrivateChatService privateChatService;
    private final UserService userService;

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
        if (request.getSender().equals(request.getRecipient())) {
            final Optional<PrivateChat> selfChat = privateChatRepository.findSelfChat(request.getSender());

            if (selfChat.isPresent()) {
                return selfChat.get();
            }

            final PrivateChat newPrivateChat = privateChatRepository.save(new PrivateChat("SELF"));
            final ChatParticipant singleParticipant = new ChatParticipant(newPrivateChat.getId(), request.getSender());
            chatParticipantRepository.save(singleParticipant);

            newPrivateChat.setChatParticipants(List.of(singleParticipant));
            return newPrivateChat;
        }

        final Optional<PrivateChat> privateChat = privateChatRepository.findPrivateChatByParticipants(request.getSender(), request.getRecipient());

        if (privateChat.isPresent()) {
            return privateChat.get();
        }

        final PrivateChat newPrivateChat = privateChatRepository.save(new PrivateChat("DEFAULT"));

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
                                   message.getPrivateChatId(),
                                   null);
    }

    @Override
    public GroupMessageResponse saveToGroup(GroupMessageRequest request) {
        final MessageEntity message = new MessageEntity(request.getBody(),
                                                        request.getSender(),
                                                        request.getContentType(),
                                                        LocalDateTime.now(),
                                                        request.getGroupId());
        messageRepository.save(message);
        return new GroupMessageResponse(message.getId(),
                                        message.getSender(),
                                        message.getBody(),
                                        message.getContentType(),
                                        message.getSentAt(),
                                        message.getGroupChatId());
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
    public List<LastMessage> getLastMessagesByRecipient(final String recipient) {
        final List<MessageEntity> messageEntities = messageRepository.findLastMessagesByUsername(recipient);
        final List<MessageResponse> messages = convertToMessageResponse(messageEntities);

        messageRepository.findGroupMessages();

        return messages.stream()
                       .map(m -> {
                                final ChatParticipant otherUser = privateChatService.getOtherUser(recipient, m.privateChatId());
                                final User user = userService.getByEmail(otherUser.getUsername());
                                return new LastMessage(String.valueOf(m.privateChatId()),
                                                       false,
                                                       otherUser.getUsername(),
                                                       0,
                                                       m.sender(),
                                                       m.content(),
                                                       m.sentAt(),
                                                       user.getAvatar());
                            }
                       )
                       .toList();
    }

    @Override
    public List<MessageResponse> getMessagesByChatId(final Integer chatId, final String type) {
        if (type.equals("PRIVATE_CHAT")) {
            return convertToMessageResponse(messageRepository.findByPrivateChatId(chatId));
        }

        if (type.equals("GROUP_CHAT")) {
            return convertToMessageResponse(messageRepository.findByGroupChatId(chatId));
        }

        return Collections.emptyList();
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
                                                                  message.getPrivateChatId(),
                                                                  message.getGroupChatId()))
                              .toList();
    }

}
