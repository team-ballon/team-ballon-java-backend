package com.ballon.domain.order.repository;

import com.ballon.domain.order.dto.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomOrderProductRepository {
    Page<OrderSummaryResponse> findAllByUserId(Long userId, Pageable pageable);
}
