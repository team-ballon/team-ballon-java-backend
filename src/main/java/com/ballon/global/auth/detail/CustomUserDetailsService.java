package com.ballon.global.auth.detail;

import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdStr) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(userIdStr)) {
            throw new UsernameNotFoundException(String.format("%s not found.", userIdStr));
        }

        Long userId = Long.parseLong(userIdStr);

        com.ballon.domain.user.entity.User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("%s not found.", userId)));

        Long trainerId = null;
        if (Role.TRAINER == user.getRole()) {
            trainerId = trainerRepository.findTrainerIdByUserId(userId)
                    .orElseThrow(() -> new UsernameNotFoundException(String.format("%s not found.", userId)));
        }

        return new CustomUserDetails(
                user.getUserId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole().name(),
                trainerId
        );
    }
}
