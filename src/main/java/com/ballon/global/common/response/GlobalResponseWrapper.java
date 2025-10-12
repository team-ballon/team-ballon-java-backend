package com.ballon.global.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
@RestControllerAdvice
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    public GlobalResponseWrapper() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty print
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> clazz = returnType.getParameterType();

        // 순수 String 응답이거나 ResponseEntity<String> 응답은 제외
        if (clazz.equals(String.class)) {
            return false;
        }
        if (ResponseEntity.class.isAssignableFrom(clazz)) {
            // 제네릭 타입 검사
            Type genericType = returnType.getGenericParameterType();
            if (genericType instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericType;
                return !pt.getActualTypeArguments()[0].getTypeName().equals("java.lang.String");
            }
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body instanceof CommonResponse) {
            return body; // 이미 래핑된 경우 중복 래핑 방지
        }

        if (body instanceof byte[] || MediaType.APPLICATION_OCTET_STREAM.equals(selectedContentType)) {
            return body;
        }

        CommonResponse<Object> commonResponse = new CommonResponse<>(body);

        // 로깅: DEBUG 레벨로 변경 (운영 환경에서는 비활성화)
        if (log.isDebugEnabled()) {
            try {
                String jsonLog = objectMapper.writeValueAsString(commonResponse);
                // 응답 크기 제한 (5000자 이상이면 요약)
                if (jsonLog.length() > 5000) {
                    log.debug("\n[API Response] (truncated, size: {})\n{}\n...(truncated)",
                            jsonLog.length(), jsonLog.substring(0, 5000));
                } else {
                    log.debug("\n[API Response]\n{}", jsonLog);
                }
            } catch (Exception e) {
                log.debug("Response data type: {}", body.getClass().getSimpleName());
            }
        }

        return commonResponse;
    }
}

