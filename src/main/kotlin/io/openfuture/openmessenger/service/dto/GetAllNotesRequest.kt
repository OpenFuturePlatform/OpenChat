package io.openfuture.openmessenger.service.dto

data class GetAllNotesRequest(
    val chatId: Int,
    val isGroup: Boolean,
)