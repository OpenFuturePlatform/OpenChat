package io.openfuture.openmessanger.repository.entity;

import java.time.ZonedDateTime;

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
    ZonedDateTime receivedAt;

    public MessageEntity(String body,
                         String sender,
                         String recipient,
                         ZonedDateTime receivedAt) {
        this.body = body;
        this.sender = sender;
        this.recipient = recipient;
        this.receivedAt = receivedAt;
    }
}
