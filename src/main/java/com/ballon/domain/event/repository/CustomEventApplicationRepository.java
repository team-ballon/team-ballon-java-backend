package com.ballon.domain.event.repository;

import com.ballon.domain.event.dto.EventSearchApplicationRequest;
import com.ballon.domain.event.dto.EventApplicationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomEventApplicationRepository {
    Page<EventApplicationResponse> searchApplications(EventSearchApplicationRequest request, Pageable pageable);
}
