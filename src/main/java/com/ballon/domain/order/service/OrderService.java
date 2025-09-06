package com.ballon.domain.order.service;

import com.ballon.domain.order.dto.OrderRequest;
import com.ballon.domain.order.dto.OrderResponse;
import com.ballon.domain.order.dto.PaymentConfirmRequest;
import com.ballon.domain.order.dto.PaymentFailRequest;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);

    OrderResponse completeOrder(PaymentConfirmRequest paymentConfirmRequest);

    void failOrder(PaymentFailRequest paymentFailRequest);
}
