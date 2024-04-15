package io.openfuture.openmessanger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import io.openfuture.openmessanger.repository.entity.GroupChat;

public interface GroupChatRepository extends JpaRepository<GroupChat, Integer> {
    Optional<GroupChat> findByNameAndCreator(String name, String creator);
}
