package com.ballon.domain.cart.controller;


import com.ballon.domain.cart.dto.CartItemRequest;
import com.ballon.domain.cart.dto.CartResponse;
import com.ballon.domain.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // userId는 헤더나 쿼리로 받음. 보안 미적용
    private Long resolveUserId(Long userId) {
        return userId;
    }

    @Operation(summary = "내 장바구니 조회")
    @GetMapping
    public CartResponse getCart(@RequestParam Long userId) {
        return cartService.getMyCart(resolveUserId(userId));
    }

    @Operation(summary = "장바구니 담기/증가")
    @PostMapping("/items")
    public CartResponse addItem(@RequestParam Long userId,
                                @Valid @RequestBody CartItemRequest req) {
        return cartService.addItem(resolveUserId(userId), req);
    }

    @Operation(summary = "장바구니 수량 변경")
    @PutMapping("/items/{cartItemId}")
    public CartResponse changeQuantity(@RequestParam Long userId,
                                       @PathVariable Long cartItemId,
                                       @RequestParam Integer quantity) {
        return cartService.changeQuantity(resolveUserId(userId), cartItemId, quantity);
    }

    @Operation(summary = "장바구니 항목 삭제")
    @DeleteMapping("/items/{cartItemId}")
    public CartResponse removeItem(@RequestParam Long userId,
                                   @PathVariable Long cartItemId) {
        return cartService.removeItem(resolveUserId(userId), cartItemId);
    }
}
