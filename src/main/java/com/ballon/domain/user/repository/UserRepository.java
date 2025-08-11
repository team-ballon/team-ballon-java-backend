package com.ballon.domain.user.repository;

import com.ballon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> , UserRepositoryCustom {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findByUserId(Long userId);

    Boolean existsByUserId(Long userId);
}