package com.ballon.domain.event.service;

import com.ballon.domain.event.dto.EventRequest;
import com.ballon.domain.event.dto.EventResponse;
import com.ballon.domain.event.dto.EventSearchRequest;
import com.ballon.domain.event.dto.EventSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    Page<EventSearchResponse> searchEvents(EventSearchRequest request, Pageable pageable);

    EventResponse getEventByEventId(Long eventId);

    EventResponse createEvent(EventRequest eventRequest);

    EventResponse updateEvent(Long eventId, EventRequest eventUpdateRequest);

    void deleteEvent(Long eventId);
}
