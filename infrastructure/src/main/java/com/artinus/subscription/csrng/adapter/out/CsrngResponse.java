package com.artinus.subscription.csrng.adapter.out;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
class CsrngResponse {

    private String status;
    private int min;
    private int max;
    private int random;


    public boolean isRandomOne() {
        return random == 1;
    }
}
