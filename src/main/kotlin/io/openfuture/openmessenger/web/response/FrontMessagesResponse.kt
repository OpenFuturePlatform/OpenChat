package io.openfuture.openmessenger.web.response

data class FrontMessagesResponse (
    var lastMessages: Collection<LastMessage>? = null
)