package com.ballon.domain.cart.entity;


import com.ballon.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_item",
       uniqueConstraints = @UniqueConstraint(name = "uk_cart_item", columnNames = {"cart_id", "product_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class CartItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 다대1 (여러 항목이 한 장바구니에)
    @JoinColumn(name = "cart_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cartitem_cart"))
    private Cart cart;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cartitem_product"))
    private Product product;

    private int quantity;

    // 연관관계 세터 (양방향 관리 목적)
    public void setCart(Cart cart) {
        this.cart=cart;
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

    // 여기서는 상품 재고 차감 하지 않음 (결제직전/ 주문확정 시점이 안전하기 때문) 장바구니는 담아두기 용도
}
