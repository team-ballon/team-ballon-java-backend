package com.ballon.global.auth.detail;

import com.ballon.domain.admin.repository.AdminRepository;
import com.ballon.domain.partner.repository.PartnerRepository;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdStr) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(userIdStr)) {
            throw new UsernameNotFoundException("[UserDetailsService] 익명 사용자 요청 (anonymous)");
        }

        Long userId = Long.parseLong(userIdStr);

        com.ballon.domain.user.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("[UserDetailsService] userId=%s 사용자를 찾을 수 없음", userId)));

        Long trainerId = null;
        Long adminId = null;
        String roleStr;

        if (Role.PARTNER == user.getRole()) {
            trainerId = partnerRepository.findPartnerIdByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("%s not found.", userId)));
            roleStr = "PARTNER";
        } else if (Role.ADMIN == user.getRole()) {
            adminId = adminRepository.findAdminIdByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("%s not found.", userId)));
            roleStr = "ADMIN";
        } else {
            roleStr = "USER";
        }

        // 요청한 사용자 로그
        logger.info("[UserDetailsService] 요청한 사용자: userId={}, role={}{}{}",
                userId,
                roleStr,
                trainerId != null ? ", trainerId=" + trainerId : "",
                adminId != null ? ", adminId=" + adminId : "");

        return new CustomUserDetails(
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name(),
                trainerId,
                adminId
        );
    }
}
