package io.openfuture.openmessanger.domain;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class Message {
    private String body;
    private String sender;
    private ZonedDateTime sentAt;
}
