package io.openfuture.openmessanger.repository.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "open_user")
class User(email: String? = null, firstName: String? = null, lastName: String? = null) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id = 0

    @Column(name = "email", unique = false)
    var email: String? = null

    @Column(name = "first_name")
    var firstName: String? = null

    @Column(name = "last_name")
    var lastName: String? = null

    @Column(name = "phone_number")
    val phoneNumber: String? = null

    @Column(name = "registered_at")
    val registeredAt: ZonedDateTime = ZonedDateTime.now()

    @Column(name = "last_login")
    val lastLogin: ZonedDateTime? = null

    @Column(name = "avatar")
    val avatar: String? = null

    @Column(name = "active")
    val active = true
}