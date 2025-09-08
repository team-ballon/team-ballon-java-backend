package com.ballon.domain.event.entity;

import com.ballon.domain.event.entity.type.EventStatus;
import com.ballon.domain.partner.entity.Partner;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EventApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_application_id", nullable = false)
    private Long eventApplicationId;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @Column(nullable = false)
    private LocalDateTime applicationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;
}
