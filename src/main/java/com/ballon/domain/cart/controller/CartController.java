package com.ballon.domain.cart.controller;


import com.ballon.domain.cart.dto.CartProductRequest;
import com.ballon.domain.cart.dto.CartResponse;
import com.ballon.domain.cart.service.CartService;
import com.ballon.global.UserUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;



    @Operation(summary = "내 장바구니 조회")
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Long userId = UserUtil.getUserId();
        return ResponseEntity.ok(cartService.getMyCart(userId));
    }

    @Operation(summary = "장바구니 담기/증가")
    @PostMapping("/products")
    public ResponseEntity<CartResponse> addProduct(@RequestBody @Validated CartProductRequest req) {
        Long userId = UserUtil.getUserId();
        CartResponse body = cartService.addProduct(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(body); //201
    }

    @Operation(summary = "장바구니 수량 변경")
    @PutMapping("/products/{cart-product-id}")
    public ResponseEntity<CartResponse> changeQuantity(@PathVariable("cart-product-id") Long cartProductId, @RequestParam @Min(1) Integer quantity) {
        Long userId = UserUtil.getUserId();
        CartResponse body = cartService.changeQuantity(userId, cartProductId, quantity);
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "장바구니 항목 삭제")
    @DeleteMapping("/products/{cart-product-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(@PathVariable("cart-product-id") Long cartProductId) {
        Long userId = UserUtil.getUserId();
        cartService.removeProduct(userId, cartProductId);
    }
}
