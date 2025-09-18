package com.ballon.domain.settlement.dto;

import com.ballon.domain.settlement.entity.type.SettlementStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Setter
public class SettlementSearchRequest {
    private Long partnerId;
    private LocalDate startDate;
    private LocalDate endDate;
    private SettlementStatus status;
    private String sort; // latest, oldest, amount_high, amount_low
}
