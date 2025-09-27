package com.ballon.domain.event.dto;

import com.ballon.domain.event.entity.type.EventStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventSearchApplicationRequest {
    private EventStatus status;     // 상태 필터 (PENDING, APPROVED)
    private Long eventId;           // 특정 이벤트 ID
    private String partnerName;     // 파트너 이름 검색
}
