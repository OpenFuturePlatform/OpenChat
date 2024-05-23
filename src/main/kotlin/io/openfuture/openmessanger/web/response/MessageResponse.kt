package io.openfuture.openmessanger.web.response

import io.openfuture.openmessanger.repository.entity.MessageContentType
import java.time.LocalDateTime

data class MessageResponse(
    var id: Int,
    var sender: String,
    var recipient: String,
    var content: String,
    var contentType: MessageContentType,
    var receivedAt: LocalDateTime,
    var sentAt: LocalDateTime,
    var privateChatId: Int,
    var groupChatId: Int?,
    var attachments: List<Int> = emptyList()
)