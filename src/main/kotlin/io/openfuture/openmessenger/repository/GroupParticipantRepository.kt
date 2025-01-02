package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.GroupChat
import io.openfuture.openmessenger.repository.entity.GroupParticipant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface GroupParticipantRepository : JpaRepository<GroupParticipant?, Int?> {
    fun findAllByParticipantAndGroupChat_Id(username: String?, groupId: Int?): Optional<GroupParticipant?>
    fun findAllByGroupChat(groupChat: GroupChat?): List<GroupParticipant?>
    fun findAllByGroupChatAndDeleted(groupChat: GroupChat?, deleted: Boolean): List<GroupParticipant?>
}