package com.ballon.domain.user.repository;

import com.ballon.domain.user.entity.UserCoupon;
import com.ballon.domain.user.entity.id.UserCouponId;
import org.springframework.data.repository.CrudRepository;

public interface UserCouponRepository extends CrudRepository<UserCoupon, UserCouponId> {
}
