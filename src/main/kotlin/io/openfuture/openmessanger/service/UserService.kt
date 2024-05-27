package io.openfuture.openmessanger.service

import io.openfuture.openmessanger.repository.entity.User
import io.openfuture.openmessanger.web.request.user.UserDetailsRequest
import io.openfuture.openmessanger.web.response.UserDetailsResponse

interface UserService {
    fun getAllRecipientsBySender(username: String?): Collection<User?>?
    fun allUsers(): Collection<User?>
    fun getByEmail(email: String?): User?
    fun getUserDetails(request: UserDetailsRequest): UserDetailsResponse
}