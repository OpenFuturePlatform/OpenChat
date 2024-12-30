package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.PrivateChat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface PrivateChatRepository : JpaRepository<PrivateChat?, Int?> {
    @Query(
        "SELECT pc FROM PrivateChat pc " +
                "JOIN pc.chatParticipants cp1 " +
                "JOIN pc.chatParticipants cp2 " +
                "WHERE cp1.username = :sender AND cp2.username = :recipient " +
                "AND pc.type = 'DEFAULT'"
    )
    fun findPrivateChatByParticipants(
        @Param("sender") sender: String?,
        @Param("recipient") recipient: String?
    ): PrivateChat?

    @Query(
        "SELECT pc FROM PrivateChat pc " +
                "JOIN pc.chatParticipants cp " +
                "WHERE cp.username = :username AND pc.type = 'SELF' "
    )
    fun findSelfChat(username: String?): PrivateChat?
}