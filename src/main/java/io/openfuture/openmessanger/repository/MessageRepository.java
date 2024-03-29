package io.openfuture.openmessanger.repository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import io.openfuture.openmessanger.repository.entity.MessageEntity;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MessageRepository {

    private final NamedParameterJdbcOperations jdbcOperations;

    public List<MessageEntity> findByRecipient(final String recipient) {

        final String sql = """
                select m.id, m.body, m.sender, m.recipient, m.received_at
                    from message m
                where m.recipient = :recipient
                order by received_at desc
                """;
        final MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("recipient", recipient);

        return jdbcOperations.query(sql,
                                    parameterSource,
                                    selectMapper());
    }

    public List<MessageEntity> findByRecipientAndSender(final String recipient, final String sender) {

        final String sql = """
                select m.id, m.body, m.sender, m.recipient, m.received_at
                    from message m
                where m.recipient = :recipient and m.sender = :sender
                order by received_at desc
                """;
        final MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("recipient", recipient)
                .addValue("sender", sender);

        return jdbcOperations.query(sql,
                                    parameterSource,
                                    selectMapper());
    }

    public void save(final MessageEntity message) {
        final String sql = """
                INSERT INTO message(body, sender, recipient, received_at)
                VALUES (:body, :sender, :recipient, :receivedAt)
                """;

        final MapSqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("body", message.getBody())
                .addValue("sender", message.getSender())
                .addValue("recipient", message.getRecipient())
                .addValue("receivedAt", message.getReceivedAt());

        jdbcOperations.update(sql, parameterSource);
    }

    private static RowMapper<MessageEntity> selectMapper() {
        return (rs, rowNum) -> {
            final ZonedDateTime receivedAt = rs.getTimestamp("received_at")
                                               .toLocalDateTime()
                                               .atZone(ZoneId.systemDefault());

            return new MessageEntity(rs.getInt("id"),
                                     rs.getString("body"),
                                     rs.getString("sender"),
                                     rs.getString("recipient"),
                                     receivedAt);
        };
    }

}
