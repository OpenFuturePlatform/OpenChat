package io.openfuture.openmessanger.security;

import java.math.BigInteger;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.LocatorAdapter;
import io.jsonwebtoken.ProtectedHeader;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CognitoAuthenticationProvider implements AuthenticationProvider {

    @Value("${jwks.url}")
    private String jwksUrl;

    @SneakyThrows
    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        final JwtParser parser = Jwts.parser().keyLocator(new MyKeyLocator(jwksUrl)).build();
        final Jws<Claims> claimsJws;
        try {
            claimsJws = parser.parseSignedClaims(token);
        } catch (Exception e) {
            throw new BadCredentialsException("Unable to parse access token", e);
        }

        final String username = claimsJws.getPayload().get("username", String.class);
        final String scope = claimsJws.getPayload().get("scope", String.class);
        final SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(scope);

        return new CognitoAuthenticationToken(username, token, List.of(simpleGrantedAuthority));
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return CognitoAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public static class MyKeyLocator extends LocatorAdapter<Key> {

        private final String jwksUrl;

        public MyKeyLocator(final String jwksUrl) {
            this.jwksUrl = jwksUrl;
        }

        @SneakyThrows
        @Override
        protected Key locate(final ProtectedHeader header) {
            final String keyId = header.getKeyId();

            final ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> jwksMap = objectMapper.readValue(new URL(jwksUrl), Map.class);

            // Extract the keys array from the JWKS
            List<Map<String, Object>> keys = (List<Map<String, Object>>) jwksMap.get("keys");

            // Find the key with the matching key ID (kid)
            for (Map<String, Object> key : keys) {
                String kid = (String) key.get("kid");
                if (kid != null && kid.equals(keyId)) {
                    // Convert the key to a PublicKey (e.g., RSA)
                    String alg = (String) key.get("alg");
                    String e = (String) key.get("e");
                    String n = (String) key.get("n");
                    byte[] eBytes = Base64.getUrlDecoder().decode(e);
                    byte[] nBytes = Base64.getUrlDecoder().decode(n);

                    BigInteger modulus = new BigInteger(1, nBytes);
                    BigInteger exponent = new BigInteger(1, eBytes);

                    // Construct RSA public key
                    RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    return keyFactory.generatePublic(keySpec);
                }
            }
            return null;
        }
    }

}
