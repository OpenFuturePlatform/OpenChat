package io.openfuture.openmessanger.repository.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "group_participant")
class GroupParticipant() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     var id: Int? = null

    @Column(name = "participant")
     var participant: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
     var groupChat: GroupChat? = null

    @Column(name = "deleted")
     var deleted = false

    @Column(name = "added_at")
     var addedAt: LocalDateTime? = null

    @Column(name = "last_updated_at")
     var lastUpdatedAt: LocalDateTime? = null

    constructor(
        participant: String?,
        groupChat: GroupChat?,
        deleted: Boolean,
        addedAt: LocalDateTime?,
        lastUpdatedAt: LocalDateTime?
    ) : this() {
        this.participant = participant
        this.groupChat = groupChat
        this.deleted = deleted
        this.addedAt = addedAt
        this.lastUpdatedAt = lastUpdatedAt
    }

}