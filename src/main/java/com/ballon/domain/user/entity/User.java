package com.ballon.domain.user.entity;

import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserUpdateRequest;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.entity.type.Sex;
import com.ballon.domain.user.entity.type.converter.RoleConverter;
import com.ballon.domain.user.entity.type.converter.SexConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "\"user\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId; // 회원 고유 ID

    @Column(length = 100, nullable = false)
    private String password; // 암호화된 비밀번호

    @Column(length = 100, unique = true, nullable = false)
    private String email; // 이메일 (로그인 ID)

    @Column
    private Integer age;

    @Convert(converter = RoleConverter.class)
    @Column(nullable = false)
    private Role role; // 회원 권한 (수련생, 선수, 관리자 등)

    @Convert(converter = SexConverter.class)
    @Column(nullable = false)
    private Sex sex; // 회원 권한 (수련생, 선수, 관리자 등)

    @Column(length = 100)
    private String name; // 이름

    @Column
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime createdAt; // 가입 일자

    public static User createUser(UserRegisterRequest userRegisterRequest, Role role) {
        return User.builder()
                .email(userRegisterRequest.getEmail())
                .password(userRegisterRequest.getPassword())
                .age(userRegisterRequest.getAge())
                .sex(userRegisterRequest.getSex())
                .role(role)
                .name(userRegisterRequest.getName())
                .build();
    }

    // 비밀번호 변경
    public void updatePassword(String password) {
        this.password = password;
    }

    // 권한 변경
    public void updateRole(Role role) {
        this.role = role;
    }

    // 회원 정보 업데이트
    public void updateUser(UserUpdateRequest userUpdateRequest) {
        this.age = userUpdateRequest.getAge();
        this.sex = userUpdateRequest.getSex();
        this.name = userUpdateRequest.getName();
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // 엔티티 저장 전 생성 시간 자동 설정
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
