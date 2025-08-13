package com.ballon.global.common.response;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@Getter
@ToString
public class CommonResponse<T> {
    private final T data;
    private final Meta meta;

    public CommonResponse(T data) {
        this.data = data;
        this.meta = new Meta();
    }

}

