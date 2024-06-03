package io.openfuture.openmessenger.service.impl

import io.openfuture.openmessenger.repository.MessageRepository
import io.openfuture.openmessenger.repository.UserJpaRepository
import io.openfuture.openmessenger.repository.entity.GroupChat
import io.openfuture.openmessenger.repository.entity.User
import io.openfuture.openmessenger.service.GroupChatService
import io.openfuture.openmessenger.service.UserService
import io.openfuture.openmessenger.web.request.user.UserDetailsRequest
import io.openfuture.openmessenger.web.response.GroupInfo
import io.openfuture.openmessenger.web.response.UserDetailsResponse
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class UserServiceImpl(
    val userJpaRepository: UserJpaRepository,
    val messageRepository: MessageRepository,
    val groupChatService: GroupChatService
) : UserService {

    override fun getAllRecipientsBySender(username: String?): Collection<User?>? {
        val recipients = messageRepository.findRecipientsBySender(username)
        return recipients.stream().map { email: String? -> userJpaRepository.findByEmail(email) }.toList()
    }

    override fun allUsers(): Collection<User?> {
        return userJpaRepository.findAll()
    }

    override fun getByEmail(email: String?): User? {
        return userJpaRepository.findByEmail(email)
    }

    override fun getUserDetails(request: UserDetailsRequest): UserDetailsResponse {
        val commonGroups = groupChatService.findCommonGroups(request.email, request.username)
        val user = userJpaRepository.findByEmail(request.email)
        val groups = commonGroups!!.stream().map { groupChat: GroupChat? -> GroupInfo(groupChat?.id, groupChat?.name) }
            .toList()
        return UserDetailsResponse(request.email, "", groups)
    }
}