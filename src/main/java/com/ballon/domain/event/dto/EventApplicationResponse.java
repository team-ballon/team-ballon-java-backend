package com.ballon.domain.event.dto;

import com.ballon.domain.event.entity.type.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EventApplicationResponse {
    Long applicationId;
    EventStatus status;
    LocalDateTime applicationDate;
    String eventTitle;
    String partnerName;
    String partnerEmail;
}
