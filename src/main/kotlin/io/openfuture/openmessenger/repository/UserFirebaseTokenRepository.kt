package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.UserFireBaseToken
import org.springframework.data.jpa.repository.JpaRepository

interface UserFirebaseTokenRepository : JpaRepository<UserFireBaseToken, Long> {
    fun findAllByUserId(userId: String): List<UserFireBaseToken>
    fun existsByUserIdAndFirebaseToken(userId: String, fireBaseToken: String) : Boolean
}