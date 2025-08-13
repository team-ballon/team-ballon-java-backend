package com.ballon.global.common.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 응답 타입 검사 생략, 모두 적용
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

        log.info(commonResponse.toString());

        return commonResponse;
    }
}

