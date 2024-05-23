package io.openfuture.openmessanger.web.request

import io.openfuture.openmessanger.repository.entity.MessageContentType
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

data class MessageRequest(
    val sender: String? = null,
    val recipient: String? = null,
    val contentType: MessageContentType? = null,
    val body: String? = null,
    val attachments: List<Int> = emptyList()
)