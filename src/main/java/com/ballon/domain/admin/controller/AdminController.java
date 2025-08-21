package com.ballon.domain.admin.controller;

import com.ballon.domain.address.dto.AddressResponse;
import com.ballon.domain.admin.dto.AdminRequest;
import com.ballon.domain.admin.dto.AdminResponse;
import com.ballon.domain.admin.dto.AdminSearchRequest;
import com.ballon.domain.admin.dto.AdminUpdateRequest;
import com.ballon.domain.admin.service.AdminService;
import com.ballon.global.common.aop.CheckSuperAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "관리자 API", description = "관리자 RBAC, 관리자 관련 API")
public class AdminController {
    private final AdminService adminService;

    @Operation(
            summary = "관리자 조회",
            description = "기존 관리자를 조회합니다. 슈퍼 관리자가 다른 관리자를 조회할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AddressResponse.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckSuperAdmin
    @GetMapping
    public ResponseEntity<Page<AdminResponse>> searchAdmins(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) List<Long> permissionIds,
            @RequestParam(defaultValue = "latest") String sort,
            Pageable pageable
    ) {
        AdminSearchRequest req = new AdminSearchRequest(email, role, permissionIds, sort);
        return ResponseEntity.ok(adminService.searchAdmins(req, pageable));
    }


    @Operation(
            summary = "관리자 등록",
            description = "새로운 관리자를 등록합니다. 슈퍼 관리자가 다른 관리자를 생성할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "관리자 등록 성공",
                    content = @Content(schema = @Schema(implementation = AdminResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckSuperAdmin
    @PostMapping("/register")
    public ResponseEntity<AdminResponse> createAdmin(@RequestBody @Validated AdminRequest adminRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createAdmin(adminRequest));
    }

    @Operation(
            summary = "관리자 수정",
            description = "기존 관리자를 수정합니다. 슈퍼 관리자가 다른 관리자를 수정할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 수정 성공",
                    content = @Content(schema = @Schema(implementation = AdminResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckSuperAdmin
    @PutMapping("{admin-id}")
    public ResponseEntity<AdminResponse> createAdmin(
            @PathVariable("admin-id") Long adminId,
            @RequestBody @Validated AdminUpdateRequest adminUpdateRequest
    ) {
        return ResponseEntity.ok(adminService.updateAdmin(adminId, adminUpdateRequest));
    }

    @Operation(
            summary = "관리자 삭제",
            description = "기존 관리자를 삭제합니다. 슈퍼 관리자가 다른 관리자를 삭제할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "관리자 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckSuperAdmin
    @DeleteMapping("/{admin-id}")
    public ResponseEntity<Void> removeAdmin(@PathVariable("admin-id") Long adminId) {
        adminService.removeAdmin(adminId);

        return ResponseEntity.noContent().build();
    }
}
