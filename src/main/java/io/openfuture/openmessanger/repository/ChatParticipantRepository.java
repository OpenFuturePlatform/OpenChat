package io.openfuture.openmessanger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.openfuture.openmessanger.repository.entity.ChatParticipant;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Integer> {
}
