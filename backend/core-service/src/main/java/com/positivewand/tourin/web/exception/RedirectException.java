package com.positivewand.tourin.web.exception;

public class RedirectException extends RuntimeException {
    private final String redirectUri;

    public RedirectException(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getUri() {
        return this.redirectUri;
    }
}
