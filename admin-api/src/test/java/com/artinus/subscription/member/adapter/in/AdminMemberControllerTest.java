package com.artinus.subscription.member.adapter.in;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import com.artinus.subscription.common.config.MessageConfig;
import com.artinus.subscription.common.exception.GlobalExceptionHandler;
import com.artinus.subscription.member.application.port.in.GetMemberQuery;
import com.artinus.subscription.member.application.port.in.GetMemberQuery.MemberResponse;
import com.artinus.subscription.subscription.domain.enums.SubscriptionStatus;

@WebMvcTest(AdminMemberController.class)
@Import({GlobalExceptionHandler.class, MessageConfig.class})
class AdminMemberControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    GetMemberQuery getMemberQuery;


    @Test
    @DisplayName("회원 목록 조회 성공 시 200과 목록을 반환한다")
    void getMembers_success() throws Exception {
        List<MemberResponse> members = List.of(MemberResponse.builder()
                .id(1L)
                .phoneNumber("01012345678")
                .subscriptionStatus(SubscriptionStatus.BASIC)
                .build(), MemberResponse.builder()
                .id(2L)
                .phoneNumber("01099998888")
                .subscriptionStatus(SubscriptionStatus.NONE)
                .build());
        given(getMemberQuery.getMembers()).willReturn(members);

        mockMvc.perform(get("/api/v1/admin/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].phoneNumber").value("01012345678"))
                .andExpect(jsonPath("$.data[0].subscriptionStatus").value("BASIC"));
    }


    @Test
    @DisplayName("회원이 없으면 빈 목록을 반환한다")
    void getMembers_empty() throws Exception {
        given(getMemberQuery.getMembers()).willReturn(List.of());

        mockMvc.perform(get("/api/v1/admin/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
