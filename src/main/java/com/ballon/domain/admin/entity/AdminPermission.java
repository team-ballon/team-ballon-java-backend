package com.ballon.domain.admin.entity;

import com.ballon.domain.admin.entity.id.AdminPermissionId;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminPermission that)) return false;
        return Objects.equals(adminPermissionId, that.adminPermissionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminPermissionId);
    }
}
