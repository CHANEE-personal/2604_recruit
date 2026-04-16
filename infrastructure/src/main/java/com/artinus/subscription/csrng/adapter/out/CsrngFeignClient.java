package com.artinus.subscription.csrng.adapter.out;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "csrng", url = "${csrng.base-url}")
interface CsrngFeignClient {

    @GetMapping("/csrng/csrng.php?min=0&max=1")
    List<CsrngResponse> getRandom();
}
