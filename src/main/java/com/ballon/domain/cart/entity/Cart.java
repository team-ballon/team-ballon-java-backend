package com.ballon.domain.cart.entity;


import com.ballon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cart",
        uniqueConstraints = @UniqueConstraint(name = "uk_cart_user", columnNames = "user_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Cart {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원 1 : 장바구니 1
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_cart_user"))
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    //장바구니에 아이템 추가
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this); // 양방향 연관관계 주인 쪽(cart_id) 세팅
    }

    // 장바구니에서 아이템 제거
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null); // 연관 끊기 (orphanRemoval = true 라 DB에서 같이 삭제됨)
    }
}
