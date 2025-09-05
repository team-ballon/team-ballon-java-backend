package com.ballon.domain.cart.entity;


import com.ballon.domain.product.entity.Product;
import com.ballon.global.common.exception.ValidationException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_product",
       uniqueConstraints = @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class CartProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 다대1 (여러 항목이 한 장바구니에)
    @JoinColumn(name = "cart_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cartproduct_cart"))
    private Cart cart;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cartproduct_product"))
    private Product product;

    private int quantity;

    public static CartProduct create(Product product, int quantity) {
        if(product == null) throw new ValidationException("상품은 필수입니다.");
        if (quantity <0) throw new ValidationException("수량은 0 보다 커야 합니다.");
        return CartProduct.builder()
                .product(product)
                .quantity(quantity)
                .build(); // cart는 cart.addItem()에서 연결
    }

    // cart에서만 연관 세팅 가능하도록 패키지-프라이빗
    void setCart(Cart cart) {
        this.cart = cart;
    }

    public void changeQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        this.quantity = quantity;
    }

    public void increase(int delta) {
        int next = this.quantity + delta;
        if (next <= 0) throw new IllegalArgumentException("수량은 1이상이어야 합니다.");
        this.quantity = next;
    }

}
