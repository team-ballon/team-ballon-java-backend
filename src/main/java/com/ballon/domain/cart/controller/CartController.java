package com.ballon.domain.cart.controller;

import com.ballon.domain.cart.dto.CartProductRequest;
import com.ballon.domain.cart.dto.CartResponse;
import com.ballon.domain.cart.service.CartService;
import com.ballon.global.UserUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Cart API", description = "장바구니 관련 API")
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final ObjectMapper objectMapper;

    @PostConstruct
    public void printMapper() {
        log.info("NamingStrategy = {}", objectMapper.getPropertyNamingStrategy());
    }

    private final CartService cartService;

    @Operation(
            summary = "내 장바구니 조회",
            description = "현재 로그인한 사용자의 장바구니를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = CartResponse.class)))
            }
    )
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Long userId = UserUtil.getUserId();
        return ResponseEntity.ok(cartService.getMyCart(userId));
    }

    @Operation(
            summary = "장바구니 담기/증가",
            description = "특정 상품을 장바구니에 담거나 이미 담긴 상품의 수량을 증가시킵니다.",
            requestBody = @RequestBody(
                    required = true,
                    description = "상품 ID 및 수량",
                    content = @Content(schema = @Schema(implementation = CartProductRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "추가 성공",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청")
            }
    )
    @PostMapping("/products")
    public ResponseEntity<CartResponse> addProduct(@RequestBody @Validated CartProductRequest req) {
        Long userId = UserUtil.getUserId();
        CartResponse body = cartService.addProduct(userId, req);

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(
            summary = "장바구니 수량 변경",
            description = "장바구니에 담긴 특정 상품의 수량을 변경합니다.",
            parameters = {
                    @Parameter(name = "cart-product-id", description = "장바구니 상품 ID", required = true),
                    @Parameter(name = "quantity", description = "변경할 수량 (1 이상)", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "변경 성공",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "404", description = "장바구니 또는 상품 없음")
            }
    )
    @PutMapping("/products/{cart-product-id}")
    public ResponseEntity<CartResponse> changeQuantity(
            @PathVariable("cart-product-id") Long cartProductId,
            @RequestParam @Min(1) Integer quantity) {

        Long userId = UserUtil.getUserId();
        CartResponse body = cartService.changeQuantity(userId, cartProductId, quantity);

        return ResponseEntity.ok(body);
    }

    @Operation(
            summary = "장바구니 항목 삭제",
            description = "장바구니에서 특정 상품을 제거합니다.",
            parameters = {
                    @Parameter(name = "cart-product-id", description = "장바구니 상품 ID", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "장바구니 또는 상품 없음")
            }
    )
    @DeleteMapping("/products/{cart-product-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(@PathVariable("cart-product-id") Long cartProductId) {
        Long userId = UserUtil.getUserId();
        cartService.removeProduct(userId, cartProductId);
    }
}
