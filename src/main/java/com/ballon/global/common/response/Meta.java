package com.ballon.global.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    private String timestamp;  // ISO 8601 형식
    @JsonProperty(value = "request_id")
    private String requestId;  // 요청 ID
}
