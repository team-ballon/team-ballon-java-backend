package com.ballon.domain.partner.entity;

import com.ballon.domain.admin.entity.AdminPermission;
import com.ballon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "partner")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id")
    private Long partnerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id", // 명시적 참조 컬럼 지정
            foreignKey = @ForeignKey(name = "user_id_fk_1") // FK 이름 지정
    )
    private User user;

    @Column(length = 50, nullable = false)
    private String partnerName;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean active;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(nullable = false, length = 30)
    private String partnerEmail;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PartnerCategory> partnerCategory = new HashSet<>();

    public static Partner createPartner(String partnerName, String overview, String email, User user) {
        return Partner.builder()
                .partnerName(partnerName)
                .overview(overview)
                .partnerEmail(email)
                .user(user)
                .build();
    }

    // 입점업체 정보 업데이트
    public void updatePartner(String partnerName, String overview, String partnerEmail) {
        this.partnerName = partnerName;
        this.overview = overview;
        this.partnerEmail = partnerEmail;
    }

    public void updateActive(Boolean active) {
        this.active = active;
    }

    @PrePersist
    public void prePersist() {
        this.active = false;
        this.createdAt = LocalDateTime.now();
    }
}
