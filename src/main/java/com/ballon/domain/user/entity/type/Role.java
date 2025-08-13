package com.ballon.domain.user.entity.type;

import lombok.Getter;

@Getter
public enum Role {
    USER("회원"),
    PARTNER("입점업체"),
    ADMIN("관리자");

    private final String label;

    Role(String label) {

        this.label = label;
    }

    //기본값(영어)= name(), 한글레이블(사용자 정의) =label()
}
