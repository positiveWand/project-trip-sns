package com.positivewand.tourin.web.tourspot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.domain.tourspot.TourSpotRepository;
import com.positivewand.tourin.domain.tourspot.TourSpotReviewRepository;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotCategory;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotReview;
import com.positivewand.tourin.domain.user.UserRepository;
import com.positivewand.tourin.domain.user.entity.User;
import com.positivewand.tourin.web.tourspot.request.AddTourSpotReviewRequest;
import com.positivewand.tourin.web.tourspot.request.PutTourSpotReviewLikeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TourSpotControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TourSpotRepository tourSpotRepository;
    @Autowired
    private TourSpotReviewRepository tourSpotReviewRepository;


    @BeforeEach
    void setUpEach() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(sharedHttpSession())
                .build();
    }

    private TourSpotReview lastReview;

    void setUpReview() {
        // 테스트 관광지
        TourSpot testTourSpot = TourSpot.create(
                2000000000,
                "리뷰용관광지",
                "관광지 설명...",
                "대충 이미지 url",
                "대충 full address",
                "대충 address1",
                "대충 address2",
                111,
                111,
                "대충 전화번호",
                37.7642173334521,
                128.88380355693758,
                Arrays.asList(TourSpotCategory.EXPERIENCE)
        );

        tourSpotRepository.save(testTourSpot);

        // 테스트 사용자
        // 테스트 회원 2명
        User userA = User.create(
                "userfortestA",
                passwordEncoder.encode("password123!"),
                "테용자A",
                "userfortestA@example.com"
        );
        userRepository.save(userA);
        User userB = User.create(
                "userfortestB",
                passwordEncoder.encode("password123!"),
                "테용자B",
                "userfortestB@example.com"
        );
        userRepository.save(userB);

        // 테스트 관광지 후기
        for (int i = 0; i < 33; i++) {
            TourSpotReview testTourSpotReview = TourSpotReview.create(
                    testTourSpot,
                    userA,
                    "관광지 후기",
                    LocalDateTime.now(),
                    0);
            lastReview = tourSpotReviewRepository.save(testTourSpotReview);
        }
    }

    RequestPostProcessor testUser(String username) {
        return user(customUserDetailsService.loadUserByUsername(username));
    }

    @Test
    void 사용자는_관광지_하나를_검색한다() throws Exception {
        setUpReview();
        mockMvc.perform(get("/api/tour-spots/{tourSpotId}", 2000000000))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.address").exists())
                .andExpect(jsonPath("$.lat").exists())
                .andExpect(jsonPath("$.lng").exists())
                .andExpect(jsonPath("$.imageUrl").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.phoneNumber").exists())
                .andExpect(jsonPath("$.tags").exists());
    }

    @Test
    void 사용자는_없는_관광지_조회에_실패한다() throws Exception {
        setUpReview();
        mockMvc.perform(get("/api/tour-spots/{tourSpotId}", 2100000000))
                .andExpect(status().isNotFound());
    }

    @Test
    void 사용자는_관광지의_후기_모음을_조회한다() throws Exception {
        setUpReview();
        mockMvc.perform(get("/api/tour-spots/{tourSpotId}/reviews", 2000000000)
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].tourSpotId").exists())
                .andExpect(jsonPath("$[*].userId").exists())
                .andExpect(jsonPath("$[*].createdAt").exists())
                .andExpect(jsonPath("$[*].content").exists())
                .andExpect(jsonPath("$[*].likeCount").exists())
                .andExpect(header().longValue("X-Pagination-Page", 0))
                .andExpect(header().longValue("X-Pagination-Page-Size", 10))
                .andExpect(header().longValue("X-Pagination-Page-Limit", 10))
                .andExpect(header().longValue("X-Pagination-Total-Page", 4))
                .andExpect(header().longValue("X-Pagination-Total-Item", 33));
    }

    @Test
    void 로그인된_회원은_관광지_후기를_추가한다() throws Exception {
        setUpReview();
        mockMvc.perform(post("/api/tour-spots/{tourSpotId}/reviews", 2000000000)
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddTourSpotReviewRequest(
                                "userfortestA",
                                "테스트용 리뷰"
                        )))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.tourSpotId").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.likeCount").exists());
    }

    @Test
    void 로그인_안된_회원은_관광지_후기를_추가하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(post("/api/tour-spots/{tourSpotId}/reviews", 2000000000)
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddTourSpotReviewRequest(
                                "userfortestA",
                                "테스트용 리뷰"
                        )))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 회원은_존재하지_않는_관광지에_후기를_추가하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(post("/api/tour-spots/{tourSpotId}/reviews", 2100000000)
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddTourSpotReviewRequest(
                                "userfortestA",
                                "테스트용 리뷰"
                        )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void 회원은_다른회원의_관광지_후기를_추가하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(post("/api/tour-spots/{tourSpotId}/reviews", 2000000000)
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddTourSpotReviewRequest(
                                "userfortestB",
                                "테스트용 리뷰"
                        )))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void 로그인된_회원은_자신의_관광지_후기를_삭제한다() throws Exception {
        setUpReview();
        mockMvc.perform(delete("/api/tour-spot-reviews/{tourSpotReviewId}", lastReview.getId())
                        .with(testUser("userfortestA"))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void 로그인_안된_회원은_관광지_후기를_삭제하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(delete("/api/tour-spot-reviews/{tourSpotReviewId}", lastReview.getId())
                        .with(anonymous())
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 회원은_다른회원의_관광지_후기를_삭제하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(delete("/api/tour-spot-reviews/{tourSpotReviewId}", lastReview.getId())
                        .with(testUser("userfortestB"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void 회원은_존재하지_않는_관광지_후기를_삭제하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(delete("/api/tour-spot-reviews/{tourSpotReviewId}", 999999999)
                        .with(testUser("userfortestA"))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void 로그인된_회원은_관광지_후기를_공감한다() throws Exception {
        setUpReview();
        mockMvc.perform(put("/api/tour-spot-reviews/{tourSpotReviewId}/likes", lastReview.getId())
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutTourSpotReviewLikeRequest(
                                "userfortestA",
                                true
                        )))
                )
                .andExpect(status().isOk());
    }

    @Test
    void 로그인된_회원은_관광지_후기를_공감취소한다() throws Exception {
        setUpReview();
        mockMvc.perform(put("/api/tour-spot-reviews/{tourSpotReviewId}/likes", lastReview.getId())
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutTourSpotReviewLikeRequest(
                                "userfortestA",
                                false
                        )))
                )
                .andExpect(status().isOk());
    }

    @Test
    void 로그인_안된_회원은_관광지_후기를_공감하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(put("/api/tour-spot-reviews/{tourSpotReviewId}/likes", lastReview.getId())
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutTourSpotReviewLikeRequest(
                                "userfortestA",
                                true
                        )))
                )
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    void 로그인_안된_회원은_관광지_후기를_공감취소하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(put("/api/tour-spot-reviews/{tourSpotReviewId}/likes", lastReview.getId())
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutTourSpotReviewLikeRequest(
                                "userfortestA",
                                false
                        )))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 회원은_다른_회원_대신_공감할_수_없다() throws Exception {
        setUpReview();
        mockMvc.perform(put("/api/tour-spot-reviews/{tourSpotReviewId}/likes", lastReview.getId())
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutTourSpotReviewLikeRequest(
                                "userfortestB",
                                true
                        )))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void 회원은_다른_회원_대신_공감취소할_수_없다() throws Exception {
        setUpReview();
        mockMvc.perform(put("/api/tour-spot-reviews/{tourSpotReviewId}/likes", lastReview.getId())
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutTourSpotReviewLikeRequest(
                                "userfortestB",
                                false
                        )))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void 회원은_존재하지_않는_관광지_후기를_공감하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(put("/api/tour-spot-reviews/{tourSpotReviewId}/likes", 999999999)
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutTourSpotReviewLikeRequest(
                                "userfortestA",
                                true
                        )))
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void 회원은_존재하지_않는_관광지_후기를_공감취소하지_못한다() throws Exception {
        setUpReview();
        mockMvc.perform(put("/api/tour-spot-reviews/{tourSpotReviewId}/likes", 999999999)
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PutTourSpotReviewLikeRequest(
                                "userfortestA",
                                false
                        )))
                )
                .andExpect(status().isNotFound());
    }

}