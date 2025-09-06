package com.ballon.domain.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderProductRequest {
    private Long productId;
    private Long couponId;
    private int quantity;
}
