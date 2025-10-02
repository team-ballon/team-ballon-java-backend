package com.ballon.domain.report.entity.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AiReportTypeConverter implements AttributeConverter<AiReportType, String> {

    @Override
    public String convertToDatabaseColumn(AiReportType attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public AiReportType convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return AiReportType.fromValue(dbData);
    }
}

