package com.artinus.subscription.subscription.adapter.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.artinus.subscription.common.response.ApiResponse;
import com.artinus.subscription.subscription.application.port.in.ChangeSubscriptionUseCase;
import com.artinus.subscription.subscription.application.port.in.GetSubscriptionHistoryQuery;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
@Validated
class SubscriptionController {

    private final ChangeSubscriptionUseCase changeSubscriptionUseCase;
    private final GetSubscriptionHistoryQuery getSubscriptionHistoryQuery;


    @PatchMapping
    public ApiResponse<Void> changeSubscription(@Valid @RequestBody SubscriptionRequest request) {
        changeSubscriptionUseCase.changeSubscription(request.toCommand());
        return ApiResponse.ok("구독 상태가 변경되었습니다.", null);
    }


    @GetMapping("/history")
    public ApiResponse<GetSubscriptionHistoryQuery.HistoryResponse> getHistory(
            @RequestParam @NotBlank(message = "전화번호는 필수입니다.") String phoneNumber) {
        GetSubscriptionHistoryQuery.HistoryResponse response =
                getSubscriptionHistoryQuery.getHistory(phoneNumber);
        return ApiResponse.ok(response);
    }
}
