package io.openfuture.openmessenger.service.dto

class PushNotificationRequest(
    var title: String? = null,
    val message: String,
    val topic: String,
    val token: String? = null
)