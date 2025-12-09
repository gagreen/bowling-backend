package com.gagreen.bowling.exception;

public class AuthenticationCredentialsNotFoundException extends org.springframework.security.authentication.AuthenticationCredentialsNotFoundException {
    public AuthenticationCredentialsNotFoundException(String message) {
        super(message);
    }

    public AuthenticationCredentialsNotFoundException() {
        super("로그인 후 이용해주세요.");
    }
}
