package io.openfuture.openmessanger.service;

import java.util.List;

import io.openfuture.openmessanger.web.request.MessageRequest;
import io.openfuture.openmessanger.web.response.MessageResponse;
import io.openfuture.openmessanger.web.response.UserMessageResponse;

public interface MessageService {
    void sendMessage(MessageRequest message);
    MessageResponse save(MessageRequest messageRequest);
    List<MessageResponse> getAllByRecipient(String recipient);
    List<MessageResponse> getAllByRecipientAndSender(String recipient, String sender);
    List<UserMessageResponse> getGroupRecipient(String recipient);
}
