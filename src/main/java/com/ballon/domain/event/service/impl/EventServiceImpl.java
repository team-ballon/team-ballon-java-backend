package com.ballon.domain.event.service.impl;

import com.ballon.domain.event.dto.EventRequest;
import com.ballon.domain.event.dto.EventResponse;
import com.ballon.domain.event.dto.EventSearchRequest;
import com.ballon.domain.event.dto.EventSearchResponse;
import com.ballon.domain.event.entity.Event;
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
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("존재하지 않는 이벤트입니다.");
        }

        eventRepository.deleteById(eventId);
        log.info("이벤트 삭제 완료: eventId={}", eventId);
    }
}
