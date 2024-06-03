package io.openfuture.openmessenger.web.request.group

data class AddParticipantsRequest(
    var groupId: Int? = null,
    var users: List<String>? = null
)