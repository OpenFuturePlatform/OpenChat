package io.openfuture.openmessanger.assistant.openai.model;

import java.util.List;

import lombok.Data;

@Data
public class Response {
    private List<Choice> choices;

    @Data
    public static class Choice {

        private int index;
        private Message message;
    }
}
