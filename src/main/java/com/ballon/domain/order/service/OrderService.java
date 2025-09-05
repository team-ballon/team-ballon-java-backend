package com.ballon.domain.order.service;

import com.ballon.domain.order.dto.OrderRequest;
import com.ballon.domain.order.dto.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
}
