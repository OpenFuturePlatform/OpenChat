package io.openfuture.openmessenger.service.dto

data class GetAllTodosRequest(
    val chatId: Int,
    val isGroup: Boolean,
)