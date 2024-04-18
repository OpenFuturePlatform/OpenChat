package io.openfuture.openmessanger.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.openfuture.openmessanger.repository.entity.GroupChat;

public interface GroupChatRepository extends JpaRepository<GroupChat, Integer> {
    Optional<GroupChat> findByNameAndCreator(String name, String creator);

    @Query(value = "SELECT DISTINCT p.* FROM " +
            " group_chat p " +
            "JOIN group_participant c ON p.id = c.group_id " +
            "WHERE c.participant IN :names GROUP BY p.id HAVING COUNT(DISTINCT c.participant) = 2",
            nativeQuery = true)
    List<GroupChat> findAllByGroupParticipants_ParticipantContains(List<String> names);
}
