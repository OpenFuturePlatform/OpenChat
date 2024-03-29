package io.openfuture.openmessanger.repository.entity;

import java.time.ZonedDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "open_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String email;

    private String phoneNumber;

    private ZonedDateTime registeredAt;

    private ZonedDateTime lastLogin;

    private String firstName;

    private String lastName;

    private String avatar;

    private boolean active = true; // default value

    public User(final String email) {
        this.email = email;
        this.registeredAt = ZonedDateTime.now();
    }

}