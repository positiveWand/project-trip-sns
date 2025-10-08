package com.positivewand.tourin.domain.user;

import com.positivewand.tourin.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Page<User> findByUsernameContaining(String usernameQuery, Pageable pageable);

    void deleteByUsername(String username);
}
