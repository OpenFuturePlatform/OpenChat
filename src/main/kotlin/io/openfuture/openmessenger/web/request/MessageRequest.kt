package io.openfuture.openmessenger.web.request

import io.openfuture.openmessenger.repository.entity.MessageContentType

data class MessageRequest(
    val sender: String? = null,
    val recipient: String? = null,
    val contentType: MessageContentType? = null,
    val body: String? = null,
    val attachments: List<Int> = emptyList()
)