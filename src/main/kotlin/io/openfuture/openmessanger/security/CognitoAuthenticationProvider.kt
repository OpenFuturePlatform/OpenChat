package io.openfuture.openmessanger.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.*
import io.openfuture.openmessanger.security.CognitoAuthenticationToken
import lombok.SneakyThrows
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.math.BigInteger
import java.net.URL
import java.security.Key
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Slf4j
@Component
class CognitoAuthenticationProvider : AuthenticationProvider {
    @Value("\${jwks.url}")
    private val jwksUrl: String? = null
    @SneakyThrows
    @Throws(AuthenticationException::class)
    override fun authenticate(authentication: Authentication): Authentication {
        val token = authentication.credentials as String
        if (!StringUtils.hasText(token)) {
            throw BadCredentialsException("Token not provided")
        }
        val parser = Jwts.parser().keyLocator(MyKeyLocator(jwksUrl)).build()
        val claimsJws: Jws<Claims> = try {
            parser.parseSignedClaims(token)
        } catch (e: Exception) {
            throw BadCredentialsException("Unable to parse access token", e)
        }
        val username = claimsJws.payload.get("username", String::class.java)
        val scope = claimsJws.payload.get("scope", String::class.java)
        val simpleGrantedAuthority = SimpleGrantedAuthority(scope)
        return CognitoAuthenticationToken(username, token, listOf(simpleGrantedAuthority))
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return CognitoAuthenticationToken::class.java.isAssignableFrom(authentication)
    }

    class MyKeyLocator(private val jwksUrl: String?) : LocatorAdapter<Key?>() {
        @SneakyThrows
        override fun locate(header: ProtectedHeader): Key? {
            val keyId = header.keyId
            val objectMapper = jacksonObjectMapper()

            val jwksMap: Map<String, Any> = objectMapper.readValue(URL(jwksUrl))

            val keys = jwksMap["keys"] as List<Map<String, Any>>?

            // Find the key with the matching key ID (kid)
            for (key in keys!!) {
                val kid = key["kid"] as String?
                if (kid != null && kid == keyId) {
                    // Convert the key to a PublicKey (e.g., RSA)
                    val alg = key["alg"] as String?
                    val e = key["e"] as String?
                    val n = key["n"] as String?
                    val eBytes = Base64.getUrlDecoder().decode(e)
                    val nBytes = Base64.getUrlDecoder().decode(n)
                    val modulus = BigInteger(1, nBytes)
                    val exponent = BigInteger(1, eBytes)

                    // Construct RSA public key
                    val keySpec = RSAPublicKeySpec(modulus, exponent)
                    val keyFactory = KeyFactory.getInstance("RSA")
                    return keyFactory.generatePublic(keySpec)
                }
            }
            return null
        }
    }
}