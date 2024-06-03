package io.openfuture.openmessenger.service

import io.openfuture.openmessenger.repository.entity.ChatParticipant

interface PrivateChatService {
    fun getOtherUser(username: String, chatId: Int?): ChatParticipant?
}