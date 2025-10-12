package com.ballon.global.common.handler;

import com.ballon.global.common.exception.BaseException;
import com.ballon.global.common.response.ErrorResponse;
import com.ballon.global.common.response.Meta;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * 전역 예외 처리 클래스.
 * 컨트롤러에서 발생하는 예외를 공통 형식의 JSON 응답으로 변환하여 반환한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * 유효성 검사 실패 시 발생하는 예외 처리.
     * MethodArgumentNotValidException 또는 BindException을 처리하며,
     * 필드별 오류 정보를 수집하여 표준 에러 응답을 생성한다.
     *
     * @param ex MethodArgumentNotValidException 또는 BindException 인스턴스
     * @return HTTP 422 상태와 표준화된 에러 응답
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("VALIDATION_ERROR", "입력 데이터에 오류가 있습니다.", HttpStatus.UNPROCESSABLE_ENTITY.value(), ex.getMessage());

        logError("ValidationException", errorResponse, ex);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorResponse);
    }

    /**
     * BaseException 계열 예외 처리.
     * 커스텀 예외에서 제공하는 상태 코드, 에러 코드, 메시지, 상세 정보를 포함하여 응답 생성.
     *
     * @param ex BaseException 인스턴스
     * @return 표준화된 에러 응답과 HTTP 상태 코드
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getCode(), ex.getDetails(), ex.getStatus().value(), ex.getMessage());

        logError("BaseException", errorResponse, ex);

        return ResponseEntity.status(ex.getStatus())
                .body(errorResponse);
    }

    /**
     * 알 수 없는 예외 처리.
     * 내부 로깅 후, 클라이언트에는 일반화된 서버 오류 메시지(INTERNAL_SERVER_ERROR)로 응답 반환.
     *
     * @param ex Exception 인스턴스
     * @return HTTP 500 상태와 에러 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());

        logError("UnhandledException", errorResponse, ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    /**
     * 에러 로깅 헬퍼 메서드
     * JSON 형태로 에러 응답을 보기 좋게 출력
     */
    private void logError(String exceptionType, ErrorResponse errorResponse, Exception ex) {
        try {
            String jsonError = objectMapper.writeValueAsString(errorResponse);
            log.error("\n[API Error - {}]\n{}\nException: {}",
                    exceptionType, jsonError, ex.getMessage(), ex);
        } catch (Exception e) {
            // JSON 직렬화 실패 시 기본 로깅
            log.error("{} - code: {}, message: {}, status: {}",
                    exceptionType, errorResponse.getCode(), errorResponse.getMessage(),
                    errorResponse.getStatus(), ex);
        }
    }

}
