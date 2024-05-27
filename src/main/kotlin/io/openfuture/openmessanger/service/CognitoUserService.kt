package io.openfuture.openmessanger.service

import com.amazonaws.services.cognitoidp.model.*
import io.openfuture.openmessanger.service.dto.UserSignUpRequest
import java.util.*

interface CognitoUserService {
    fun initiateAuth(email: String, password: String): Optional<AdminInitiateAuthResult>
    fun respondToAuthChallenge(
        username: String?, newPassword: String?, session: String?
    ): Optional<AdminRespondToAuthChallengeResult>

    fun signOut(accessToken: String?): GlobalSignOutResult?
    fun getUserDetails(cognitoId: String?): AdminGetUserResult?
    fun forgotPassword(username: String?): ForgotPasswordResult?
    fun refreshAccessToken(userId: String?, refreshToken: String?): AuthenticationResultType?
    fun addUserToGroup(username: String?, groupName: String?)
    fun setUserPassword(username: String?, password: String?): AdminSetUserPasswordResult?
    fun signUp(request: UserSignUpRequest): UserType?
    fun respondToAuthSmsChallenge(
        username: String?, smsCode: String?, session: String?
    ): Optional<AdminRespondToAuthChallengeResult>

    fun getUserAuthEvents(username: String?, maxResult: Int, nextToken: String?): AdminListUserAuthEventsResult?
}