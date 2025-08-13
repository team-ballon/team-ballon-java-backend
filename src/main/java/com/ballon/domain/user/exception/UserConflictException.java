package com.ballon.domain.user.exception;

import com.ballon.global.common.exception.ConflictException;

public class UserConflictException extends ConflictException {
    public UserConflictException() {
        super("이미 존재하는 유저 입니다.");
    }
}
