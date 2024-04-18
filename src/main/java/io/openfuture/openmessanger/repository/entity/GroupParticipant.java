package io.openfuture.openmessanger.repository.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "group_participant")
public class GroupParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "participant")
    private String participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private GroupChat groupChat;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    public GroupParticipant(final String participant,
                            final GroupChat groupChat,
                            final boolean deleted,
                            final LocalDateTime addedAt,
                            final LocalDateTime lastUpdatedAt) {
        this.participant = participant;
        this.groupChat = groupChat;
        this.deleted = deleted;
        this.addedAt = addedAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public GroupParticipant(final Integer id) {
        this.id = id;
    }

}
