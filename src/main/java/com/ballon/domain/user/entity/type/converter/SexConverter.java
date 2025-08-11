package com.ballon.domain.user.entity.type.converter;

import com.ballon.domain.user.entity.type.Sex;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * JPA에서 Sex enum을 DB에 저장하거나 조회할 때 enum name 기반으로 변환하는 컨버터.
 * DB 저장 시: Sex.MALE → "MALE"
 * DB 조회 시: "FEMALE" → Sex.FEMALE
 */
@Converter(autoApply = false) // @Convert 애노테이션이 있는 필드에만 적용
public class SexConverter implements AttributeConverter<Sex, String> {

    /**
     * Sex enum 값을 DB에 저장할 문자열(enum name)로 변환
     * 예: Sex.FEMALE → "FEMALE"
     *
     * @param sex Sex enum 값
     * @return DB에 저장할 문자열(enum name)
     */
    @Override
    public String convertToDatabaseColumn(Sex sex) {
        return sex != null ? sex.name() : null;
    }

    /**
     * DB에서 조회한 문자열(enum name)을 Sex enum으로 변환
     * 예: "MALE" → Sex.MALE
     *
     * @param dbData DB에서 조회된 문자열
     * @return 매칭되는 Sex enum
     */
    @Override
    public Sex convertToEntityAttribute(String dbData) {
        return dbData != null ? Sex.valueOf(dbData) : null;
    }
}
