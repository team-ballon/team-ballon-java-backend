package com.ballon.global.common.response;

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
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalResponseWrapper implements ResponseBodyAdvice<Object> {

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

        log.info(commonResponse.toString());

        return commonResponse;
    }
}

