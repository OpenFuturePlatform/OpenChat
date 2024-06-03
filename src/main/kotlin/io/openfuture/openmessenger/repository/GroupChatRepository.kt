package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.GroupChat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface GroupChatRepository : JpaRepository<GroupChat?, Int?> {
    fun findByNameAndCreator(name: String?, creator: String?): Optional<GroupChat?>

    @Query(
        value = "SELECT DISTINCT p.* FROM " +
                " group_chat p " +
                "JOIN group_participant c ON p.id = c.group_id " +
                "WHERE c.participant IN :names GROUP BY p.id HAVING COUNT(DISTINCT c.participant) = 2", nativeQuery = true
    )
    fun findAllByGroupParticipants_ParticipantContains(names: List<String?>?): List<GroupChat?>?
}