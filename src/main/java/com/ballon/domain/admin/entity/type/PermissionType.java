package com.ballon.domain.admin.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PermissionType {
    MANAGE_USER("MANAGE_USER", "사용자 관리"),
    MANAGE_PARTNER("MANAGE_PARTNER", "입점업체 관리"),
    MANAGE_PRODUCT("MANAGE_PRODUCT", "상품 관리"),
    MANAGE_CATEGORY("MANAGE_CATEGORY", "카테고리 관리"),
    MANAGE_EVENT("MANAGE_EVENT", "이벤트 관리");
    /*MANAGE_REFUND("MANAGE_REFUND", "환불 관리"),
    MANAGE_COUPON("MANAGE_COUPON", "쿠폰 관리");*/

    private final String code;
    private final String description;
}

