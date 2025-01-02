package io.openfuture.openmessenger.web.request

import io.openfuture.openmessenger.repository.entity.MessageContentType

data class GroupMessageRequest(
    val sender: String? = null,
    val groupId: Int? = null,
    val contentType: MessageContentType? = null,
    val body: String? = null,
    val attachments: List<Int> = emptyList()
)