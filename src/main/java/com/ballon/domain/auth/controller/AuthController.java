package com.ballon.domain.auth.controller;

import com.ballon.domain.auth.dto.JwtResponse;
import com.ballon.domain.auth.dto.LoginRequest;
import com.ballon.domain.auth.service.AuthService;
import com.ballon.domain.user.service.impl.VerificationCodeServiceImpl;
import com.ballon.global.UserUtil;
import com.ballon.global.auth.jwt.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 로그인, 토큰 재발급, 로그아웃
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인, 로그아웃, 토큰 재발급, 인증 코드 관련 API")
public class AuthController {
    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;
    private final VerificationCodeServiceImpl verificationCodeService;

    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호를 이용하여 로그인합니다. AccessToken과 RefreshToken을 발급받습니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그인 성공",
                            content = @Content(schema = @Schema(implementation = JwtResponse.class))),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (아이디 또는 비밀번호 불일치)",
                            content = @Content(schema = @Schema(example = "{\"message\": \"아이디 또는 비밀번호가 다릅니다.\"}"))),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
                            content = @Content(schema = @Schema(example = "{\"message\": \"이미 가입된 이메일입니다.\"}")))
            }
    )
    @PostMapping("/login")
    public JwtResponse login(@RequestBody @Validated LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @Operation(
            summary = "로그아웃",
            description = "현재 로그인된 사용자의 Refresh Token을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
                    @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
            }
    )
    @PostMapping("/logout")
    public void logOut(){
        authService.logOut(UserUtil.getUserId());
    }

    @Operation(
            summary = "Access Token 재발급",
            description = "유효한 Refresh Token을 이용해 새로운 Access Token을 발급합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access Token 재발급 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않음",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "사용자 정보를 찾을 수 없음",
                    content = @Content)
    })
    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestHeader("Authorization") String refreshToken) {
        String tokenValue = refreshToken.replace("Bearer ", "");
        String newAccessToken = jwtTokenUtil.refresh(tokenValue);

        return new JwtResponse(newAccessToken, tokenValue);
    }

    @Operation(
            summary = "이메일 인증 코드 전송",
            description = "입력된 이메일로 인증 코드를 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 코드 전송 성공",
                            content = @Content(schema = @Schema(example = "\"인증 코드가 전송되었습니다.\""))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청",
                            content = @Content(schema = @Schema(example = "{\"message\": \"이메일 전송 실패\"}")))
            }
    )
    @PostMapping("/send-code")
    public String sendCode(@RequestParam String email) {
        verificationCodeService.sendCodeToEmail(email);

        return "인증 코드가 전송되었습니다.";
    }

    @Operation(
            summary = "이메일 인증 코드 검증",
            description = "이메일과 인증 코드를 입력받아 유효성을 검증합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 성공",
                            content = @Content(schema = @Schema(example = "\"인증 성공\""))),
                    @ApiResponse(responseCode = "400", description = "인증 실패",
                            content = @Content(schema = @Schema(example = "\"인증 실패\"")))
            }
    )
    @PostMapping("/verify-code")
    public ResponseEntity<String> verifyCode(
            @RequestParam String email,
            @RequestParam String code) {
        boolean success = verificationCodeService.verifyCode(email, code);

        return success
                ? ResponseEntity.ok("인증 성공")
                : ResponseEntity.badRequest().body("인증 실패");
    }

}
