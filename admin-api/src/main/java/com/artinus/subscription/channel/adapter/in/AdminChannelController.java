package com.artinus.subscription.channel.adapter.in;

import com.artinus.subscription.channel.application.port.in.CreateChannelUseCase;
import com.artinus.subscription.channel.application.port.in.GetChannelQuery;
import com.artinus.subscription.channel.domain.Channel;
import com.artinus.subscription.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/channels")
@RequiredArgsConstructor
public class AdminChannelController {

    private final CreateChannelUseCase createChannelUseCase;
    private final GetChannelQuery getChannelQuery;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<GetChannelQuery.ChannelResponse> createChannel(
            @Valid @RequestBody ChannelCreateRequest request) {
        Channel channel = createChannelUseCase.createChannel(request.toCommand());
        return ApiResponse.ok(GetChannelQuery.ChannelResponse.from(channel));
    }


    @GetMapping
    public ApiResponse<List<GetChannelQuery.ChannelResponse>> getChannels() {
        return ApiResponse.ok(getChannelQuery.getChannels());
    }
}
