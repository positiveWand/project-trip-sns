package com.positivewand.tourin.web.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.domain.user.UserRepository;
import com.positivewand.tourin.domain.user.entity.User;
import com.positivewand.tourin.web.auth.request.LoginRequest;
import com.positivewand.tourin.web.auth.request.SignupRequest;
import com.positivewand.tourin.web.auth.request.UpdatePasswordRequest;
import com.positivewand.tourin.web.auth.request.UpdateProfileRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final String testUsername = "userfortest";
    private static final String testPassword = "password123!";
    private static final String testName = "테용자";
    private static final String testEmail = "userfortest@example.com";

    RequestPostProcessor testUser() {
        return user(customUserDetailsService.loadUserByUsername(testUsername));
    }

    @BeforeEach
    void setUpEach() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(sharedHttpSession())
                .build();

        User testUser = User.create(testUsername, passwordEncoder.encode(testPassword), testName, testEmail);
        userRepository.save(testUser);
    }

    @Test
    void 가입되지_않은_사용자의_회원가입은_성공한다() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new SignupRequest(
                                "newuser",
                                "newpassword123!",
                                "신용자",
                                "new@example.com"
                        )
                )))
                .andExpect(status().isCreated());
    }

    @Test
    void 중복된_회원가입은_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new SignupRequest(
                                testUsername,
                                testPassword,
                                testName,
                                testEmail
                        )
                )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 형식이_틀린_회원가입은_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new SignupRequest(
                                "newuser",
                                "newpassword123!",
                                "신용자",
                                "random string"
                        )
                )))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원은_로그인이_성공한다() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new LoginRequest(
                                testUsername,
                                testPassword
                        )
                )))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(testUsername));
    }

    @Test
    void 회원가입_안된_사용자는_로그인이_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new LoginRequest(
                                "someuser",
                                "somepassword123!"
                        )
                )))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated());
    }

    @Test
    void 비밀번호가_틀린_회원은_로그인이_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new LoginRequest(
                                testUsername,
                                "wrongpassword123!"
                        )
                )))
                .andExpect(status().isUnauthorized())
                .andExpect(unauthenticated());
    }

    @Test
    void 로그인된_회원은_로그아웃에_성공한다() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new LoginRequest(
                                testUsername,
                                testPassword
                        )
                )))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isNoContent())
                .andExpect(unauthenticated());
    }

    @Test
    void 로그인_안된_회원은_로그아웃에_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인된_회원은_프로필_조회에_성공한다() throws Exception {
        mockMvc.perform(get("/api/auth/me").with(testUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUsername))
                .andExpect(jsonPath("$.name").value(testName))
                .andExpect(jsonPath("$.email").value(testEmail));
    }

    @Test
    void 로그인_안된_회원은_프로필_조회에_실패한다() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인된_회원은_회원탈퇴에_성공한다() throws Exception {
        mockMvc.perform(post("/api/auth/delete-account").with(testUser()))
                .andExpect(status().isNoContent());

        userRepository.flush();

        Optional<User> user = userRepository.findByUsername(testUsername);
        assertTrue(user.isEmpty());
    }

    @Test
    void 로그인_안된_회원은_회원탈퇴에_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/delete-account").with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인된_회원은_자신의_프로필_변경을_성공한다() throws Exception {
        mockMvc.perform(post("/api/auth/update-profile").with(testUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdateProfileRequest(
                                        "새용자",
                                        "newemail@example.com"
                                )
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUsername))
                .andExpect(jsonPath("$.name").value("새용자"))
                .andExpect(jsonPath("$.email").value("newemail@example.com"));
    }

    @Test
    void 로그인되지_않은_회원은_자신의_프로필_변경을_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/update-profile").with(anonymous())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new UpdateProfileRequest(
                                "새용자",
                                "newemail@example.com"
                        )
                )))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인된_회원은_자신의_비밀번호_변경을_성공한다() throws Exception {
        mockMvc.perform(post("/api/auth/update-password").with(testUser())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new UpdatePasswordRequest(
                                testPassword,
                                "newpassword123!"
                        )
                )))
                .andExpect(status().isNoContent());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest(
                                        testUsername,
                                        "newpassword123!"
                                )
                        )))
                .andExpect(status().isOk())
                .andExpect(authenticated().withUsername(testUsername));
    }

    @Test
    void 기존_비밀번호가_틀리면_비밀번호_변경을_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/update-password").with(testUser())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdatePasswordRequest(
                                        "wrongpassword123!",
                                        "newpassword123!"
                                )
                        )))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인되지_않은_회원은_자신의_비밀번호_변경을_실패한다() throws Exception {
        mockMvc.perform(post("/api/auth/update-password").with(anonymous())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UpdatePasswordRequest(
                                        testPassword,
                                        "newpassword123!"
                                )
                        )))
                .andExpect(status().isUnauthorized());
    }
}