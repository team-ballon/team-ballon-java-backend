package com.ballon.domain.user.dto;

import com.ballon.domain.user.entity.type.Sex;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateRequest {
    private Integer age;

    private Sex sex;

    private String name;

}
