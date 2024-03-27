package io.openfuture.openmessanger.domain;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String body;
    private String sender;
    private String receiver;
    private ZonedDateTime sentAt;
}
