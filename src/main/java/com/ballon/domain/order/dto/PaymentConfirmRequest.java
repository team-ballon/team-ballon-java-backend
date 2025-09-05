package com.ballon.domain.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentConfirmRequest {
    private String orderId;
    private int amount;
}
