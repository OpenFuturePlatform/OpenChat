package io.openfuture.openmessanger.service.impl

import io.openfuture.openmessanger.repository.ChatParticipantRepository
import io.openfuture.openmessanger.repository.entity.ChatParticipant
import io.openfuture.openmessanger.service.PrivateChatService
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class PrivateChatServiceImpl(
    val chatParticipantRepository: ChatParticipantRepository
) : PrivateChatService {

    override fun getOtherUser(username: String, chatId: Int?): ChatParticipant? {
        val participants = chatParticipantRepository.findAllByChatId(chatId)
        if (participants!!.size == 1) {
            return participants[0]
        }
        val recipient = participants.stream()
            .filter { chatParticipant: ChatParticipant? -> chatParticipant?.username != username }
            .findFirst()
        return recipient.orElseThrow { IllegalStateException("No other participants of chat") }
    }
}