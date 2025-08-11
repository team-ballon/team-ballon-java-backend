package com.ballon.domain.user.dto;

import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.entity.type.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  유저 정보를 수정하는 dto
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserUpdateRequest {


    /**
     * 이메일
     */
    private String email;

    /**
     * 회원 권한
     */
    private Role role;

    /**
     * 나이
     */
    private Integer age;

    /**
     * 성별
     */
    private Sex sex;

    /**
     * 이름
     */
    private String name;

}
