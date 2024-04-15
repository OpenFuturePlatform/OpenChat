package io.openfuture.openmessanger.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import io.openfuture.openmessanger.repository.GroupChatRepository;
import io.openfuture.openmessanger.repository.GroupParticipantRepository;
import io.openfuture.openmessanger.repository.MessageRepository;
import io.openfuture.openmessanger.repository.entity.GroupChat;
import io.openfuture.openmessanger.repository.entity.GroupParticipant;
import io.openfuture.openmessanger.repository.entity.MessageContentType;
import io.openfuture.openmessanger.repository.entity.MessageEntity;
import io.openfuture.openmessanger.service.GroupChatService;
import io.openfuture.openmessanger.web.request.group.AddParticipantsRequest;
import io.openfuture.openmessanger.web.request.group.CreateGroupRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GroupChatServiceImpl implements GroupChatService {

    private final GroupChatRepository groupChatRepository;
    private final GroupParticipantRepository groupParticipantRepository;
    private final MessageRepository messageRepository;

    @Override
    public GroupChat get(final Integer groupId) {
        return groupChatRepository.findById(groupId).orElseThrow(() -> {
            throw new EntityNotFoundException(String.format("Group with ID: %s not found", groupId));
        });
    }

    @Override
    public GroupChat create(final CreateGroupRequest request) {
        final GroupChat groupChat = groupChatRepository.findByNameAndCreator(request.getName(), request.getCreator()).orElseGet(() -> {
            final GroupChat newChatGroup = new GroupChat(request.getCreator(), LocalDateTime.now(), request.getName());
            return groupChatRepository.save(newChatGroup);
        });

        final MessageEntity message = new MessageEntity(request.getCreator() + " has just created project chat " + request.getName(),
                                                        "",
                                                        MessageContentType.TEXT,
                                                        LocalDateTime.now(),
                                                        groupChat.getId());
        messageRepository.save(message);
        addParticipants(new AddParticipantsRequest(groupChat.getId(), List.of(request.getCreator())));
        addParticipants(new AddParticipantsRequest(groupChat.getId(), request.getParticipants()));
        return groupChat;
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
                       .map(s -> new GroupParticipant(s, new GroupChat(request.getGroupId()), false, LocalDateTime.now(), LocalDateTime.now()))
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
        groupParticipantRepository.findAllByParticipantAndGroupChat_Id("", 1).ifPresent(
                groupParticipant -> {
                    groupParticipant.setDeleted(true);
                    groupParticipant.setLastUpdatedAt(LocalDateTime.now());
                }
        );
    }

    private boolean hasInGroup(final List<GroupParticipant> groupParticipants, final String username) {
        return groupParticipants.stream().map(GroupParticipant::getParticipant).anyMatch(user -> user.equals(username));
    }

}
