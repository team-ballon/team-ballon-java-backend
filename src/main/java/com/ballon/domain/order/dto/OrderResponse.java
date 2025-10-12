package com.ballon.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class OrderResponse {
    Long orderId;
    int amount;
    String orderName;     // "포트레 네일 외 2건"
    String customerName;  // 유저 이름
}
