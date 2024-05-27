package io.openfuture.openmessanger.web.response

data class CommonGroupsResponse(
    var email: String,
    var fullName: String,
    var groups: List<GroupInfo>
)