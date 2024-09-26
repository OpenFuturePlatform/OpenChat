package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.repository.UserFirebaseTokenRepository
import io.openfuture.openmessenger.repository.entity.UserFireBaseToken
import io.openfuture.openmessenger.web.request.FirebaseTokenRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/token")
class UserFirebaseTokenController(
    val userFirebaseTokenRepository: UserFirebaseTokenRepository
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(UserFirebaseTokenController::class.java)
    }

    @PostMapping("/add")
    fun addToken(
        @RequestBody request: FirebaseTokenRequest,
    ): UserFireBaseToken? {
        println("Save FCM token request $request")
        val userFireBaseToken = UserFireBaseToken(request.userId, request.token)
        val fireBaseToken = userFirebaseTokenRepository.findFirstByUserId(request.userId)
        return if (fireBaseToken.isPresent) {
            fireBaseToken.get().apply {
                firebaseToken = request.token
                updatedAt = LocalDateTime.now()
            }.let { userFirebaseTokenRepository.save(it) }
        } else {
            userFirebaseTokenRepository.save(userFireBaseToken)
        }

    }
}