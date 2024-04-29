package io.openfuture.openmessanger.service;

import java.util.Collection;

import io.openfuture.openmessanger.repository.entity.User;
import io.openfuture.openmessanger.web.request.user.UserDetailsRequest;
import io.openfuture.openmessanger.web.response.UserDetailsResponse;

public interface UserService {
    Collection<User> getAllRecipientsBySender(String username);

    Collection<User> getAllUsers();

    User getByEmail(final String email);

    UserDetailsResponse getUserDetails(UserDetailsRequest request);
}
