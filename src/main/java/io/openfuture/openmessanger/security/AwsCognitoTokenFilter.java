package io.openfuture.openmessanger.security;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AwsCognitoTokenFilter extends AbstractAuthenticationProcessingFilter {

    private final RequestMatcher loginRequestMatcher;
    private final RequestMatcher signupRequestMatcher;

    public AwsCognitoTokenFilter(final String defaultFilterProcessesUrl,
                                 final AuthenticationManager authenticationManager,
                                 final String loginUrl,
                                 final String signupUrl) {
        super(defaultFilterProcessesUrl);
        this.loginRequestMatcher = new AntPathRequestMatcher(loginUrl);
        this.signupRequestMatcher = new AntPathRequestMatcher(signupUrl);
        setAuthenticationManager(authenticationManager);
    }

    @Override
    protected boolean requiresAuthentication(final HttpServletRequest request, final HttpServletResponse response) {
        return !loginRequestMatcher.matches(request) && !signupRequestMatcher.matches(request);
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || header.isEmpty()) {
            log.info("Token is not provided");
            throw new AuthenticationServiceException("Token is missing");
        }
        final String token = header.substring("Bearer ".length());
        return getAuthenticationManager().authenticate(new CognitoAuthenticationToken(token));
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request,
                                            final HttpServletResponse response,
                                            final FilterChain chain,
                                            final Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }
}
