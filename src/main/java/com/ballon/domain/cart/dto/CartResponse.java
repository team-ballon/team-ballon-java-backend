package com.ballon.domain.cart.dto;


import lombok.*;
import org.eclipse.angus.mail.imap.protocol.Item;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {
    private Long cartId;
    private Long userId;
    private List<Item> items;
    private int totalQuantity;
    private int totalPrice;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item {
        private Long cartItemId;
        private Long productId;
        private String name;
        private Integer price;
        private Integer quantity;
        private Integer lineAmount; // Price * quantity
    }
}
