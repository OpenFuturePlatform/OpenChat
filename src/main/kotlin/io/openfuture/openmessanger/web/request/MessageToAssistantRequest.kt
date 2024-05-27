package io.openfuture.openmessanger.web.request

import io.openfuture.openmessanger.repository.entity.MessageContentType

data class MessageToAssistantRequest(
    val sender: String? = null,
    val contentType: MessageContentType? = null,
    val body: String? = null
)