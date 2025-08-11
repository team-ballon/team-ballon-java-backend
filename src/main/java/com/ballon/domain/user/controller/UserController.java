package com.ballon.domain.user.controller;

import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 수련생을 등록하는 메서드.
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerTrainee(@RequestBody @Validated UserRegisterRequest userRegisterRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(userRegisterRequest, Role.TRAINEE));
    }

}
