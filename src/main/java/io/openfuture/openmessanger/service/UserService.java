package io.openfuture.openmessanger.service;

import java.util.Collection;

import io.openfuture.openmessanger.repository.entity.User;

public interface UserService {
    Collection<User> getAllRecipientsBySender(String username);

    Collection<User> getAllUsers();
}
