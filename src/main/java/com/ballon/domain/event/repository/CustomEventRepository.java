package com.ballon.domain.event.repository;

import com.ballon.domain.event.dto.EventSearchRequest;
import com.ballon.domain.event.dto.EventSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomEventRepository {
    Page<EventSearchResponse> searchEvents(EventSearchRequest request, Pageable pageable);
}
