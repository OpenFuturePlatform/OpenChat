package io.openfuture.openmessanger.web.response;

import java.time.LocalDateTime;

import io.openfuture.openmessanger.repository.entity.MessageContentType;

public record MessageResponse(
        int id,
        String sender,
        String recipient,
        String content,
        MessageContentType contentType,
        LocalDateTime receivedAt,
        LocalDateTime sentAt) {
}
