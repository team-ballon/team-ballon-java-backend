package com.ballon.domain.user.dto;

import com.ballon.domain.user.entity.type.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserUpdateRequest {
    private Integer age;

    private Sex sex;

    private String name;

}
