package com.ballon.domain.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentConfirmRequest {
    private Long orderId;
    private int amount;
}
