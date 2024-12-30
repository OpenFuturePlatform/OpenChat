package io.openfuture.openmessenger.web.request.group

data class CreateGroupRequest(
    var name: String? = null,
    var creator: String? = null,
    var participants: List<String> = ArrayList()
)