package com.ballon.domain.event.service.impl;

import com.ballon.domain.coupon.dto.CouponPartnerResponse;
import com.ballon.domain.coupon.repository.CouponRepository;
import com.ballon.domain.event.dto.*;
import com.ballon.domain.event.entity.Event;
import com.ballon.domain.event.entity.EventApplication;
import com.ballon.domain.event.entity.type.EventStatus;
import com.ballon.domain.event.repository.EventApplicationRepository;
import com.ballon.domain.event.repository.EventRepository;
import com.ballon.domain.event.service.EventService;
import com.ballon.global.common.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventApplicationRepository eventApplicationRepository;
    private final CouponRepository couponRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<EventSearchResponse> searchEvents(EventSearchRequest request, Pageable pageable) {
        log.info("이벤트 검색 요청: request={}, pageable={}", request, pageable);
        Page<EventSearchResponse> result = eventRepository.searchEvents(request, pageable);

        log.debug("이벤트 검색 결과: 총 {}건, 총 페이지 {}", result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public EventResponse getEventByEventId(Long eventId) {
        log.info("이벤트 단건 조회 요청: eventId={}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));

        return new EventResponse(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate()
        );
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CouponPartnerResponse> getEventCouponsByEventId(Long eventId, Pageable pageable) {
        log.info("이벤트의 쿠폰 조회 요청: eventId={}", eventId);

        return couponRepository.findCouponsByEventId(eventId, pageable);
    }

    @Override
    public EventResponse createEvent(EventRequest eventRequest) {
        log.info("이벤트 생성 요청: {}", eventRequest);
        Event event = Event.createEvent(eventRequest);

        eventRepository.save(event);

        log.info("이벤트 생성 완료: eventId={}", event.getEventId());

        return new EventResponse(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate()
        );
    }

    @Override
    public EventResponse updateEvent(Long eventId, EventRequest eventUpdateRequest) {
        log.info("이벤트 수정 요청: eventId={}, updateRequest={}", eventId, eventUpdateRequest);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트입니다."));

        event.updateEvent(
                eventUpdateRequest.getTitle(),
                eventUpdateRequest.getDescription(),
                eventUpdateRequest.getStartDate(),
                eventUpdateRequest.getEndDate()
        );
        log.info("이벤트 수정 완료: eventId={}", eventId);

        return new EventResponse(
                event.getEventId(),
                event.getTitle(),
                event.getDescription(),
                event.getStartDate(),
                event.getEndDate()
        );
    }

    @Override
    public void deleteEvent(Long eventId) {
        log.info("이벤트 삭제 요청: eventId={}", eventId);
        eventRepository.deleteById(eventId);

        log.info("이벤트 삭제 완료: eventId={}", eventId);
    }

    @Override
    public Page<EventApplicationResponse> searchEventApplications(EventSearchApplicationRequest request, Pageable pageable) {
        log.info("이벤트 신청 내역 조회 시작: 요청조건={}, 페이지정보={}", request, pageable);
        Page<EventApplicationResponse> responses = eventApplicationRepository.searchApplications(request, pageable);

        log.info("이벤트 신청 내역 조회 완료: 총 {}건, 현재페이지={}, 페이지크기={}",
                responses.getTotalElements(),
                pageable.getPageNumber(),
                pageable.getPageSize());

        return responses;
    }

    @Override
    public void updateStatusByEventApplication(Long eventApplicationId, EventStatus eventStatus) {
        log.info("이벤트 신청 상태 수정 요청: eventApplicationId={}, eventStatus={}", eventApplicationId, eventStatus);
        EventApplication eventApplication = eventApplicationRepository.findById(eventApplicationId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 이벤트 신청입니다."));

        eventApplication.updateStatus(eventStatus);

        log.info("이벤트 신청 상태 수정 완료: eventApplicationId={}", eventApplicationId);
    }
}
