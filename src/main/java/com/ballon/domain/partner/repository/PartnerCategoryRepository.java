package com.ballon.domain.partner.repository;

import com.ballon.domain.partner.entity.PartnerCategory;
import org.springframework.data.jpa.repository.*;

public interface PartnerCategoryRepository extends JpaRepository<PartnerCategory, Long> {
}

