package io.openfuture.openmessenger.web.response

data class UserDetailsResponse(
    var email: String? = null,
    var fullName: String? = null,
    var groups: List<GroupInfo>? = null
)
