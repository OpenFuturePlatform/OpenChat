package io.openfuture.openmessanger.repository.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "open_user")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id = 0

    @Column(name = "email", unique = false)
    var email: String? = null

    @Column(name = "first_name")
    var firstName: String? = null

    @Column(name = "last_name")
    var lastName: String? = null

    @Column(name = "phone_number")
    private val phoneNumber: String? = null

    @Column(name = "registered_at")
    private val registeredAt: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "last_login")
    private val lastLogin: ZonedDateTime? = null

    @Column(name = "avatar")
    private val avatar: String? = null

    @Column(name = "active")
    private val active = true
}