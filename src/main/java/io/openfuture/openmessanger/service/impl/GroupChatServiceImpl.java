package io.openfuture.openmessanger.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.openfuture.openmessanger.repository.GroupChatRepository;
import io.openfuture.openmessanger.repository.GroupParticipantRepository;
import io.openfuture.openmessanger.repository.entity.GroupChat;
import io.openfuture.openmessanger.repository.entity.GroupParticipant;
import io.openfuture.openmessanger.service.GroupChatService;
import io.openfuture.openmessanger.web.request.group.AddParticipantsRequest;
import io.openfuture.openmessanger.web.request.group.CreateGroupRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupChatServiceImpl implements GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final GroupParticipantRepository groupParticipantRepository;

    @Override
    public GroupChat create(final CreateGroupRequest request) {
        final GroupChat groupChat = new GroupChat(request.getCreator(), LocalDateTime.now(), request.getName());
        return groupChatRepository.save(groupChat);
    }

    @Override
    public void addParticipants(final AddParticipantsRequest request) {
        //add check to get users from database as well
        final List<GroupParticipant> existingInGroup = request.getUsers().stream().map(username -> groupParticipantRepository.findAllByParticipantAndGroupChat_Id(
                username, request.getGroupId()
        )).filter(Optional::isPresent).map(Optional::get).toList();

        existingInGroup.forEach(groupParticipant -> groupParticipant.setDeleted(false));

        final List<String> newParticipants = request.getUsers().stream().filter(s -> !hasInGroup(existingInGroup, s)).toList();

        newParticipants.stream()
                       .map(s -> new GroupParticipant(s, new GroupChat(request.getGroupId()), false))
                       .forEach(groupParticipantRepository::save);
    }

    @Override
    public void archive(final Integer groupId) {
        final Optional<GroupChat> group = groupChatRepository.findById(groupId);
        group.ifPresent(groupChat -> {
            groupChat.setArchived(true);
            groupChat.setArchivedAt(LocalDateTime.now());
        });
    }

    @Override
    public void leave() {

    }

    private boolean hasInGroup(final List<GroupParticipant> groupParticipants, final String username) {
        return groupParticipants.stream().map(GroupParticipant::getParticipant).anyMatch(user -> user.equals(username));
    }

}
