package com.ballon.global.auth.jwt.error;

import com.ballon.global.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증 실패 시 호출되는 엔트리 포인트.
 * 인증이 필요한 요청에 인증이 없거나 실패하면 이 클래스가 실행되어
 * 표준화된 JSON 에러 응답을 반환한다.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // JSON 직렬화를 위한 ObjectMapper 인스턴스
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 인증 실패 시 호출되는 메서드.
     * HTTP 응답에 JSON 형식의 에러 메시지를 작성하고 401 상태 코드를 반환한다.
     *
     * @param request       요청 객체
     * @param response      응답 객체
     * @param authException 인증 실패 예외 정보
     * @throws IOException 응답 출력 중 발생하는 예외
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 공통 에러 응답 객체 생성 (code: "UNAUTHORIZED", message: "인증이 필요합니다.")
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "인증이 필요합니다.", HttpStatus.UNAUTHORIZED.value());

        // 응답 헤더에 JSON 콘텐츠 타입 설정
        response.setContentType("application/json");

        // HTTP 상태 코드를 401 (Unauthorized) 로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 에러 응답을 JSON 문자열로 변환하여 응답 본문에 출력
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
