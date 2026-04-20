package com.artinus.subscription.channel.adapter.in;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.artinus.subscription.channel.application.port.in.CreateChannelUseCase;
import com.artinus.subscription.channel.application.port.in.GetChannelQuery;
import com.artinus.subscription.channel.application.port.in.GetChannelQuery.ChannelResponse;
import com.artinus.subscription.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/channels")
class AdminChannelController {

    private final CreateChannelUseCase createChannelUseCase;
    private final GetChannelQuery getChannelQuery;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ChannelResponse> createChannel(
            @Valid @RequestBody ChannelCreateRequest request) {
        return ApiResponse.ok(createChannelUseCase.createChannel(request.toCommand()));
    }


    @GetMapping
    public ApiResponse<List<ChannelResponse>> getChannels() {
        return ApiResponse.ok(getChannelQuery.getChannels());
    }
}
