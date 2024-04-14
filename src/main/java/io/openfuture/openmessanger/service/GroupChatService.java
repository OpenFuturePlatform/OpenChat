package io.openfuture.openmessanger.service;

import io.openfuture.openmessanger.repository.entity.GroupChat;
import io.openfuture.openmessanger.web.request.group.AddParticipantsRequest;
import io.openfuture.openmessanger.web.request.group.CreateGroupRequest;

public interface GroupChatService {

    GroupChat get(Integer groupId);

    GroupChat create(final CreateGroupRequest request);

    void addParticipants(AddParticipantsRequest request);

    void archive(Integer groupId);

    void leave();
}
