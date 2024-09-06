package io.openfuture.openmessenger.service.firebase

enum class NotificationParameter(
    val value: String
) {
    SOUND("default"),
    COLOR("#FFFF00");
    companion object {
        fun fromInt(value: String) = entries.first { it.value == value }
    }
}