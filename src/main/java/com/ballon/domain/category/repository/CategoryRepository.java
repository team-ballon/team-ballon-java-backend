package com.ballon.domain.category.repository;

import com.ballon.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 트리 조립용 전체 로딩
    @Query("select c from Category c left join fetch c.parent")
    List<Category> findAllForTree();
}