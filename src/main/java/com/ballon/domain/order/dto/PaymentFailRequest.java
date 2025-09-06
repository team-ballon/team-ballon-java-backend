package com.ballon.domain.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentFailRequest {
    private Long orderId;
    private String code;
    private String message;
}
