package io.openfuture.openmessenger.web.response

import io.openfuture.openmessenger.repository.entity.MessageContentType
import java.time.LocalDateTime
import java.time.ZonedDateTime

data class GroupMessageResponse(var id: Int? = null,
                                var sender: String? = null,
                                var content: String? = null,
                                var contentType: MessageContentType? = null,
                                var sentAt: LocalDateTime? = null,
                                var groupChatId: Int? = null) {
    private var privateChatId: Int? = null
    private var receivedAt: ZonedDateTime? = null
    private var recipient: String? = null
    private var email: String? = null
    private var username: String? = null

}