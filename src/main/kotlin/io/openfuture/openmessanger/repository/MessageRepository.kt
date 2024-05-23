package io.openfuture.openmessanger.repository

import io.openfuture.openmessanger.repository.entity.MessageContentType
import io.openfuture.openmessanger.repository.entity.MessageEntity
import lombok.RequiredArgsConstructor
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
@RequiredArgsConstructor
class MessageRepository(
    private val jdbcOperations: NamedParameterJdbcOperations
) {
    fun findByRecipient(recipient: String?): List<MessageEntity> {
        val sql = """
                select
                m.id,
                m.private_chat_id,
                m.body,
                m.sender,
                m.recipient,
                m.received_at,
                m.content_type,
                m.sent_at
                    from message m
                where m.recipient = :recipient
                order by received_at
                
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("recipient", recipient)
        return jdbcOperations!!.query(
            sql,
            parameterSource,
            privateMessageSelectMapper()
        )
    }

    fun findLastMessagesByUsername(username: String?): List<MessageEntity> {
        val sql = """
                with ordered as (select id,
                                        private_chat_id,
                                        body,
                                        sent_at,
                                        received_at,
                                        sent_at,
                                        sender,
                                        recipient,
                                        content_type,
                                        row_number() over (partition by private_chat_id order by sent_at desc ) last_message
                                 from message mes
                                 where mes.private_chat_id in (select pc.id
                                                               from private_chat pc
                                                                        join chat_participant cp on pc.id = cp.chat_id
                                                               where cp."username" = :user))
                                select *
                                from ordered
                                where last_message = 1
                
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("user", username)
        return jdbcOperations!!.query(
            sql,
            parameterSource,
            privateMessageSelectMapper()
        )
    }

    fun findByRecipientAndSender(recipient: String?, sender: String?): List<MessageEntity> {
        val sql = """
                select
                m.id,
                m.private_chat_id,
                m.body,
                m.sender,
                m.recipient,
                m.received_at,
                m.sent_at,
                m.content_type
                    from message m
                where (m.recipient = :recipient and m.sender = :sender) or (m.recipient = :sender and m.sender = :recipient)
                order by m.id asc
                
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("recipient", recipient)
            .addValue("sender", sender)
        return jdbcOperations!!.query(
            sql,
            parameterSource,
            privateMessageSelectMapper()
        )
    }

    fun findRecipientsBySender(sender: String?): List<String> {
        val sql = """
                select distinct m.recipient
                    from message m
                where m.sender = :sender
                
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("sender", sender)
        return jdbcOperations!!.query(
            sql,
            parameterSource
        ) { rs: ResultSet, rowNum: Int -> rs.getString("recipient") }
    }

    fun findByPrivateChatId(privateChatId: Int?): List<MessageEntity> {
        val sql = """
                select
                m.id,
                m.private_chat_id,
                m.body,
                m.sender,
                m.recipient,
                m.received_at,
                m.content_type,
                m.sent_at
                    from message m
                where m.private_chat_id = :privateChatId
                order by received_at
                
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("privateChatId", privateChatId)
        return jdbcOperations!!.query(
            sql,
            parameterSource,
            privateMessageSelectMapper()
        )
    }

    fun findByGroupChatId(groupChatId: Int?): List<MessageEntity> {
        val sql = """
                select
                m.id,
                m.group_chat_id,
                m.body,
                m.sender,
                m.content_type,
                m.sent_at
                    from message m
                where m.group_chat_id = :groupChatId
                order by received_at
                
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("groupChatId", groupChatId)
        return jdbcOperations!!.query(
            sql,
            parameterSource,
            groupMessageSelectMapper()
        )
    }

    fun save(message: MessageEntity) {
        val sql = """
                INSERT INTO message(private_chat_id, group_chat_id, body, content_type, sender, recipient, received_at, sent_at)
                VALUES (:privateChatId, :groupChatId, :body, :content_type, :sender, :recipient, :receivedAt, :sentAt)
                
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("privateChatId", message.privateChatId)
            .addValue("groupChatId", message.groupChatId)
            .addValue("body", message.body)
            .addValue("sender", message.sender)
            .addValue("recipient", message.recipient)
            .addValue("content_type", message.contentType.name)
            .addValue("receivedAt", message.receivedAt)
            .addValue("sentAt", message.sentAt)
        jdbcOperations!!.update(sql, parameterSource)
    }

    fun findGroupMessages(username: String?): List<MessageEntity> {
        val sql = """
                with ordered
                         as (select id,
                                    group_chat_id,
                                    body,
                                    sent_at,
                                    sender,
                                    content_type,
                                    row_number() over (partition by group_chat_id order by sent_at desc ) last_message
                             from message mes
                             where mes.group_chat_id in
                                   (select pc.id
                                    from group_chat pc
                                             join group_participant cp on pc.id = cp.group_id and cp.deleted is false
                                    where cp.participant = :user))
                select *
                from ordered
                where last_message = 1
                
                """.trimIndent()
        val parameterSource = MapSqlParameterSource()
            .addValue("user", username)
        return jdbcOperations!!.query(
            sql,
            parameterSource,
            groupMessageSelectMapper()
        )
    }

    companion object {
        private fun privateMessageSelectMapper(): RowMapper<MessageEntity> {
            return RowMapper { rs: ResultSet, rowNum: Int ->
                val receivedAt = rs.getTimestamp("received_at")
                    .toLocalDateTime()
                val sentAt = rs.getTimestamp("sent_at")
                    .toLocalDateTime()
                MessageEntity(
                    rs.getInt("id"),
                    rs.getString("body"),
                    rs.getString("sender"),
                    rs.getString("recipient"),
                    MessageContentType.valueOf(rs.getString("content_type")),
                    receivedAt,
                    sentAt,
                    rs.getInt("private_chat_id")
                )
            }
        }

        private fun groupMessageSelectMapper(): RowMapper<MessageEntity> {
            return RowMapper { rs: ResultSet, rowNum: Int ->
                val sentAt = rs.getTimestamp("sent_at")
                    .toLocalDateTime()
                MessageEntity(
                    rs.getInt("id"),
                    rs.getString("body"),
                    rs.getString("sender"),
                    MessageContentType.valueOf(rs.getString("content_type")),
                    sentAt,
                    rs.getInt("group_chat_id")
                )
            }
        }
    }
}