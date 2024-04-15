package io.openfuture.openmessanger.service;

import io.openfuture.openmessanger.repository.entity.ChatParticipant;

public interface PrivateChatService {
    ChatParticipant getOtherUser(String username, Integer chatId);
}
