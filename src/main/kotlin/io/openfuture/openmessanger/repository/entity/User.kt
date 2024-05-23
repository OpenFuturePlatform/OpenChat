package io.openfuture.openmessanger.repository.entity

import jakarta.persistence.*
import java.time.ZonedDateTime
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "open_user")
class User(email: @NotBlank @NotNull @Email String?, firstName: @NotBlank @NotNull String?, lastName: String?) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id = 0

    private val email: String? = null

    private val firstName: String? = null

    private val lastName: String? = null

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