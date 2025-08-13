package com.ballon.global.common.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class NotFoundException extends BaseException {
    public NotFoundException(String details) {
        super(HttpStatus.NOT_FOUND.getReasonPhrase(), "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND, details);
    }
}
