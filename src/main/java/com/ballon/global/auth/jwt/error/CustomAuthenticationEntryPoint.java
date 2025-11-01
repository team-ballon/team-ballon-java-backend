package com.ballon.global.auth.jwt.error;

import com.ballon.global.common.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

        String requestURI = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");
        String method = request.getMethod();
        String userAgent = request.getHeader("User-Agent");
        String clientIp = getClientIp(request);
        String queryString = request.getQueryString();

        log.warn("[Auth Fail] Client IP: {} - {}", clientIp, authException.getMessage());

        log.warn("""
        [Auth Fail]
        - Time: {}
        - Method: {}
        - Request URI: {}
        - Query: {}
        - Client IP: {}
        - Authorization: {}
        - User-Agent: {}
        - Exception: {}
        """,
                java.time.LocalDateTime.now(),
                method,
                requestURI,
                queryString != null ? queryString : "(none)",
                clientIp,
                authHeader,
                userAgent,
                authException.getMessage()
        );

        // 공통 에러 응답 객체 생성 (code: "UNAUTHORIZED", message: "Authentication is required.")
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                "Authentication is required.",
                HttpStatus.UNAUTHORIZED.value()
        );

        // 응답 헤더에 JSON 콘텐츠 타입 설정
        response.setContentType("application/json");

        // HTTP 상태 코드를 401 (Unauthorized) 로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // 에러 응답을 JSON 문자열로 변환하여 응답 본문에 출력
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * 클라이언트 IP 추출 (프록시 환경 대응)
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 여러 IP 중 첫 번째만 추출 (X-Forwarded-For에 다중 IP가 들어올 수 있음)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
