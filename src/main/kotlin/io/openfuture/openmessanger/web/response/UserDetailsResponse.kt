package io.openfuture.openmessanger.web.response

data class UserDetailsResponse(
    var email: String? = null,
    var fullName: String? = null,
    var groups: List<GroupInfo>? = null
)
