package com.positivewand.tourin.web.tourspot;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.positivewand.tourin.domain.tourspot.TourSpotRepository;
import com.positivewand.tourin.domain.tourspot.entity.TourSpot;
import com.positivewand.tourin.domain.tourspot.entity.TourSpotCategory;
import com.positivewand.tourin.web.tourspot.response.TourSpotOverviewResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TourSpotSearchControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TourSpotRepository tourSpotRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;

    private List<TourSpot> testTourSpots = new ArrayList<>();

    @BeforeEach
    void setUpEach() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .apply(sharedHttpSession())
                .build();
    }

    @BeforeAll
    void setUpAll() {
        setUpTourSpot();
    }

    @AfterAll
    void cleanEach() {
        cleanTourSpot();
    }

    void setUpTourSpot() {
        // 테스트 관광지
        double seoulLat = 37.5668174446949;
        double seoulLng = 126.97864943098769;

        double tokyoLat = 35.682839;
        double tokyoLng = 139.759455;

        TourSpotCategory[] tags = {
                TourSpotCategory.EXPERIENCE,
                TourSpotCategory.CULTURE,
                TourSpotCategory.NATURE,
        };

        int[] testBaseIds = {
                1000000000,
                1100000000,
                1200000000
        };
        
        String[] testBaseNames = {
                "가괁지",
                "나괁지",
                "다괁지"
        };

        testTourSpots.clear();

        for (int variant = 0; variant < 3; variant++) {
            for (int i = 0; i < 15; i++) {
                TourSpot testTourSpot = TourSpot.create(
                        testBaseIds[variant]+i,
                        testBaseNames[variant] + i,
                        "관광지 설명...",
                        "대충 이미지 url",
                        "대충 full address",
                        "대충 address1",
                        "대충 address2",
                        111,
                        111,
                        i < 10 ? seoulLat : tokyoLat,
                        i < 10 ? seoulLng : tokyoLng,
                        Arrays.asList(tags[i % 3])
                );

                tourSpotRepository.save(testTourSpot);
                testTourSpots.add(testTourSpot);
            }
        }
    }

    void cleanTourSpot() {
        tourSpotRepository.deleteAll(testTourSpots);
    }

    @Test
    void 사용자는_태그를_이용해_관광지를_검색한다() throws Exception {
        mockMvc.perform(get("/api/tour-spots")
                        .queryParam("tags", "EXPERIENCE")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(jsonPath("$[*].tags", everyItem(
                        hasItem("체험")
                )));
    }

    @Test
    void 사용자는_여러_태그를_이용해_관광지를_검색한다() throws Exception {
        mockMvc.perform(get("/api/tour-spots")
                        .queryParam("tags", "EXPERIENCE")
                        .queryParam("tags", "CULTURE")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(jsonPath("$[*].tags", everyItem(
                        anyOf(
                                hasItem("체험"),
                                hasItem("문화")
                        )
                )));
    }

    @Test
    void 사용자는_키워드를_이용해_관광지를_검색한다() throws Exception {
        mockMvc.perform(get("/api/tour-spots")
                        .queryParam("query", "나괁지")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name", everyItem(startsWith("나괁지"))))
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(header().longValue("X-Pagination-Page", 0))
                .andExpect(header().longValue("X-Pagination-Page-Size", 10))
                .andExpect(header().longValue("X-Pagination-Page-Limit", 10))
                .andExpect(header().longValue("X-Pagination-Total-Page", 2))
                .andExpect(header().longValue("X-Pagination-Total-Item", 15));
    }

    @Test
    void 사용자는_태그와_키워드를_이용해_관광지를_검색한다() throws Exception {
        mockMvc.perform(get("/api/tour-spots")
                        .queryParam("query", "나괁지")
                        .queryParam("tags", "EXPERIENCE")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name", everyItem(startsWith("나괁지"))))
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(jsonPath("$[*].tags", everyItem(
                        hasItem("체험")
                )))
                .andExpect(header().longValue("X-Pagination-Page", 0))
                .andExpect(header().longValue("X-Pagination-Page-Size", 5))
                .andExpect(header().longValue("X-Pagination-Page-Limit", 10))
                .andExpect(header().longValue("X-Pagination-Total-Page", 1))
                .andExpect(header().longValue("X-Pagination-Total-Item", 5));
    }

    @Test
    void 사용자는_키워드를_이용해_관광지를_이름_오름차순_검색한다() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tour-spots")
                        .queryParam("query", "나괁지")
                        .queryParam("sort", "name-asc")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name",  everyItem(startsWith("나괁지"))))
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(header().longValue("X-Pagination-Page", 0))
                .andExpect(header().longValue("X-Pagination-Page-Size", 10))
                .andExpect(header().longValue("X-Pagination-Page-Limit", 10))
                .andExpect(header().longValue("X-Pagination-Total-Page", 2))
                .andExpect(header().longValue("X-Pagination-Total-Item", 15))
                .andReturn();

        List<TourSpotOverviewResponse> actual = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TourSpotOverviewResponse>>() {});
        List<TourSpotOverviewResponse> expected = new ArrayList<>(actual);
        expected.sort(Comparator.comparing(TourSpotOverviewResponse::name));

        assertEquals(actual, expected);
    }

    @Test
    void 사용자는_태그와_키워드를_이용해_관광지를_이름_오름차순_검색한다() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tour-spots")
                        .queryParam("query", "나괁지")
                        .queryParam("tags", "EXPERIENCE")
                        .queryParam("sort", "name-asc")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name", everyItem(startsWith("나괁지"))))
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(jsonPath("$[*].tags", everyItem(
                        hasItem("체험")
                )))
                .andExpect(header().longValue("X-Pagination-Page", 0))
                .andExpect(header().longValue("X-Pagination-Page-Size", 5))
                .andExpect(header().longValue("X-Pagination-Page-Limit", 10))
                .andExpect(header().longValue("X-Pagination-Total-Page", 1))
                .andExpect(header().longValue("X-Pagination-Total-Item", 5))
                .andReturn();

        List<TourSpotOverviewResponse> actual = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TourSpotOverviewResponse>>() {});
        List<TourSpotOverviewResponse> expected = new ArrayList<>(actual);
        expected.sort(Comparator.comparing(TourSpotOverviewResponse::name));

        assertEquals(actual, expected);
    }

    @Test
    void 사용자는_키워드를_이용해_관광지를_이름_내림차순_검색한다() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tour-spots")
                        .queryParam("query", "나괁지")
                        .queryParam("sort", "name-desc")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name", everyItem(startsWith("나괁지"))))
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(header().longValue("X-Pagination-Page", 0))
                .andExpect(header().longValue("X-Pagination-Page-Size", 10))
                .andExpect(header().longValue("X-Pagination-Page-Limit", 10))
                .andExpect(header().longValue("X-Pagination-Total-Page", 2))
                .andExpect(header().longValue("X-Pagination-Total-Item", 15))
                .andReturn();

        List<TourSpotOverviewResponse> actual = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TourSpotOverviewResponse>>() {});
        List<TourSpotOverviewResponse> expected = new ArrayList<>(actual);
        expected.sort(Comparator.comparing(TourSpotOverviewResponse::name).reversed());

        assertEquals(actual, expected);
    }

    @Test
    void 사용자는_태그와_키워드를_이용해_관광지를_이름_내림차순_검색한다() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tour-spots")
                        .queryParam("query", "나괁지")
                        .queryParam("tags", "EXPERIENCE")
                        .queryParam("sort", "name-desc")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name", everyItem(startsWith("나괁지"))))
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(jsonPath("$[*].tags", everyItem(
                        hasItem("체험")
                )))
                .andExpect(header().longValue("X-Pagination-Page", 0))
                .andExpect(header().longValue("X-Pagination-Page-Size", 5))
                .andExpect(header().longValue("X-Pagination-Page-Limit", 10))
                .andExpect(header().longValue("X-Pagination-Total-Page", 1))
                .andExpect(header().longValue("X-Pagination-Total-Item", 5))
                .andReturn();

        List<TourSpotOverviewResponse> actual = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TourSpotOverviewResponse>>() {});
        List<TourSpotOverviewResponse> expected = new ArrayList<>(actual);
        expected.sort(Comparator.comparing(TourSpotOverviewResponse::name).reversed());

        assertEquals(actual, expected);
    }

    @Test
    void 사용자는_태그와_좌표를_이용해_관광지를_검색한다() throws Exception {
        mockMvc.perform(get("/api/tour-spots/map")
                        .queryParam("tags", "EXPERIENCE")
                        .queryParam("minLat", "37.5656995663738")
                        .queryParam("maxLat", "37.5674824316917")
                        .queryParam("minLng", "126.97534746490761")
                        .queryParam("maxLng", "126.98163749845423")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(12))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name").exists())
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(jsonPath("$[*].tags", everyItem(
                        hasItem("체험")
                )));
    }

    @Test
    void 사용자는_키워드와_좌표를_이용해_관광지를_검색한다() throws Exception {
        mockMvc.perform(get("/api/tour-spots/map")
                        .queryParam("query", "나괁지")
                        .queryParam("minLat", "37.5656995663738")
                        .queryParam("maxLat", "37.5674824316917")
                        .queryParam("minLng", "126.97534746490761")
                        .queryParam("maxLng", "126.98163749845423")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name", everyItem(startsWith("나괁지"))))
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists());
    }

    @Test
    void 사용자는_태그와_키워드와_좌표를_이용해_관광지를_검색한다() throws Exception {
        mockMvc.perform(get("/api/tour-spots/map")
                        .queryParam("query", "나괁지")
                        .queryParam("tags", "EXPERIENCE")
                        .queryParam("minLat", "37.5656995663738")
                        .queryParam("maxLat", "37.5674824316917")
                        .queryParam("minLng", "126.97534746490761")
                        .queryParam("maxLng", "126.98163749845423")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[*].id").exists())
                .andExpect(jsonPath("$[*].name", everyItem(startsWith("나괁지"))))
                .andExpect(jsonPath("$[*].address").exists())
                .andExpect(jsonPath("$[*].lat").exists())
                .andExpect(jsonPath("$[*].lng").exists())
                .andExpect(jsonPath("$[*].imageUrl").exists())
                .andExpect(jsonPath("$[*].tags").exists())
                .andExpect(jsonPath("$[*].tags", everyItem(
                        hasItem("체험")
                )));
    }

    @Test
    void 사용자는_검색좌표범위가_너무_넓으면_관광지를_검색할_수_없다() throws Exception {
        mockMvc.perform(get("/api/tour-spots/map")
                        .queryParam("minLat", "37.43945178053661")
                        .queryParam("maxLat", "37.66257687820321")
                        .queryParam("minLng", "126.61274098801715")
                        .queryParam("maxLng", "127.39713633487993 ")
                        .queryParam("pageNo", "1")
                        .queryParam("pageSize", "10")
                )
                .andExpect(status().isBadRequest());
    }
}
