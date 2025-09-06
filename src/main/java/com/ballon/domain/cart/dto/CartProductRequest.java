package com.ballon.domain.cart.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartProductRequest {
    @NotNull @Positive
    private Long productId;
    @NotNull @Min(1)
    private Integer quantity;
}