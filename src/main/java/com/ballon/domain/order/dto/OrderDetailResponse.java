package com.ballon.domain.order.dto;

import com.ballon.domain.address.dto.AddressResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class OrderDetailResponse {
    private String productName;
    private String productImageUrl;
    private AddressResponse address;
    private int quantity;
    private int productAmount;
    private int discountAmount;
    private int paidAmount;
    private LocalDateTime createdAt;
}
