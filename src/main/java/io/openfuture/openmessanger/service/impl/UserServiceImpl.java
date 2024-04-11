package io.openfuture.openmessanger.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import io.openfuture.openmessanger.repository.MessageRepository;
import io.openfuture.openmessanger.repository.UserJpaRepository;
import io.openfuture.openmessanger.repository.entity.User;
import io.openfuture.openmessanger.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final MessageRepository messageRepository;

    @Override
    public Collection<User> getAllRecipientsBySender(final String username) {
        final List<String> recipients = messageRepository.findRecipientsBySender(username);
        return recipients.stream().map(userJpaRepository::findByEmail).toList();
    }

    @Override
    public Collection<User> getAllUsers() {
        return userJpaRepository.findAll();
    }

}
