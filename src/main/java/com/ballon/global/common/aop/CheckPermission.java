package com.ballon.global.common.aop;

import com.ballon.domain.admin.entity.type.PermissionType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckPermission {
    PermissionType value();
}
