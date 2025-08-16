package com.ballon.domain.address.entity;

import com.ballon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long addressId;

    @Column(length = 200)
    private String detailAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            foreignKey = @ForeignKey(name = "user_id_fk_1")
    )
    private User user;

    public static Address of(String detailAddress, User user) {
        return Address.builder()
                .detailAddress(detailAddress)
                .user(user)
                .build();

    }
}
