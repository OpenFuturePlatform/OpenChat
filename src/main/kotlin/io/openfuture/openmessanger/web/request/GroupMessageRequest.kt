package io.openfuture.openmessanger.web.request

import io.openfuture.openmessanger.repository.entity.MessageContentType

data class GroupMessageRequest(
    val sender: String? = null,
    val groupId: Int? = null,
    val contentType: MessageContentType? = null,
    val body: String? = null,
    val attachments: List<Int> = emptyList()
)