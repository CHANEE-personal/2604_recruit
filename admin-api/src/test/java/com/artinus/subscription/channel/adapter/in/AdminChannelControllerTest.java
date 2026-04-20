package com.artinus.subscription.channel.adapter.in;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.artinus.subscription.channel.application.port.in.CreateChannelUseCase;
import com.artinus.subscription.channel.application.port.in.GetChannelQuery;
import com.artinus.subscription.channel.application.port.in.GetChannelQuery.ChannelResponse;
import com.artinus.subscription.channel.domain.enums.ChannelType;
import com.artinus.subscription.common.config.MessageConfig;
import com.artinus.subscription.common.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AdminChannelController.class)
@Import({GlobalExceptionHandler.class, MessageConfig.class})
class AdminChannelControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CreateChannelUseCase createChannelUseCase;
    @MockBean
    GetChannelQuery getChannelQuery;


    @Test
    @DisplayName("채널 생성 요청 성공 시 201을 반환한다")
    void createChannel_success() throws Exception {
        ChannelResponse channel = ChannelResponse.builder()
                .id(1L)
                .name("기본 채널")
                .channelType(ChannelType.BOTH)
                .build();
        given(createChannelUseCase.createChannel(any())).willReturn(channel);

        Map<String, Object> request = Map.of("name", "기본 채널", "channelType", "BOTH");

        mockMvc.perform(post("/api/v1/admin/channels").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("기본 채널"))
                .andExpect(jsonPath("$.data.channelType").value("BOTH"));
    }


    @Test
    @DisplayName("채널 이름 없이 요청 시 400을 반환한다")
    void createChannel_missing_name() throws Exception {
        Map<String, Object> request = Map.of("channelType", "BOTH");

        mockMvc.perform(post("/api/v1/admin/channels").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    @DisplayName("채널 타입 없이 요청 시 400을 반환한다")
    void createChannel_missing_channel_type() throws Exception {
        Map<String, Object> request = Map.of("name", "기본 채널");

        mockMvc.perform(post("/api/v1/admin/channels").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    @DisplayName("채널 목록 조회 성공 시 200과 목록을 반환한다")
    void getChannels_success() throws Exception {
        List<ChannelResponse> channels = List.of(ChannelResponse.builder()
                .id(1L)
                .name("기본 채널")
                .channelType(ChannelType.BOTH)
                .build(), ChannelResponse.builder()
                .id(2L)
                .name("구독 전용")
                .channelType(ChannelType.SUBSCRIBE_ONLY)
                .build());
        given(getChannelQuery.getChannels()).willReturn(channels);

        mockMvc.perform(get("/api/v1/admin/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("기본 채널"));
    }


    @Test
    @DisplayName("채널이 없으면 빈 목록을 반환한다")
    void getChannels_empty() throws Exception {
        given(getChannelQuery.getChannels()).willReturn(List.of());

        mockMvc.perform(get("/api/v1/admin/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
