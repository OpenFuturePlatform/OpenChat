package io.openfuture.openmessanger.web.response

import java.time.LocalDateTime

data class UserMessageResponse(
    var uniqueId: String? = null,
    var recipientName: String? = null,
    var recipientAvatarUrl: String? = null,
    var lastMessage: String? = null,
    var lastAt: LocalDateTime? = null
)