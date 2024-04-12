package io.openfuture.openmessanger.repository.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "group")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "creator")
    private Integer creator;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "archived")
    private boolean archived = false;

    @Column(name = "archived_at")
    private Date archivedAt;

}

