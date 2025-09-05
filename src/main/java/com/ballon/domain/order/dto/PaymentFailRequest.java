package com.ballon.domain.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentFailRequest {
    private String orderId;
    private String code;
    private String message;
}
