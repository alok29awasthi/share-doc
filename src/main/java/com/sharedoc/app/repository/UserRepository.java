package com.sharedoc.app.repository;

import com.sharedoc.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<List<User>> findByEmailIn(List<String> emailIds);
}
