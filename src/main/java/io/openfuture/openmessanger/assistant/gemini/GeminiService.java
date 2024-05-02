package io.openfuture.openmessanger.assistant.gemini;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, Object> requestBody = new HashMap<>();
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> parts = new HashMap<>();
        parts.put("text", "Write a story about a magic backpack");
        contents.add(parts);
        requestBody.put("contents", contents);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        final ResponseEntity<GeminiResponse> response = restTemplate.postForEntity("/gemini-pro:generateContent", requestEntity, GeminiResponse.class, Map.of("key", geminiApiKey));

        return response.getBody().getCandidates().get(0).getContent().getParts().get(0).getText();
    }

}
