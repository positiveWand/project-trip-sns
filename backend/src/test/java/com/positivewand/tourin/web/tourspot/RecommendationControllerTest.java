package com.positivewand.tourin.web.tourspot;

import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.domain.user.UserRepository;
import com.positivewand.tourin.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RecommendationControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUpEach() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(sharedHttpSession())
                .build();

        User userA = User.create(
                "userfortestA",
                passwordEncoder.encode("password123!"),
                "테용자A",
                "userfortestA@example.com"
        );
        userRepository.save(userA);
    }

    RequestPostProcessor testUser(String username) {
        return user(customUserDetailsService.loadUserByUsername(username));
    }

    @Test
    void 로그인된_회원은_메인_추천을_받을_수_있다() throws Exception {
        mockMvc.perform(get("/api/recommendations/main")
                        .with(testUser("userfortestA"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].description").exists())
                .andExpect(jsonPath("$[*].phoneNumber").exists())
                .andExpect(jsonPath("$[*].tags").exists());
    }

    @Test
    void 로그인_안된_회원은_메인_추천을_받을_수_없다() throws Exception {
        mockMvc.perform(get("/api/recommendations/main")
                        .with(anonymous())
                )
                .andExpect(status().isUnauthorized());
    }
}