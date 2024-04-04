package io.openfuture.openmessanger.web.response;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class LastMessage {
    String preview;
    String sender;
    LocalDateTime sentAt;
}
