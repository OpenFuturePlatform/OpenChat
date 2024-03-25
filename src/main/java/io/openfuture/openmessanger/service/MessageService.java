package io.openfuture.openmessanger.service;

import io.openfuture.openmessanger.domain.User;

public interface MessageService {

    void sendMessage(User sender, String content);
}
