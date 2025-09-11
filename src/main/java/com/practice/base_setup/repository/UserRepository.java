package com.practice.base_setup.repository;

import com.practice.base_setup.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Boolean existsByEmailIgnoreCase(String email);

    Optional<User> findByEmail(String email);
}
