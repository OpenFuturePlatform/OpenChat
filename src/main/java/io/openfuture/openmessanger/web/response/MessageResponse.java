package io.openfuture.openmessanger.web.response;

import io.openfuture.openmessanger.repository.entity.MessageContentType;

import java.time.LocalDateTime;

public record MessageResponse(
        int id,
        String sender,
        String recipient,
        String content,
        MessageContentType contentType,
        LocalDateTime receivedAt) {
}
