package io.openfuture.openmessanger.security

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class CognitoAuthenticationToken : AbstractAuthenticationToken {
    private val username: String?
    private val token: String

    constructor(token: String) : super(null) {
        this.token = token
        username = null
    }

    constructor(
        username: String,
        token: String,
        authorities: Collection<GrantedAuthority?>?
    ) : super(authorities) {
        super.setAuthenticated(true)
        super.eraseCredentials()
        this.token = token
        this.username = username
    }

    override fun getCredentials(): Any {
        return token
    }

    override fun getPrincipal(): Any? {
        return username
    }
}