package com.ballon.domain.order.controller;

import com.ballon.domain.order.dto.OrderRequest;
import com.ballon.domain.order.dto.OrderResponse;
import com.ballon.domain.order.dto.PaymentConfirmRequest;
import com.ballon.domain.order.dto.PaymentFailRequest;
import com.ballon.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "주문 관련 API", description = "주문 생성, 결제 성공/실패 처리 등 주문과 관련된 기능 제공")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "장바구니 또는 단건 상품을 기반으로 주문을 생성한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 생성 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 또는 주소")
    })
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @Operation(summary = "결제 완료 처리", description = "토스 결제 성공 시 주문 상태를 완료(DONE)로 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "결제 완료 처리 성공",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "결제 금액 불일치"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주문")
    })
    @PostMapping("/payments/confirm")
    public OrderResponse completeOrder(@RequestBody PaymentConfirmRequest paymentConfirmRequest) {
        return orderService.completeOrder(paymentConfirmRequest);
    }

    @Operation(summary = "결제 실패 처리", description = "토스 결제 실패 시 주문 상태를 취소(CANCELED)로 변경한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "결제 실패 처리 성공 (응답 본문 없음)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주문")
    })
    @PostMapping("/payments/fail")
    public ResponseEntity<Void> failOrder(@RequestBody PaymentFailRequest paymentFailRequest) {
        orderService.failOrder(paymentFailRequest);

        return ResponseEntity.noContent().build();
    }
}
