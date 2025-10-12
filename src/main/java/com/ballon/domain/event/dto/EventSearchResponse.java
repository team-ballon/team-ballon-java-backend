package com.ballon.domain.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class EventSearchResponse {
    private Long eventId;
    private String title;
    private LocalDateTime createdAt;
}
