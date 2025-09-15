package com.ballon.domain.cart.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartProductRequest {
    @NotNull
    private Long productId;
    @NotNull @Min(1)
    private int quantity;
}