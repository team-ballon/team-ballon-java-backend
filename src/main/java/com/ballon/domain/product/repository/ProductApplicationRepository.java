package com.ballon.domain.product.repository;

import com.ballon.domain.product.entity.ProductApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductApplicationRepository extends JpaRepository<ProductApplication, Long>, CustomProductApplicationRepository {
}
