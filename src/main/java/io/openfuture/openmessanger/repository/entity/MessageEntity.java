package io.openfuture.openmessanger.repository.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {
    int id;
    String body;
    String sender;
    String recipient;
    MessageContentType contentType;
    LocalDateTime receivedAt;
    LocalDateTime sentAt;
    Integer privateChatId;

    public MessageEntity(final String body,
                         final String sender,
                         final String recipient,
                         final MessageContentType contentType,
                         final LocalDateTime receivedAt,
                         final LocalDateTime sentAt,
                         final Integer privateChatId) {
        this.body = body;
        this.sender = sender;
        this.recipient = recipient;
        this.contentType = contentType;
        this.receivedAt = receivedAt;
        this.sentAt = sentAt;
        this.privateChatId = privateChatId;
    }
}
