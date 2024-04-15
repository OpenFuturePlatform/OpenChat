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
    Integer groupChatId;

    public MessageEntity(final int id,
                         final String body,
                         final String sender,
                         final String recipient,
                         final MessageContentType contentType,
                         final LocalDateTime receivedAt,
                         final LocalDateTime sentAt,
                         final Integer privateChatId) {
        this.id = id;
        this.body = body;
        this.sender = sender;
        this.recipient = recipient;
        this.contentType = contentType;
        this.receivedAt = receivedAt;
        this.sentAt = sentAt;
        this.privateChatId = privateChatId;
    }
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
    public MessageEntity(final String body,
                         final String sender,
                         final MessageContentType contentType,
                         final LocalDateTime sentAt,
                         final Integer groupChat) {
        this.body = body;
        this.sender = sender;
        this.contentType = contentType;
        this.sentAt = sentAt;
        this.groupChatId = groupChat;
    }
    public MessageEntity(final int id,
                         final String body,
                         final String sender,
                         final MessageContentType contentType,
                         final LocalDateTime sentAt,
                         final Integer groupChat) {
        this.id = id;
        this.body = body;
        this.sender = sender;
        this.recipient = "";//mute errors on client
        this.receivedAt = LocalDateTime.now();//mute errors on client
        this.contentType = contentType;
        this.sentAt = sentAt;
        this.groupChatId = groupChat;
    }
}
