package io.openfuture.openmessanger.web.response;

import java.time.LocalDateTime;

public record MessageResponse(
        int id,
        String sender,
        String recipient,
        String content,
        LocalDateTime receivedAt) {
}
