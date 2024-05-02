package io.openfuture.openmessanger.assistant.openai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.openfuture.openmessanger.assistant.openai.model.Request;
import io.openfuture.openmessanger.assistant.openai.model.Response;


@Service
public class ChatService {

    @Autowired
    @Qualifier("openaiRestTemplate")
    private RestTemplate restTemplate;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiUrl;

    public String chat(String input) {
        Request request = new Request(model, input);

        Response response = restTemplate.postForObject(apiUrl, request, Response.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        return response.getChoices().get(0).getMessage().getContent();
    }

}
