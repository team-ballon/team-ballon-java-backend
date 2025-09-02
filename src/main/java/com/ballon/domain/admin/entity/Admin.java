package com.ballon.domain.admin.entity;

import com.ballon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "admin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id", // 명시적 참조 컬럼 지정
            foreignKey = @ForeignKey(name = "user_id_fk_1") // FK 이름 지정
    )
    private User user;

    @Column(length = 50, nullable = false)
    private boolean isSuperAdmin;

    @Column(length = 100, nullable = false)
    private String role;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AdminPermission> adminPermissions = new HashSet<>();

    public void updateRole(String role) {
        this.role = role;
    }

    public static Admin of(User user, String role) {
        return Admin.builder()
                .user(user)
                .isSuperAdmin(false)
                .role(role)
                .build();
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
