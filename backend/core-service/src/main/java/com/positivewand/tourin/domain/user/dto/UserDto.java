package com.positivewand.tourin.domain.user.dto;

import com.positivewand.tourin.domain.user.entity.User;

public record UserDto(Long id, String username, String password, String name, String email) {
    public static UserDto create(User entity) {
        return new UserDto(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getName(),
                entity.getEmail()
        );
    }
}
