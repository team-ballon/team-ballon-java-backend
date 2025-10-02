package com.ballon.domain.report.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AiReportType {
    REPEAT_RATE_REPORT("repeat_rate_report"),
    PARTNER_SALES_REPORT("partner_sales_report"),
    SUPPLYCHAIN_PARTNER_REPORT("supplychain_partner_report"),
    TOP20_SALES_REPORT("top20_sales_report"),
    REPORT_GENDER_CATEGORY("report_gender_category");

    private final String value;

    AiReportType(String value) {
        this.value = value;
    }

    @JsonValue   // JSON 직렬화할 때 value 사용
    public String getValue() {
        return value;
    }

    @JsonCreator // JSON 역직렬화할 때 value로 Enum 매핑
    public static AiReportType fromValue(String value) {
        for (AiReportType type : AiReportType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown AiReportType: " + value);
    }
}

