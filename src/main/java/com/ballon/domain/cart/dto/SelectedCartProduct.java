package com.ballon.domain.cart.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SelectedCartProduct {
    private Long selectedCartProductId;

    private Integer selectedProductQuantity;
}
