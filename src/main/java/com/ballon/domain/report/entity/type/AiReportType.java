package com.ballon.domain.report.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

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

    @JsonCreator
    public static AiReportType fromValue(String value) {
        return Arrays.stream(AiReportType.values())
                .filter(t -> t.value.equalsIgnoreCase(value)) // 소문자 스네이크 매칭
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown type: " + value));
    }

    @JsonValue
    public String toValue() {
        return this.value; // 응답 내려줄 때도 소문자 스네이크로
    }
}

