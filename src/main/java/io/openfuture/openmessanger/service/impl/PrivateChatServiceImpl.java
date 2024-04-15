package io.openfuture.openmessanger.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.openfuture.openmessanger.repository.ChatParticipantRepository;
import io.openfuture.openmessanger.repository.entity.ChatParticipant;
import io.openfuture.openmessanger.service.PrivateChatService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrivateChatServiceImpl implements PrivateChatService {

    private final ChatParticipantRepository chatParticipantRepository;

    @Override
    public ChatParticipant getOtherUser(final String username, final Integer chatId) {
        final List<ChatParticipant> participants = chatParticipantRepository.findAllByChatId(chatId);
        if (participants.size() == 1) {
            return participants.get(0);
        }
        final Optional<ChatParticipant> recipient = participants.stream()
                                                                .filter(chatParticipant -> !chatParticipant.getUsername().equals(username))
                                                                .findFirst();
        return recipient.orElseThrow(() -> new IllegalStateException("No other participants of chat"));
    }

}
