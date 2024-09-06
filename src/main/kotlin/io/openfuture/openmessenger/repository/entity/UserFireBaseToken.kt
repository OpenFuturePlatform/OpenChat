package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_firebase_tokens")
class UserFireBaseToken() {
    constructor(
        userId: String,
        token: String
    ) : this() {
        this.userId = userId
        this.firebaseToken = token
        this.createdAt = LocalDateTime.now()
        this.updatedAt = LocalDateTime.now()
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var createdAt: LocalDateTime = LocalDateTime.now()
    var updatedAt: LocalDateTime = LocalDateTime.now()
    var userId: String? = null
    var firebaseToken: String? = null

}