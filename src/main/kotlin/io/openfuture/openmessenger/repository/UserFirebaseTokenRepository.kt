package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.UserFireBaseToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserFirebaseTokenRepository : JpaRepository<UserFireBaseToken, Long> {
    fun findAllByUserId(userId: String): List<UserFireBaseToken>
    fun findFirstByUserId(userId: String): Optional<UserFireBaseToken>
}