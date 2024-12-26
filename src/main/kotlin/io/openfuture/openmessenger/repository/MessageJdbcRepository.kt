package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.Message
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MessageJdbcRepository : CrudRepository<Message, Int> {

    @Query("SELECT m FROM Message m WHERE m.privateChatId = :privateChatId AND m.sentAt BETWEEN :start AND :end")
    fun findByPrivateChatIdAndSentAtBetween(
        @Param("privateChatId") privateChatId: Int,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
        sort: Sort
    ): List<Message>

    @Query("""
       SELECT m FROM Message m WHERE m.groupChatId = :groupChatId AND m.sentAt BETWEEN :start AND :end 
    """)
    fun findByGroupChatIdAndSentAtBetween(
        @Param("groupChatId") groupChatId: Int,
        @Param("start") start: LocalDateTime,
        @Param("end") end: LocalDateTime,
        sort: Sort
    ): List<Message>

}