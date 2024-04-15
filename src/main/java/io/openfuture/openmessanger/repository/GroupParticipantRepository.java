package io.openfuture.openmessanger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.openfuture.openmessanger.repository.entity.GroupChat;
import io.openfuture.openmessanger.repository.entity.GroupParticipant;

public interface GroupParticipantRepository extends JpaRepository<GroupParticipant, Integer> {
    Optional<GroupParticipant> findAllByParticipantAndGroupChat_Id(String username, Integer groupId);

    List<GroupParticipant> findAllByGroupChat(GroupChat groupChat);
}
