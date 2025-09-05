package com.ballon.domain.order.repository;

import com.ballon.domain.order.entity.OrderProduct;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {
}
