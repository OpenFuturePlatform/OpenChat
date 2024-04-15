package io.openfuture.openmessanger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.openfuture.openmessanger.repository.entity.ChatParticipant;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Integer> {
    List<ChatParticipant> findAllByChatId(Integer chat);
}
