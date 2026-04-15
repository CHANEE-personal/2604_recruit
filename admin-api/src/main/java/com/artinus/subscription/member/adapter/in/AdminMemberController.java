package com.artinus.subscription.member.adapter.in;

import com.artinus.subscription.common.response.ApiResponse;
import com.artinus.subscription.member.application.port.in.GetMemberQuery;
import com.artinus.subscription.member.application.port.in.GetMemberQuery.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/members")
class AdminMemberController {

    private final GetMemberQuery getMemberQuery;


    @GetMapping
    public ApiResponse<List<MemberResponse>> getMembers() {
        return ApiResponse.ok(getMemberQuery.getMembers());
    }
}
