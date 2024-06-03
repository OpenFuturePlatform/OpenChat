package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "group_chat")
class GroupChat() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Column(name = "creator")
    var creator: String? = null

    @Column(name = "created_at")
    var createdAt: LocalDateTime? = null

    @Column(name = "name", length = 255)
    var name: String? = null

    @Column(name = "archived")
    var archived = false

    @Column(name = "archived_at")
    var archivedAt: LocalDateTime? = null

    @OneToMany(mappedBy = "groupChat")
    var groupParticipants: List<GroupParticipant>? = null

    constructor(creator: String?, createdAt: LocalDateTime?, name: String?) : this() {
        this.creator = creator
        this.createdAt = createdAt
        this.name = name
    }

    constructor(id: Int?) : this() {
        this.id = id
    }
    
}