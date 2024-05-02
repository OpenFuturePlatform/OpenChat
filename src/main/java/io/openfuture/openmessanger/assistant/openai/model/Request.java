package io.openfuture.openmessanger.assistant.openai.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Request {
    private String model;
    private List<Message> messages;
    private int n;
    private double temperature;

    public Request(final String model, String message) {
        this.model = model;
        this.messages = new ArrayList<>();
        this.messages.add(new Message(model, message));
    }

}
