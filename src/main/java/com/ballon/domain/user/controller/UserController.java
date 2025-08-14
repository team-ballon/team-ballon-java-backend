package com.ballon.domain.user.controller;

import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "회원 관리 API", description = "회원과 관련된 기능")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 이름 등의 정보를 입력받아 새로운 일반 회원(USER)을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
                            content = @Content(schema = @Schema(example = "{\"message\": \"이미 가입된 이메일입니다.\"}")))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestBody @Validated UserRegisterRequest userRegisterRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(userRegisterRequest, Role.USER));
    }
}
