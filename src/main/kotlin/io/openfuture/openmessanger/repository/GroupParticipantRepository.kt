package io.openfuture.openmessanger.repository

import io.openfuture.openmessanger.repository.entity.GroupChat
import io.openfuture.openmessanger.repository.entity.GroupParticipant
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface GroupParticipantRepository : JpaRepository<GroupParticipant?, Int?> {
    fun findAllByParticipantAndGroupChat_Id(username: String?, groupId: Int?): Optional<GroupParticipant?>
    fun findAllByGroupChat(groupChat: GroupChat?): List<GroupParticipant?>
    fun findAllByGroupChatAndDeleted(groupChat: GroupChat?, deleted: Boolean): List<GroupParticipant?>
}