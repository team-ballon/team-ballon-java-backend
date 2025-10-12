package com.ballon.domain.wishlist.repository;

import com.ballon.domain.wishlist.dto.WishlistResponse;
import com.ballon.domain.wishlist.entity.Wishlist;
import com.ballon.domain.wishlist.entity.id.WishlistId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {
    @Query("""
    SELECT new com.ballon.domain.wishlist.dto.WishlistResponse(
        p.id,
        p.productUrl,
        p.name,
        p.price,
        COALESCE(AVG(r.rating), 0.0),
        COUNT(r)
    )
    FROM Wishlist w
    JOIN w.product p
    LEFT JOIN Review r ON r.product.id = p.id
    WHERE w.user.userId = :userId
    GROUP BY p.id, p.productUrl, p.name, p.price
    ORDER BY p.createdAt DESC
    """)
    List<WishlistResponse> findProductsByUserId(@Param("userId") Long userId);
}
