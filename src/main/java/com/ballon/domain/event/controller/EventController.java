package com.ballon.domain.event.controller;

import com.ballon.domain.event.dto.EventResponse;
import com.ballon.domain.event.dto.EventSearchRequest;
import com.ballon.domain.event.dto.EventSearchResponse;
import com.ballon.domain.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "이벤트 API", description = "이벤트 검색 및 조회 API")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @Operation(summary = "이벤트 검색", description = "이벤트를 키워드, 기간, 진행 여부 조건으로 검색합니다. 기본 정렬은 createdAt DESC입니다.")
    @ApiResponse(responseCode = "200", description = "이벤트 검색 성공",
            content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @GetMapping("/search")
    public Page<EventSearchResponse> searchEvents(
            EventSearchRequest request,
            @Parameter(hidden = true)
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        return eventService.searchEvents(request, pageable);
    }

    @Operation(summary = "이벤트 단건 조회", description = "eventId로 이벤트 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이벤트 조회 성공",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음")
    })
    @GetMapping("/{event-id}")
    public EventResponse getEventById(@PathVariable("event-id") Long eventId) {
        return eventService.getEventByEventId(eventId);
    }
}
