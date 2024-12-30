package io.openfuture.openmessenger.web.response

import java.time.LocalDateTime

data class LastMessage(
    var chatUid: String? = null,
    var group: Boolean? = false,
    var chatRoomName: String? = null,
    var memberCount: Int? = null,
    var displayUserName: String? = null,
    var lastMessageText: String? = null,
    var lastMessageTime: LocalDateTime? = null,
    var chatRoomPicture: String? = null
)