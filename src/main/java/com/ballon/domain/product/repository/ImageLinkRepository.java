package com.ballon.domain.product.repository;

import com.ballon.domain.product.entity.ImageLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageLinkRepository extends JpaRepository<ImageLink, Long> {
    @Query("select i.link from ImageLink i where i.product.id = :productId order by i.order asc")
    List<String> findLinksByProductId(@Param("productId") Long productId);
}
