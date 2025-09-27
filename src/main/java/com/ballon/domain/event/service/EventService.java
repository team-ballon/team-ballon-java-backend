package com.ballon.domain.event.service;

import com.ballon.domain.coupon.dto.CouponPartnerResponse;
import com.ballon.domain.event.dto.*;
import com.ballon.domain.event.entity.type.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventService {
    Page<EventSearchResponse> searchEvents(EventSearchRequest request, Pageable pageable);

    EventResponse getEventByEventId(Long eventId);

    Page<CouponPartnerResponse> getEventCouponsByEventId(Long eventId, Pageable pageable);

    EventResponse createEvent(EventRequest eventRequest);

    EventResponse updateEvent(Long eventId, EventRequest eventUpdateRequest);

    void deleteEvent(Long eventId);

    Page<EventApplicationResponse> searchEventApplications(EventSearchApplicationRequest request, Pageable pageable);

    void updateStatusByEventApplication(Long eventApplicationId, EventStatus eventStatus);
}
