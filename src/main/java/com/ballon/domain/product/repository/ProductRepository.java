package com.ballon.domain.product.repository;

import com.ballon.domain.product.entity.Product;
import com.ballon.domain.product.repository.ProductRepositoryCustom.ProductRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Long>, ProductRepositoryCustom {
}
