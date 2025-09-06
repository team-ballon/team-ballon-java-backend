package com.ballon.domain.order.service;

import com.ballon.domain.order.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);

    OrderResponse completeOrder(PaymentConfirmRequest paymentConfirmRequest);

    void failOrder(PaymentFailRequest paymentFailRequest);

    Page<OrderSummaryResponse> getOrdersByUser(Long userId, Pageable pageable);

    OrderDetailResponse getOrderDetail(Long orderProductId);
}
