package com.ballon.domain.admin.entity;

import com.ballon.domain.admin.entity.id.AdminPermissionId;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin_permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminPermission {

    @EmbeddedId
    private AdminPermissionId adminPermissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("adminId")
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private Permission permission;
}
