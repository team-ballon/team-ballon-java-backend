package com.ballon.domain.order.controller;

import com.ballon.domain.order.dto.*;
import com.ballon.domain.order.service.OrderService;
import com.ballon.domain.product.dto.ProductSearchResponse;
import com.ballon.global.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상품 또는 주소"),
            @ApiResponse(responseCode = "409", description = "만료된 쿠폰 또는 사용된 쿠폰")
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

    @Operation(
            summary = "내 주문 내역 조회",
            description = "로그인한 사용자의 주문 목록을 페이징 형태로 조회합니다. " +
                    "`page`, `size`, `sort` 파라미터로 페이지네이션과 정렬을 제어할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderSummaryResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<Page<OrderSummaryResponse>> getMyOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByUser(UserUtil.getUserId(), pageable));
    }

    @Operation(
            summary = "주문 상세 조회",
            description = "특정 주문 상품(orderProductId)에 대한 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 주문 상품을 찾을 수 없음")
    })
    @GetMapping("/{order-product-id}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable("order-product-id") Long orderProductId) {
        return ResponseEntity.ok(orderService.getOrderDetail(orderProductId));
    }
}
