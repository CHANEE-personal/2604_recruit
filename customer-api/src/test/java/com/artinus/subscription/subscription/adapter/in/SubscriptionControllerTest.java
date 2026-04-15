package com.artinus.subscription.subscription.adapter.in;

import com.artinus.subscription.common.config.MessageConfig;
import com.artinus.subscription.common.exception.BusinessException;
import com.artinus.subscription.common.exception.ErrorCode;
import com.artinus.subscription.common.exception.GlobalExceptionHandler;
import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionUseCase;
import com.artinus.subscription.subscription.application.port.in.GetSubscriptionHistoryQuery;
import com.artinus.subscription.subscription.domain.SubscriptionStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
@Import({GlobalExceptionHandler.class, MessageConfig.class})
class SubscriptionControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ChangeSubscriptionUseCase changeSubscriptionUseCase;
    @MockBean
    GetSubscriptionHistoryQuery getSubscriptionHistoryQuery;


    @Test
    @DisplayName("구독 요청 성공 시 200을 반환한다")
    void subscribe_success() throws Exception {
        Map<String, Object> request =
                Map.of("phoneNumber", "01012345678", "channelId", 1, "targetStatus", "BASIC");

        mockMvc.perform(patch("/api/v1/subscriptions").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }


    @Test
    @DisplayName("구독 해지 요청 성공 시 200을 반환한다")
    void unsubscribe_success() throws Exception {
        Map<String, Object> request =
                Map.of("phoneNumber", "01012345678", "channelId", 1, "targetStatus", "NONE");

        mockMvc.perform(patch("/api/v1/subscriptions").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }


    @Test
    @DisplayName("전화번호 없이 요청 시 400을 반환한다")
    void changeSubscription_validation_fail() throws Exception {
        Map<String, Object> request = Map.of("channelId", 1, "targetStatus", "BASIC");

        mockMvc.perform(patch("/api/v1/subscriptions").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    @DisplayName("채널 미존재 시 404를 반환한다")
    void changeSubscription_channel_not_found() throws Exception {
        doThrow(new BusinessException(ErrorCode.CHANNEL_NOT_FOUND)).when(changeSubscriptionUseCase)
                .changeSubscription(any());

        Map<String, Object> request =
                Map.of("phoneNumber", "01012345678", "channelId", 99, "targetStatus", "BASIC");

        mockMvc.perform(patch("/api/v1/subscriptions").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    @DisplayName("이력 조회 성공 시 200과 데이터를 반환한다")
    void get_history_success() throws Exception {
        GetSubscriptionHistoryQuery.HistoryResponse response =
                GetSubscriptionHistoryQuery.HistoryResponse.builder()
                        .history(List.of(GetSubscriptionHistoryQuery.HistoryItem.builder()
                                .id(1L)
                                .channelName("기본 채널")
                                .date(LocalDateTime.of(2024, 1, 1, 12, 0))
                                .previousStatus(SubscriptionStatus.NONE)
                                .subscriptionStatus(SubscriptionStatus.BASIC)
                                .build()))
                        .summary("총 1건의 구독 이력이 있습니다.")
                        .build();

        given(getSubscriptionHistoryQuery.getHistory("01012345678")).willReturn(response);

        mockMvc.perform(get("/api/v1/subscriptions/history").param("phoneNumber", "01012345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.history[0].channelName").value("기본 채널"))
                .andExpect(jsonPath("$.data.summary").value("총 1건의 구독 이력이 있습니다."));
    }


    @Test
    @DisplayName("전화번호 없이 이력 조회 시 400을 반환한다")
    void get_history_missing_phone_number() throws Exception {
        mockMvc.perform(get("/api/v1/subscriptions/history"))
                .andExpect(status().isBadRequest());
    }
}
