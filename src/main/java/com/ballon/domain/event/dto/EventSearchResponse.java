package com.ballon.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventSearchResponse {
    private Long eventId;
    private String title;
    private LocalDateTime createdAt;
}
