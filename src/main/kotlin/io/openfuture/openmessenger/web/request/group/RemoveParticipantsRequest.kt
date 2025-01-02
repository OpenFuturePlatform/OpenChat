package io.openfuture.openmessenger.web.request.group

data class RemoveParticipantsRequest(
    var groupId: Int? = null,
    var users: List<String>? = null
)