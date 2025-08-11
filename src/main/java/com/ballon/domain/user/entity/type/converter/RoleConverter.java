package com.ballon.domain.user.entity.type.converter;

import com.ballon.domain.user.entity.type.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {

        return role != null ? role.name() : null; // 영어 name 저장
    }

    @Override
    public Role convertToEntityAttribute(String dbData) {

        return dbData != null ? Role.valueOf(dbData) : null;
    }
}

