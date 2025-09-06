package com.ballon.domain.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrderRequest {
    Long addressId;
    Long partnerId;
    List<OrderProductRequest> products;
}
