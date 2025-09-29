package com.positivewand.tourin.domain;

import org.springframework.stereotype.Service;

@Service
public class TestService {
    public String doSomething() {
        try {
            Thread.sleep(150);
        } catch (Exception e) {

        }
        return "did something";
    }

    public String doSomethingWithArgs(String arg) {
        try {
            Thread.sleep(150);
        } catch (Exception e) {

        }
        return "did something with arg";
    }

    public String doException() {
        try {
            Thread.sleep(150);
        } catch (Exception e) {

        }
        throw new RuntimeException("서비스 예외 발생!");
    }
}
