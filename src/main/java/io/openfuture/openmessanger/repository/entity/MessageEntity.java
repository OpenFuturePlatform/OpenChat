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

    public MessageEntity(String body,
                         String sender,
                         String recipient,
                         MessageContentType contentType,
                         LocalDateTime receivedAt,
                         LocalDateTime sentAt) {
        this.body = body;
        this.sender = sender;
        this.contentType = contentType;
        this.recipient = recipient;
        this.receivedAt = receivedAt;
        this.sentAt = sentAt;
    }
}
