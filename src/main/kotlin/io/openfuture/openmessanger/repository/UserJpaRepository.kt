package io.openfuture.openmessanger.repository

import io.openfuture.openmessanger.repository.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<User?, Int?> {
    fun findByEmail(email: String?): User?
}