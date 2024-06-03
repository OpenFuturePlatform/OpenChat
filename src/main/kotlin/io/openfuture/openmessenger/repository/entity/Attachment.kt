package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "attachment")
class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var name: String? = null
    var url: String? = null
    var messageId: Int? = null
    var createdAt: LocalDateTime? = null
}