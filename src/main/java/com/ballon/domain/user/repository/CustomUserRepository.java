package com.ballon.domain.user.repository;

import com.ballon.domain.user.dto.UserSearchRequest;
import com.ballon.domain.user.dto.UserSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomUserRepository {
    Page<UserSearchResponse> search(UserSearchRequest req, Pageable pageable);
}
