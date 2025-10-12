package com.ballon.domain.settlement.dto;

import com.ballon.domain.settlement.entity.type.SettlementStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class SettlementResponse {
    private Long settlementId;
    private Long partnerId;
    private String partnerName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer totalAmount;
    private SettlementStatus status;
    private LocalDateTime createdAt;
}
