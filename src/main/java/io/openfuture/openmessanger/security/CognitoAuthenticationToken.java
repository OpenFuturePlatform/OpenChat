package io.openfuture.openmessanger.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CognitoAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final String token;

    public CognitoAuthenticationToken(final String token) {
        super(null);
        this.token = token;
        this.username = null;
    }

    public CognitoAuthenticationToken(final String username,
                                      final String token,
                                      final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        super.setAuthenticated(true);
        super.eraseCredentials();
        this.token = token;
        this.username = username;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }

}
