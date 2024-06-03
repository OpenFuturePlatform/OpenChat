package io.openfuture.openmessenger.assistant.gemini

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.DefaultUriBuilderFactory
import java.util.Map

@Configuration
class GeminiHttpConfiguration {
    @Value("\${gemini.api.key}")
    private val geminiApiKey: String? = null

    @Value("\${gemini.api.url}")
    private val apiUrl: String? = null
    @Bean
    fun geminiRestTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.uriTemplateHandler = DefaultUriBuilderFactory(apiUrl!!)
        restTemplate.setDefaultUriVariables(Map.of("key", geminiApiKey))
        return restTemplate
    }
}