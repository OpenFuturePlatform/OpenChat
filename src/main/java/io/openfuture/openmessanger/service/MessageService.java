package io.openfuture.openmessanger.service;

import java.util.List;

import io.openfuture.openmessanger.web.request.MessageRequest;
import io.openfuture.openmessanger.web.response.MessageResponse;

public interface MessageService {
    void sendMessage(MessageRequest message);
    List<MessageResponse> getAllByRecipient(String sender);
}
