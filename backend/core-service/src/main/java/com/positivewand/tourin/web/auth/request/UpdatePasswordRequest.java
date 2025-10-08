package com.positivewand.tourin.web.auth.request;

public record UpdatePasswordRequest(String oldPassword, String newPassword) {
}
