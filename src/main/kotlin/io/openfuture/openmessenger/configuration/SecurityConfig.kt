package io.openfuture.openmessenger.configuration

import io.openfuture.openmessenger.security.AwsCognitoTokenFilter
import io.openfuture.openmessenger.security.CognitoAuthenticationProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val cognitoAuthenticationProvider: CognitoAuthenticationProvider
) {
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authenticationManagerBuilder.authenticationProvider(cognitoAuthenticationProvider)
        val authenticationManager = authenticationManagerBuilder.build()
        return http
            .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { session: SessionManagementConfigurer<HttpSecurity?> -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/public/login").permitAll()
                it.requestMatchers("/api/v1/public/signup").permitAll()
                it.requestMatchers("/api/v1/attachments/download/**").permitAll()
                it.requestMatchers("/*").permitAll()
                it.requestMatchers("/webjars/**").permitAll()
                it.requestMatchers("/js/*").permitAll()
                it.requestMatchers("/img/*").permitAll()
                it.requestMatchers("/css/*").permitAll()
                it.requestMatchers("/video/*").permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(
                AwsCognitoTokenFilter(
                    "/api/**",
                    authenticationManager,
                    "/api/v1/public/login",
                    "/api/v1/public/signup",
                    "/api/v1/attachments/download/**",
                    listOf("/*", "/webjars/**", "/js/*", "/img/*", "/css/*", "/video/*")
                ),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .authenticationManager(authenticationManager)
            .build()
    }
}