package io.openfuture.openmessanger.web.response;

import java.util.Collection;

import lombok.Value;

@Value
public class FrontMessagesResponse {
    Collection<LastMessage> lastMessages;
}
