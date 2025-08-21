package com.ballon.global;

import com.ballon.domain.admin.entity.Permission;
import com.ballon.domain.admin.entity.type.PermissionType;
import com.ballon.domain.admin.repository.PermissionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionInitializer {

    private final PermissionRepository permissionRepository;

    @PostConstruct
    public void init() {
        // DB에 존재하는 권한 이름 전부 가져오기
        Set<String> existing = permissionRepository.findAll()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        // Enum 돌면서 없는 것만 insert
        List<Permission> toSave = Arrays.stream(PermissionType.values())
                .filter(type -> !existing.contains(type.getCode()))
                .map(type -> Permission.of(type.getCode(), type.getDescription()))
                .toList();

        if (!toSave.isEmpty()) {
            permissionRepository.saveAll(toSave);
        }
    }

}

