package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<User?, Int?> {
    fun findByEmail(email: String?): User?
}