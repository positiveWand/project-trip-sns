package com.positivewand.tourin.web.tourspot;

import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.domain.recommendation.TrendService;
import com.positivewand.tourin.domain.tourspot.TourSpotRepository;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotCategory;
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

import java.util.Arrays;

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
    @Autowired
    private TourSpotRepository tourSpotRepository;
    @Autowired
    private TrendService trendService;

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
    void 로그인된_회원은_테스트_추천을_받을_수_있다() throws Exception {
        mockMvc.perform(get("/api/recommendations/test")
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
    void 로그인된_회원은_트렌드_추천을_받을_수_있다() throws Exception {
        // given - 관광지 10개
        for (int i = 0; i < 10; i++) {
            TourSpot testTourSpot = TourSpot.create(
                    1000000000+i,
                    "관광지" + i,
                    "관광지 설명...",
                    "대충 이미지 url",
                    "대충 full address",
                    "대충 address1",
                    "대충 address2",
                    111,
                    111,
                    "대충 전화번호",
                    37.5642135,
                    127.0016985,
                    Arrays.asList(TourSpotCategory.EXPERIENCE)
            );

            tourSpotRepository.save(testTourSpot);
        }

        // given - 트렌드 점수 저장소에 관광지 저장
        trendService.resetTrend();
        for (int id = 1000000000; id < 1000000010; id++) {
            trendService.incrementTrendScore(id, 1);
        }
        trendService.slideTrendTopkWindow();

        mockMvc.perform(get("/api/recommendations/trend")
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
    void 로그인_안된_회원은_추천을_받을_수_없다() throws Exception {
        String[] endpoints = {
                "/api/recommendations/trend",
                "/api/recommendations/test"
        };

        for (String endpoint: endpoints) {
            mockMvc.perform(get(endpoint)
                            .with(anonymous())
                    )
                    .andExpect(status().isUnauthorized());
        }
    }
}