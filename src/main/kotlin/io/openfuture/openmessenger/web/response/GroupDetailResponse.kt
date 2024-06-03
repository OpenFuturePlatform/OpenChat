package io.openfuture.openmessenger.web.response

data class GroupDetailResponse(
    var id: Int? = null,
    var name: String? = null,
    var creator: String? = null,
    var avatar: String? = null,
    var participants: List<String>? = null
)