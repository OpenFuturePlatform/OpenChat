package io.openfuture.openmessenger.web.request

import io.openfuture.openmessenger.repository.entity.MessageContentType

data class MessageToAssistantRequest(
    val sender: String? = null,
    val contentType: MessageContentType? = null,
    val body: String? = null
)