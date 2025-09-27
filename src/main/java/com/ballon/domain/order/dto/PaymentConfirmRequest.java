package com.ballon.domain.order.dto;

import com.ballon.domain.cart.dto.SelectedCartProduct;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PaymentConfirmRequest {
    private Long orderId;
    private int amount;
    private List<SelectedCartProduct> selectedCartProductList;
}
