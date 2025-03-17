package com.positivewand.tourin.domain.user.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void User엔티티는_필드값이_유효하면_성공적으로_생성된다() {
        User user = User.createUser("testuser", "testpassword123!", "김철수", "testuser@example.com");
        assertEquals(user.getUsername(), "testuser");
        assertEquals(user.getPassword(), "testpassword123!");
        assertEquals(user.getName(), "김철수");
        assertEquals(user.getEmail(), "testuser@example.com");
    }

    @Test
    void User엔티티는_필드값이_유효하지_않으면_생성되지_않는다() {
        assertThrows(RuntimeException.class, () -> User.createUser("testuser", "testpassword123!", "김철수", "blahblah"));
        assertThrows(RuntimeException.class, () -> User.createUser("testuser", "testpassword123!", "김철수", "test@example"));
        assertThrows(RuntimeException.class, () -> User.createUser("testuser", "testpassword123!", "김철수", "test@.com"));
        assertThrows(RuntimeException.class, () -> User.createUser("testuser", "testpassword123!", "김철수", "@example.com"));
    }
}