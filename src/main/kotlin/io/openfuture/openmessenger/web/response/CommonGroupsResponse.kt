package io.openfuture.openmessenger.web.response

data class CommonGroupsResponse(
    var email: String,
    var fullName: String,
    var groups: List<GroupInfo>
)