package com.ballon.domain.partner.entity;

import com.ballon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private Boolean active;

    @Column(columnDefinition = "TEXT")
    private String overview;

    @Column(nullable = false, length = 30)
    private String partnerEmail;

    public static Partner createPartner(String name, String overview, String email) {
        return Partner.builder()
                .partnerName(name)
                .overview(overview)
                .partnerEmail(email)
                .build();
    }

    // 입점업체 정보 업데이트
    public void updatePartner(String name, String overview, String email) {
        this.partnerName = name;
        this.overview = overview;
        this.partnerEmail = email;
    }

    @PrePersist
    public void prePersist() {
        this.active = false;
    }
}
