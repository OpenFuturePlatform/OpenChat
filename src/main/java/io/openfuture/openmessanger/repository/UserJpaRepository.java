package io.openfuture.openmessanger.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.openfuture.openmessanger.repository.entity.User;

public interface UserJpaRepository extends JpaRepository<User, Integer> {
}
