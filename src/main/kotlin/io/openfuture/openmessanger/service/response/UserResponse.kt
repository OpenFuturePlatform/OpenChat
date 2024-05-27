package io.openfuture.openmessanger.service.response

data class UserResponse(
    var id: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var orgId: String? = null,
    var modifiedTime: String? = null,
    var role: String? = null
)