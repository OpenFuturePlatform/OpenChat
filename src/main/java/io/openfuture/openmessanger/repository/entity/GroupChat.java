package io.openfuture.openmessanger.repository.entity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor

@Data
@Entity
@Table(name = "group_chat")
public class GroupChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creator")
    private String creator;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "archived")
    private boolean archived = false;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    public GroupChat(final String creator, final LocalDateTime createdAt, final String name) {
        this.creator = creator;
        this.createdAt = createdAt;
        this.name = name;
    }

    public GroupChat(final Integer id) {
        this.id = id;
    }

}

