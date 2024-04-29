package io.openfuture.openmessanger.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import io.openfuture.openmessanger.repository.MessageRepository;
import io.openfuture.openmessanger.repository.UserJpaRepository;
import io.openfuture.openmessanger.repository.entity.GroupChat;
import io.openfuture.openmessanger.repository.entity.User;
import io.openfuture.openmessanger.service.GroupChatService;
import io.openfuture.openmessanger.service.UserService;
import io.openfuture.openmessanger.web.request.user.UserDetailsRequest;
import io.openfuture.openmessanger.web.response.CommonGroupsResponse;
import io.openfuture.openmessanger.web.response.UserDetailsResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserJpaRepository userJpaRepository;
    private final MessageRepository messageRepository;
    private final GroupChatService groupChatService;

    @Override
    public Collection<User> getAllRecipientsBySender(final String username) {
        final List<String> recipients = messageRepository.findRecipientsBySender(username);
        return recipients.stream().map(userJpaRepository::findByEmail).toList();
    }

    @Override
    public Collection<User> getAllUsers() {
        return userJpaRepository.findAll();
    }

    @Override
    public User getByEmail(final String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public UserDetailsResponse getUserDetails(final UserDetailsRequest request) {
        final List<GroupChat> commonGroups = groupChatService.findCommonGroups(request.email(), request.username());
        final User user = userJpaRepository.findByEmail(request.email());

        final List<CommonGroupsResponse.GroupInfo> groups = commonGroups.stream().map(groupChat -> new CommonGroupsResponse.GroupInfo(groupChat.getId(), groupChat.getName()))
                                                                        .toList();
        return new UserDetailsResponse(request.email(), "", groups);

    }


}
