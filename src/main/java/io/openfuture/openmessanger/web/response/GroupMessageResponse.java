package io.openfuture.openmessanger.web.response;

import java.time.LocalDateTime;

import io.openfuture.openmessanger.repository.entity.MessageContentType;

public record GroupMessageResponse(
        int id,
        String sender,
        String content,
        MessageContentType contentType,
        LocalDateTime sentAt,
        Integer groupChatId) {
}
