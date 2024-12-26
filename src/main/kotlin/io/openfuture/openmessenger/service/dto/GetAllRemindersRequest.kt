package io.openfuture.openmessenger.service.dto

data class GetAllRemindersRequest(
    val chatId: Int,
    val isGroup: Boolean,
)