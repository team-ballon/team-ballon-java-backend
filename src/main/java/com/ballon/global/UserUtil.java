package com.ballon.global;

import com.ballon.global.auth.detail.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {

    private UserUtil() {
    }

    private static CustomUserDetails getCustomUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }

    public static Long getUserId() {
        return getCustomUserDetails().getUserId();
    }

    public static Long getPartnerId() {
        return getCustomUserDetails().getPartnerId();
    }

    public static Long getAdminId() {
        return getCustomUserDetails().getAdminId();
    }
}
