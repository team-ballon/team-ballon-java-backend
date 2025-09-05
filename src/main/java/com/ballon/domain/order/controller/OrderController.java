package com.ballon.domain.order.controller;

import com.ballon.domain.order.dto.OrderRequest;
import com.ballon.domain.order.dto.OrderResponse;
import com.ballon.domain.order.dto.PaymentConfirmRequest;
import com.ballon.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "주문 관련 API", description = "주문과 관련된 기능")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @PostMapping("/payments/confirm")
    public void completeOrder(@RequestBody PaymentConfirmRequest paymentConfirmRequest) {

    }

    @PatchMapping("/payments/fail")
    public void failOrder(@RequestBody PaymentConfirmRequest paymentConfirmRequest) {

    }
}
