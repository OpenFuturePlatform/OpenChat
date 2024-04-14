package io.openfuture.openmessanger.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.openfuture.openmessanger.repository.entity.PrivateChat;

public interface PrivateChatRepository extends JpaRepository<PrivateChat, Integer> {

    @Query("SELECT pc FROM PrivateChat pc " +
            "JOIN pc.chatParticipants cp1 " +
            "JOIN pc.chatParticipants cp2 " +
            "WHERE cp1.username = :sender AND cp2.username = :recipient")
    Optional<PrivateChat> findPrivateChatByParticipants(@Param("sender") String sender, @Param("recipient") String recipient);

}
