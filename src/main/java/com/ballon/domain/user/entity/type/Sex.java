package com.ballon.domain.user.entity.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Sex {
    MALE("남자"),
    FEMALE("여자");

    private final String label;

    Sex(String label) {
        this.label = label;
    }

    @JsonCreator
    public static Sex fromJson(String input) {
        for (Sex sex : values()) {
            if (sex.label.equals(input)) {
                return sex;
            }
        }
        throw new IllegalArgumentException("Unknown input: " + input);
    }

    @JsonValue
    public String toJson() {
        return this.label;
    }
}
