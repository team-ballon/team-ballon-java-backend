package com.ballon.domain.user.controller;

import com.ballon.domain.address.dto.AddressRequest;
import com.ballon.domain.address.dto.AddressResponse;
import com.ballon.domain.address.service.AddressService;
import com.ballon.domain.user.dto.PasswordUpdateRequest;
import com.ballon.domain.user.dto.UserRegisterRequest;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.dto.UserUpdateRequest;
import com.ballon.domain.user.entity.type.Role;
import com.ballon.domain.user.repository.VerificationCodeRepository;
import com.ballon.domain.user.service.UserService;
import com.ballon.global.UserUtil;
import com.ballon.global.common.exception.BadRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "회원 관리 API", description = "회원과 관련된 기능")
public class UserController {
    private final UserService userService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AddressService addressService;

    @Operation(
            summary = "회원가입",
            description = "이메일, 비밀번호, 나이, 이름, 성별 등의 정보를 입력받아 새로운 일반 회원(USER)을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원가입 성공",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "인증이 안된 메일",
                            content = @Content(schema = @Schema(example = "{\"message\": \"이메일 인증이 완료되지 않았습니다.\"}"))),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 이메일",
                            content = @Content(schema = @Schema(example = "{\"message\": \"이미 가입된 이메일입니다.\"}")))
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @RequestBody @Validated UserRegisterRequest userRegisterRequest) {
        if(!verificationCodeRepository.existsByEmailAndUsedTrue(userRegisterRequest.getEmail())) {
            throw new BadRequestException("이메일 인증이 완료되지 않았습니다.");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.registerUser(userRegisterRequest, Role.USER));
    }

    @Operation(
            summary = "본인 정보 조회",
            description = "이메일, 비밀번호, 나이, 이름, 성별 등의 정보를 제공합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유저 조회 성공",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                            content = @Content(schema = @Schema(example = "{\"message\": \"존재하지 않는 사용자입니다.\"}")))
            }
    )
    @GetMapping("/me")
    public UserResponse getUserByUserId() {
        return userService.getUserByUserId(UserUtil.getUserId());
    }

    @Operation(
            summary = "배송지 조회",
            description = "본인 배송지 정보를 제공합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "배송지 조회 성공",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AddressResponse.class)))
                    ),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                            content = @Content(schema = @Schema(example = "{\"message\": \"존재하지 않는 사용자입니다.\"}"))
                    )
            }
    )
    @GetMapping("/me/address")
    public List<AddressResponse> getAddressesByUserId() {
        return addressService.findAllAddressByUserId(UserUtil.getUserId());
    }

    @Operation(
            summary = "배송지 추가",
            description = "본인 계정에 새로운 배송지를 추가합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "배송지 등록 성공",
                            content = @Content(schema = @Schema(implementation = AddressResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                            content = @Content(schema = @Schema(example = "{\"message\": \"잘못된 요청입니다.\"}"))
                    )
            }
    )
    @PostMapping("/me/address")
    public ResponseEntity<AddressResponse> addAddress(@RequestBody @Validated AddressRequest addressRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addressService.createAddress(UserUtil.getUserId(), addressRequest));
    }

    @Operation(
            summary = "배송지 수정",
            description = "본인 계정의 배송지 정보를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "배송지 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음",
                            content = @Content(schema = @Schema(example = "{\"message\": \"존재하지 않는 배송지입니다.\"}"))
                    ),
                    @ApiResponse(responseCode = "403", description = "본인이 아님.",
                            content = @Content(schema = @Schema(example = "{\"message\": \"인증 되지 않은 사용자입니다.\"}"))
                    )
            }
    )
    @PutMapping("/me/address/{address-id}")
    public ResponseEntity<Void> updateAddress(@PathVariable("address-id") Long addressId, @RequestBody @Validated AddressRequest addressRequest) {
        addressService.updateAddress(addressId, addressRequest,  UserUtil.getUserId());

        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "배송지 삭제",
            description = "배송지 ID를 통해 본인 계정의 배송지를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "배송지 삭제 성공"
                    ),
                    @ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음",
                            content = @Content(schema = @Schema(example = "{\"message\": \"존재하지 않는 배송지입니다.\"}"))
                    ),
                    @ApiResponse(responseCode = "403", description = "본인이 아님.",
                            content = @Content(schema = @Schema(example = "{\"message\": \"인증 되지 않은 사용자입니다.\"}"))
                    )
            }
    )
    @DeleteMapping("/me/address/{address-id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("address-id") Long addressId) {
        addressService.deleteAddress(addressId,  UserUtil.getUserId());
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "본인 정보 수정",
            description = "나이, 이름, 성별 등의 정보를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "본인 정보 수정 성공",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                            content = @Content(schema = @Schema(example = "{\"message\": \"존재하지 않는 사용자입니다.\"}")))
            }
    )
    @PutMapping("/me")
    public UserResponse updateUser(@RequestBody @Validated UserUpdateRequest userUpdateRequest) {
        return userService.updateUser(userUpdateRequest, UserUtil.getUserId());
    }

    @Operation(
            summary = "본인 비밀번호 수정",
            description = "본인 비밀번호를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "본인 비밀번호 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자",
                            content = @Content(schema = @Schema(example = "{\"message\": \"존재하지 않는 사용자입니다.\"}"))
                    ),
                    @ApiResponse(responseCode = "401", description = "비밀번호 불일치",
                            content = @Content(schema = @Schema(example = "{\"message\": \"비밀번호 불일치.\"}")))
            }
    )
    @PutMapping("/password")
    public ResponseEntity<Void> updateUserPassword(@RequestBody @Validated PasswordUpdateRequest passwordUpdateRequest) {
        userService.updateUserPassword(passwordUpdateRequest);

        return ResponseEntity.noContent().build();
    }
}
