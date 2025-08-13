package com.positivewand.tourin.web;

import com.positivewand.tourin.domain.auth.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    RequestPostProcessor testUser() {
        return user(new CustomUserDetails(
                1232323L,
                "testuser",
                "",
                "테스트사용자",
                "testuser@example.com",
                null
        ));
    }

    @Test
    void 로그인하지_않고_요청() throws Exception {
        mockMvc.perform(get("/api/test/default"));
        mockMvc.perform(post("/api/test/default"));
        mockMvc.perform(delete("/api/test/default"));
        mockMvc.perform(put("/api/test/default"));

        mockMvc.perform(get("/api/test/exception/runtime"));
        mockMvc.perform(get("/api/test/exception/authentication"));
        mockMvc.perform(get("/api/test/exception/forbidden"));

        mockMvc.perform(get("/api/test/service"));
        mockMvc.perform(get("/api/test/service/args"));
        mockMvc.perform(get("/api/test/service/exception"));
    }

    @Test
    void 로그인하고_요청() throws Exception {
        mockMvc.perform(get("/api/test/default")
                .with(testUser()));
        mockMvc.perform(post("/api/test/default")
                .with(testUser()));
        mockMvc.perform(delete("/api/test/default")
                .with(testUser()));
        mockMvc.perform(put("/api/test/default")
                .with(testUser()));

        mockMvc.perform(get("/api/test/exception/runtime")
                .with(testUser()));
        mockMvc.perform(get("/api/test/exception/authentication")
                .with(testUser()));
        mockMvc.perform(get("/api/test/exception/forbidden")
                .with(testUser()));

        mockMvc.perform(get("/api/test/service")
                .with(testUser()));
        mockMvc.perform(get("/api/test/service/args")
                .with(testUser()));
        mockMvc.perform(get("/api/test/service/exception")
                .with(testUser()));
    }

    @Test
    void 로그인하지_않고_IP_바꿔서_요청() throws Exception {
        mockMvc.perform(get("/api/test/default")
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(post("/api/test/default")
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(delete("/api/test/default")
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(put("/api/test/default")
                .remoteAddress("255.255.255.255"));

        mockMvc.perform(get("/api/test/exception/runtime")
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(get("/api/test/exception/authentication")
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(get("/api/test/exception/forbidden")
                .remoteAddress("255.255.255.255"));

        mockMvc.perform(get("/api/test/service")
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(get("/api/test/service/args")
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(get("/api/test/service/exception")
                .remoteAddress("255.255.255.255"));
    }

    @Test
    void 로그인하고_IP_바꿔서_요청() throws Exception {
        mockMvc.perform(get("/api/test/default")
                .with(testUser())
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(post("/api/test/default")
                .with(testUser())
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(delete("/api/test/default")
                .with(testUser())
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(put("/api/test/default")
                .with(testUser())
                .remoteAddress("255.255.255.255"));

        mockMvc.perform(get("/api/test/exception/runtime")
                .with(testUser())
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(get("/api/test/exception/authentication")
                .with(testUser())
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(get("/api/test/exception/forbidden")
                .with(testUser())
                .remoteAddress("255.255.255.255"));

        mockMvc.perform(get("/api/test/service")
                .with(testUser())
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(get("/api/test/service/args")
                .with(testUser())
                .remoteAddress("255.255.255.255"));
        mockMvc.perform(get("/api/test/service/exception")
                .with(testUser())
                .remoteAddress("255.255.255.255"));
    }

    @Test
    void 로그인하고_IP_바꾸고_클라이언트_에이전트_설정해_요청() throws Exception {
        mockMvc.perform(get("/api/test/default")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));
        mockMvc.perform(post("/api/test/default")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));
        mockMvc.perform(delete("/api/test/default")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));
        mockMvc.perform(put("/api/test/default")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));

        mockMvc.perform(get("/api/test/exception/runtime")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));
        mockMvc.perform(get("/api/test/exception/authentication")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));
        mockMvc.perform(get("/api/test/exception/forbidden")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));

        mockMvc.perform(get("/api/test/service")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));
        mockMvc.perform(get("/api/test/service/args")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));
        mockMvc.perform(get("/api/test/service/exception")
                .with(testUser())
                .remoteAddress("255.255.255.255")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"));
    }
}