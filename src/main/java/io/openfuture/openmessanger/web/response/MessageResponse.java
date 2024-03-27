package io.openfuture.openmessanger.web.response;

import java.time.ZonedDateTime;

public record MessageResponse(
        int id,
        String sender,
        String recipient,
        ZonedDateTime receivedAt) {
}
