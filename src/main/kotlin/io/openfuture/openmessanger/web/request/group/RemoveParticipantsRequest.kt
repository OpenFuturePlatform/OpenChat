package io.openfuture.openmessanger.web.request.group

data class RemoveParticipantsRequest(
    var groupId: Int? = null,
    var users: List<String>? = null
)