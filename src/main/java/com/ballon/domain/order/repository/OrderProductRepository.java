package com.ballon.domain.order.repository;

import com.ballon.domain.order.entity.OrderProduct;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {
    List<OrderProduct> findByOrder_OrderId(Long orderId);

    @Query("SELECT op.coupon.couponId " +
            "FROM OrderProduct op " +
            "WHERE op.order.orderId = :orderId AND op.coupon IS NOT NULL")
    List<Long> findCouponIdsByOrderId(@Param("orderId") Long orderId);
}
