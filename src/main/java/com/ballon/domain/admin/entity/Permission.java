package com.ballon.domain.admin.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permission")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Long permissionId;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 400, nullable = false)
    private String description;

    public static Permission of(String name, String description) {
        return Permission.builder()
                .name(name)
                .description(description)
                .build();
    }
}
