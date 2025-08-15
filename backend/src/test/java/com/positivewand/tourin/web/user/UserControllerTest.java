package com.positivewand.tourin.web.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.domain.tourspot.TourSpotRepository;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotCategory;
import com.positivewand.tourin.domain.user.BookmarkRepository;
import com.positivewand.tourin.domain.user.UserRepository;
import com.positivewand.tourin.domain.user.entity.Bookmark;
import com.positivewand.tourin.domain.user.entity.User;
import com.positivewand.tourin.web.tourspot.request.AddTourSpotReviewRequest;
import com.positivewand.tourin.web.tourspot.request.PutTourSpotReviewLikeRequest;
import com.positivewand.tourin.web.user.request.AddBookmarkRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TourSpotRepository tourSpotRepository;
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpEach() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(sharedHttpSession())
                .build();
        
        // 회원 여러 명 추가
        for (int i = 0; i < 33; i++) {
            userRepository.save(User.create(
                    "dummy"+i,
                    passwordEncoder.encode("password123!"),
                    "더용자"+i,
                    "dummy"+i+"@example.com"
            ));
        }

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

        // 관광지 여러 개(30개 정도?) 추가
        // 북마크 관계 추가 -> 페이지를 형성할 수 있는만큼
        for (int i = 0; i < 33; i++) {
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
                    37.5642135,
                    127.0016985,
                    Arrays.asList(TourSpotCategory.EXPERIENCE)
            );

            tourSpotRepository.save(testTourSpot);

            if (i % 2 == 0) {
                bookmarkRepository.save(Bookmark.create(userA, testTourSpot));
            }
        }
    }

    RequestPostProcessor testUser(String username) {
        return user(customUserDetailsService.loadUserByUsername(username));
    }

    @Test
    void 회원_모음을_조회한다() throws Exception {
        mockMvc.perform(get("/api/users")
                        .queryParam("query", "dummy")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(header().longValue("X-Pagination-Page", 0))
                .andExpect(header().longValue("X-Pagination-Page-Size", 10))
                .andExpect(header().longValue("X-Pagination-Page-Limit", 10))
                .andExpect(header().longValue("X-Pagination-Total-Page", 4))
                .andExpect(header().longValue("X-Pagination-Total-Item", 33));
    }

    @Test
    void 특정_회원을_조회한다() throws Exception {
        mockMvc.perform(get("/api/users/{userId}", "userfortestA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("userfortestA"))
                .andExpect(jsonPath("$.name").value("테용자A"));
    }

    @Test
    void 회원의_북마크_모음을_조회한다() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/bookmarks", "userfortestA")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[*].userId").exists())
                .andExpect(jsonPath("$[*].tourSpotId").exists())
                .andExpect(jsonPath("$[*].tourSpotOverview").exists())
                .andExpect(jsonPath("$[*].tourSpotOverview.name").exists())
                .andExpect(jsonPath("$[*].tourSpotOverview.address").exists())
                .andExpect(jsonPath("$[*].tourSpotOverview.lat").exists())
                .andExpect(jsonPath("$[*].tourSpotOverview.lng").exists())
                .andExpect(jsonPath("$[*].tourSpotOverview.imageUrl").exists())
                .andExpect(jsonPath("$[*].tourSpotOverview.description").exists())
                .andExpect(jsonPath("$[*].tourSpotOverview.phoneNumber").exists())
                .andExpect(jsonPath("$[*].tourSpotOverview.tags").exists())
                .andExpect(header().longValue("X-Pagination-Page", 0))
                .andExpect(header().longValue("X-Pagination-Page-Size", 10))
                .andExpect(header().longValue("X-Pagination-Page-Limit", 10))
                .andExpect(header().longValue("X-Pagination-Total-Page", 2))
                .andExpect(header().longValue("X-Pagination-Total-Item", 17));
    }

    @Test
    void 존재하지_않는_회원의_북마크_모음은_조회할_수_없다() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/bookmarks", "nobody")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void 회원의_북마크에서_특정_관광지를_조회한다() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/bookmarks/{tourSpotId}", "userfortestA", 1000000000))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.tourSpotId").exists())
                .andExpect(jsonPath("$.tourSpotOverview").exists())
                .andExpect(jsonPath("$.tourSpotOverview.name").exists())
                .andExpect(jsonPath("$.tourSpotOverview.address").exists())
                .andExpect(jsonPath("$.tourSpotOverview.lat").exists())
                .andExpect(jsonPath("$.tourSpotOverview.lng").exists())
                .andExpect(jsonPath("$.tourSpotOverview.imageUrl").exists())
                .andExpect(jsonPath("$.tourSpotOverview.description").exists())
                .andExpect(jsonPath("$.tourSpotOverview.phoneNumber").exists())
                .andExpect(jsonPath("$.tourSpotOverview.tags").exists());
    }

    @Test
    void 존재하지_않는_회원의_북마크에서_특정_관광지를_조회할_수_없다() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/bookmarks/{tourSpotId}", "nobody", 1000000000))
                .andExpect(status().isNotFound());
    }

    @Test
    void 회원의_북마크에_존재하지_않는_관광지를_조회할_수_없다() throws Exception {
        mockMvc.perform(get("/api/users/{userId}/bookmarks/{tourSpotId}", "userfortestA", 1100000000))
                .andExpect(status().isNotFound());
    }

    @Test
    void 로그인된_회원은_자신의_북마크에_관광지를_추가한다() throws Exception {
        mockMvc.perform(post("/api/users/{userId}/bookmarks", "userfortestA")
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddBookmarkRequest(1000000001L)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.tourSpotId").exists())
                .andExpect(jsonPath("$.tourSpotOverview").exists())
                .andExpect(jsonPath("$.tourSpotOverview.name").exists())
                .andExpect(jsonPath("$.tourSpotOverview.address").exists())
                .andExpect(jsonPath("$.tourSpotOverview.lat").exists())
                .andExpect(jsonPath("$.tourSpotOverview.lng").exists())
                .andExpect(jsonPath("$.tourSpotOverview.imageUrl").exists())
                .andExpect(jsonPath("$.tourSpotOverview.description").exists())
                .andExpect(jsonPath("$.tourSpotOverview.phoneNumber").exists())
                .andExpect(jsonPath("$.tourSpotOverview.tags").exists());
    }

    @Test
    void 로그인된_회원은_다른_회원의_북마크에_관광지를_추가하지_못한다() throws Exception {
        mockMvc.perform(post("/api/users/{userId}/bookmarks", "userfortestA")
                        .with(testUser("userfortestB"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddBookmarkRequest(1000000001L)))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void 로그인된_회원은_북마크에_중복된_관광지를_추가해도_상관없다() throws Exception {
        mockMvc.perform(post("/api/users/{userId}/bookmarks", "userfortestA")
                        .with(testUser("userfortestA"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddBookmarkRequest(1000000001L)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.tourSpotId").exists())
                .andExpect(jsonPath("$.tourSpotOverview").exists())
                .andExpect(jsonPath("$.tourSpotOverview.name").exists())
                .andExpect(jsonPath("$.tourSpotOverview.address").exists())
                .andExpect(jsonPath("$.tourSpotOverview.lat").exists())
                .andExpect(jsonPath("$.tourSpotOverview.lng").exists())
                .andExpect(jsonPath("$.tourSpotOverview.imageUrl").exists())
                .andExpect(jsonPath("$.tourSpotOverview.description").exists())
                .andExpect(jsonPath("$.tourSpotOverview.phoneNumber").exists())
                .andExpect(jsonPath("$.tourSpotOverview.tags").exists());
    }

    @Test
    void 로그인되지_않은_회원은_북마크에_관광지를_추가하지_못한다() throws Exception {
        mockMvc.perform(post("/api/users/{userId}/bookmarks", "userfortestA")
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddBookmarkRequest(1000000001L)))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그인된_회원은_자신의_북마크에서_관광지를_삭제한다() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}/bookmarks/{tourSpotId}", "userfortestA", 1000000000L)
                .with(testUser("userfortestA"))
        ).andExpect(status().isNoContent());
    }

    @Test
    void 로그인된_회원은_자신의_북마크에_없는_관광지를_삭제해도_상관없다() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}/bookmarks/{tourSpotId}", "userfortestA", 1000000001L)
                .with(testUser("userfortestA"))
        ).andExpect(status().isNoContent());
    }

    @Test
    void 로그인된_회원은_다른_회원의_북마크에_관광지를_삭제하지_못한다() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}/bookmarks/{tourSpotId}", "userfortestA", 1000000000L)
                .with(testUser("userfortestB"))
        ).andExpect(status().isForbidden());
    }

    @Test
    void 로그인되지_않은_회원은_북마크에서_관광지를_삭제하지_못한다() throws Exception {
        mockMvc.perform(delete("/api/users/{userId}/bookmarks/{tourSpotId}", "userfortestA", 1000000000L)
                .with(anonymous())
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void 회원과_관광지_후기의_공감관계를_조회한다() throws Exception {
        List<String> reviewIds = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MvcResult result = mockMvc.perform(post("/api/tour-spots/{tourSpotId}/reviews", 1000000000L + i)
                            .with(testUser("userfortestA"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new AddTourSpotReviewRequest(
                                    "userfortestA",
                                    "안녕하세요, 리뷰입니다!"
                            )))
                    )
                    .andExpect(status().isCreated())
                    .andReturn();

            Map<String, Object> responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Map<String, Object>>() {});
            reviewIds.add((String) responseMap.get("id"));
        }

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(put("/api/tour-spot-reviews/{tourSpotReviewId}/likes", reviewIds.get(i))
                            .with(testUser("userfortestA"))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new PutTourSpotReviewLikeRequest(
                                    "userfortestA",
                                    true
                            )))
                    )
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/users/{userId}/tour-spot-reviews/likes", "userfortestA")
                        .queryParam("tourSpotReviewIds",
                                reviewIds.get(0),
                                reviewIds.get(1),
                                reviewIds.get(2),
                                reviewIds.get(3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].userId").exists())
                .andExpect(jsonPath("$[*].tourSpotReviewId").exists())
                .andExpect(jsonPath("$[*].liked").exists())
                .andExpect(jsonPath("$[?((@.tourSpotReviewId == '" + reviewIds.get(0) + "'))].liked").value(true))
                .andExpect(jsonPath("$[?((@.tourSpotReviewId == '" + reviewIds.get(1) + "'))].liked").value(true))
                .andExpect(jsonPath("$[?((@.tourSpotReviewId == '" + reviewIds.get(2) + "'))].liked").value(true))
                .andExpect(jsonPath("$[?((@.tourSpotReviewId == '" + reviewIds.get(3) + "'))].liked").value(false));

    }
}