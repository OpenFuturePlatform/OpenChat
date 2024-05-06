package io.openfuture.openmessanger.assistant.gemini;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Autowired
    @Qualifier("geminiRestTemplate")
    private RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public String chat(String input) {
        String jsonPayload = "{\"contents\":[{\"role\": \"user\", \"parts\":[{\"text\": \"%s\"}]}]}".formatted(input);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

        final ResponseEntity<GeminiResponse> response = restTemplate.postForEntity("/gemini-pro:generateContent" + "?key=" + geminiApiKey, requestEntity, GeminiResponse.class);

        return response.getBody().getCandidates().get(0).getContent().getParts().get(0).getText();
    }

}
