package io.openfuture.openmessanger.web.response

data class FrontMessagesResponse (
    var lastMessages: Collection<LastMessage>? = null
)