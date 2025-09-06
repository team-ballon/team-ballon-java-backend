package com.ballon.domain.cart.entity;


import com.ballon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cart",
        uniqueConstraints = @UniqueConstraint(name = "uk_cart_user", columnNames = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Cart {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원 1 : 장바구니 1
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_user"))
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartProduct> products = new ArrayList<>();

    // cart는 정적 팩토리로만 생성
    public static Cart create(User user) {
        return Cart.builder()
                .user(user)
                .products(new ArrayList<>())
                .build();
    }

    //장바구니에 아이템 추가
    public void addProduct(CartProduct cartProduct) {
        products.add(cartProduct);
        cartProduct.setCart(this); // 양방향 연관관계 주인 쪽(cart_id) 세팅
    }

    // 장바구니에서 아이템 제거
    public void removeProduct(CartProduct cartProduct) {
        products.remove(cartProduct);
        cartProduct.setCart(null); // 연관 끊기 (orphanRemoval = true 라 DB에서 같이 삭제됨)
    }


}