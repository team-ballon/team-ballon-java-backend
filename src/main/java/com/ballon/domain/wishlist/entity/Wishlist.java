package com.ballon.domain.wishlist.entity;

import com.ballon.domain.product.entity.Product;
import com.ballon.domain.user.entity.User;
import com.ballon.domain.wishlist.entity.id.WishlistId;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "wishlist")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Wishlist {

    @EmbeddedId
    private WishlistId wishlistId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof com.ballon.domain.wishlist.entity.Wishlist that)) return false;
        return Objects.equals(wishlistId, that.wishlistId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wishlistId);
    }
}