package io.openfuture.openmessanger.service

import io.openfuture.openmessanger.repository.entity.ChatParticipant

interface PrivateChatService {
    fun getOtherUser(username: String, chatId: Int?): ChatParticipant?
}