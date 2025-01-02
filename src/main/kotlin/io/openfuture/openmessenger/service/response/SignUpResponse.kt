package io.openfuture.openmessenger.service.response

data class SignUpResponse(
    var message: String? = null,
    var data: Data? = null
)

data class Data(
    var email: String? = null,
    var firstName: String? = null,
    var lastName: String? = null
)