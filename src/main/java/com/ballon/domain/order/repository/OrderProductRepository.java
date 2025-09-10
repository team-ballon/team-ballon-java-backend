package com.ballon.domain.order.repository;

import com.ballon.domain.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderProductRepository extends CrudRepository<OrderProduct, Long>, CustomOrderProductRepository {
    List<OrderProduct> findByOrder_OrderId(Long orderId);

    @Query("SELECT op.coupon.couponId " +
            "FROM OrderProduct op " +
            "WHERE op.order.orderId = :orderId AND op.coupon IS NOT NULL")
    List<Long> findCouponIdsByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT CASE WHEN COUNT(op) > 0 THEN TRUE ELSE FALSE END " +
            "FROM OrderProduct op " +
            "WHERE op.order.user.userId = :userId " +
            "AND op.product.id = :productId " +
            "AND op.order.status = 'DONE'")
    boolean existsPurchasedProductByUser(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT op " +
            "FROM OrderProduct op " +
            "WHERE op.order.user.userId = :userId " +
            "AND op.product.id = :productId " +
            "AND op.order.status = 'DONE'")
    List<OrderProduct> findPurchasedProductsByUser(@Param("userId") Long userId, @Param("productId") Long productId);
}
