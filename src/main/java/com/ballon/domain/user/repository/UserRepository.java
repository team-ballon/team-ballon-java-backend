package com.ballon.domain.user.repository;

import com.ballon.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    Optional<User> findByUserId(Long userId);

    @Query("SELECT u.name FROM User u WHERE u.userId = :userId")
    String getUserNameByUserId(@Param("userId") Long userId);
}