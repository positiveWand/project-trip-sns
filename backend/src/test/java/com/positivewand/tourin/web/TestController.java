package com.positivewand.tourin.web;

import com.positivewand.tourin.domain.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {
    private final TestService testService;

    @GetMapping("/test/default")
    public String testGet() {
        return "test get";
    }

    @PostMapping("/test/default")
    public String testPost() {
        return "test get";
    }

    @DeleteMapping("/test/default")
    public String testDelete() {
        return "test get";
    }

    @PutMapping("/test/default")
    public String testPut() {
        return "test get";
    }

    @GetMapping("/test/service")
    public String testServiceSomething() {
        return testService.doSomething();
    }

    @GetMapping("/test/service/args")
    public String testServiceSomethingWithArgs() {
        return testService.doSomethingWithArgs("testArg");
    }

    @GetMapping("/test/service/exception")
    public String testServiceException() {
        return testService.doException();
    }

    @GetMapping("/test/exception/runtime")
    public String testException() {
        throw new RuntimeException("런타임 예외 발생!");
    }

    @GetMapping("/test/exception/authentication")
    public String testAuthenticationException() {
        throw new AuthenticationException("인증 예외 발생!") {
        };
    }

    @GetMapping("/test/exception/forbidden")
    public String testAuthorizationException() {
        throw new AccessDeniedException("인가 예외 발생!");
    }
}
