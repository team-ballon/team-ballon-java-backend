package com.ballon.domain.cart.dto;


import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long cartId;
    private Long userId;
    private List<Product> products;
    private int totalQuantity;
    private int totalPrice;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Product{
        private Long cartProductId;
        private Long productId;
        private String name;
        private Integer price;
        private Integer quantity;
        private Integer lineAmount; // Price * quantity
    }
}
