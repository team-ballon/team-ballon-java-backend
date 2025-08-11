package com.ballon.global.common.exception;

import com.fightingkorea.platform.global.common.response.Meta;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 공통 에러 응답 객체 생성용 Factory 클래스
 * ErrorResponse 객체를 일관된 포맷으로 생성
 */
public class ErrorResponseFactory {

    /**
     * 상세 필드 정보 없이 기본 오류 코드와 메시지만 포함된 ErrorResponse 생성
     *
     * @param code    오류 코드 (예: "VALIDATION_ERROR")
     * @param message 오류 메시지
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(String code, String message) {
        return of(code, message, List.of());
    }

    /**
     * 오류 코드, 메시지, 필드 상세 정보를 포함한 ErrorResponse 생성
     *
     * @param code    오류 코드
     * @param message 오류 메시지
     * @param details 필드별 오류 상세 리스트
     * @return ErrorResponse 객체
     */
    public static ErrorResponse of(String code, String message, List<ErrorResponse.FieldError> details) {
        // 오류 상세 정보
        ErrorResponse.ErrorDetail error = new ErrorResponse.ErrorDetail(code, message, details);

        // 공통 메타 정보 (현재 시각 + request ID)
        Meta meta = new Meta(
                ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),  // ISO 형식 타임스탬프
                UUID.randomUUID().toString()                                         // 고유 request ID
        );

        return new ErrorResponse(error, meta);
    }
}
