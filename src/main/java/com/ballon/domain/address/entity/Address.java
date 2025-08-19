package com.ballon.domain.address.entity;

import com.ballon.domain.address.dto.AddressRequest;
import com.ballon.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String recipient;

    @Column(length = 20, nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private String baseAddress;

    @Column(nullable = false)
    private String detailAddress;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            foreignKey = @ForeignKey(name = "user_id_fk_1")
    )
    private User user;

    public static Address of(AddressRequest addressRequest, User user) {
        return Address.builder()
                .name(addressRequest.getName())
                .recipient(addressRequest.getRecipient())
                .contactNumber(addressRequest.getContactNumber())
                .baseAddress(addressRequest.getBaseAddress())
                .detailAddress(addressRequest.getDetailAddress())
                .user(user)
                .build();
    }

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
