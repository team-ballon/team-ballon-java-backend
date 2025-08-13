package com.ballon.global.common.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Meta {
    private String timestamp;  // ISO 8601 형식
    private String requestId;

    public Meta() {
        this.timestamp = LocalDateTime.now().toString();
        this.requestId = UUID.randomUUID().toString();
    }
}
