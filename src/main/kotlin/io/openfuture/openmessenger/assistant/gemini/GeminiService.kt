package io.openfuture.openmessenger.assistant.gemini

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GeminiService (
    @Qualifier("geminiRestTemplate") private val restTemplate: RestTemplate,
    @Value("\${gemini.api.key}")
    private val geminiApiKey: String
) {

    fun chat(input: String?): String? {
        val jsonPayload = """
            {
                "contents": [
                    {
                        "role": "user",
                        "parts": [
                            {
                                "text": "$input"
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val requestEntity = HttpEntity(jsonPayload, headers)
        val response = restTemplate.postForEntity("/gemini-pro:generateContent?key=$geminiApiKey", requestEntity, GeminiResponse::class.java)
        val text: String? = response.body?.candidates?.get(0)?.content?.parts?.get(0)?.text

        return text
    }

}