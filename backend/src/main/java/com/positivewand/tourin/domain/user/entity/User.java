package com.positivewand.tourin.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.regex.Pattern;

@Entity
@Table(name="user")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;

    public static User createUser(String username, String password, String name, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setName(name);
        user.setEmail(email);

        return user;
    }


    public static class InvalidUserEntity extends RuntimeException {
        public InvalidUserEntity(String message) {
            super(message);
        }
    }

    private void setUsername(String username) {
        this.username = username;
    }
    private void setPassword(String password) {
        this.password = password;
    }
    private void setName(String name) {
        this.name = name;
    }
    private void setEmail(String email) {
        if (!Pattern.matches("^(?!\\.)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", email)) {
            throw new InvalidUserEntity("이메일 형식이 올바르지 않습니다.");
        }

        this.email = email;
    }

    public void updateProfile(String newName, String newEmail) {
        this.setName(newName);
        this.setEmail(newEmail);
    }

    public void changePassword(String newPassword) {
        this.setPassword(newPassword);
    }
}
