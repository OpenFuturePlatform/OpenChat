package io.openfuture.openmessenger.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import java.io.IOException

class AwsCognitoTokenFilter(
    defaultFilterProcessesUrl: String?,
    authenticationManager: AuthenticationManager?,
    loginUrl: String?,
    signupUrl: String?,
    attachmentDownloadUrl: String?,
    allowedPages: List<String>,
) : AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {
    companion object{
        private val log = LoggerFactory.getLogger(AwsCognitoTokenFilter::class.java)
    }

    private val loginRequestMatcher: RequestMatcher = AntPathRequestMatcher(loginUrl)
    private val signupRequestMatcher: RequestMatcher = AntPathRequestMatcher(signupUrl)
    private val attachmentDownloadRequestMatcher: RequestMatcher = AntPathRequestMatcher(attachmentDownloadUrl)
    private val allowedPagesRequestMatchers: List<RequestMatcher> =
        allowedPages.map { AntPathRequestMatcher(it) }

    init {
        setAuthenticationManager(authenticationManager)
    }

    override fun requiresAuthentication(request: HttpServletRequest, response: HttpServletResponse): Boolean {
        return !loginRequestMatcher.matches(request) &&
                !signupRequestMatcher.matches(request) &&
                !attachmentDownloadRequestMatcher.matches(request) &&
                allowedPagesRequestMatchers.all { !it.matches(request) }
    }

    @Throws(AuthenticationException::class, IOException::class, ServletException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header == null || header.isEmpty() || header.length == 6) {
            log.info("Url=${request.requestURL} Token is not provided")
            throw AuthenticationServiceException("Token is missing")
        }
        val token = header.substring("Bearer ".length)
        return authenticationManager.authenticate(CognitoAuthenticationToken(token))
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)
        chain.doFilter(request, response)
    }
}