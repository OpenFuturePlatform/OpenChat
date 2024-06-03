package io.openfuture.openmessenger.service

import io.openfuture.openmessenger.repository.entity.User
import io.openfuture.openmessenger.web.request.user.UserDetailsRequest
import io.openfuture.openmessenger.web.response.UserDetailsResponse

interface UserService {
    fun getAllRecipientsBySender(username: String?): Collection<User?>?
    fun allUsers(): Collection<User?>
    fun getByEmail(email: String?): User?
    fun getUserDetails(request: UserDetailsRequest): UserDetailsResponse
}