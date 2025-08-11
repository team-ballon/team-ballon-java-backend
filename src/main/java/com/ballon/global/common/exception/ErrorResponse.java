package com.ballon.global.common.exception;

import com.ballon.global.common.response.Meta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


// 공통 에러 응답 포맷 클래스
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private ErrorDetail error;
    private Meta meta;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String code;       // 예: "VALIDATION_ERROR"
        private String message;    // 예: "입력 데이터에 오류가 있습니다."
        private List<FieldError> details;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;      // 예: "email"
        private String message;    // 예: "유효한 이메일 주소를 입력해주세요."
    }
}

