package com.ballon.domain.admin.controller;

import com.ballon.domain.address.dto.AddressResponse;
import com.ballon.domain.admin.dto.*;
import com.ballon.domain.admin.entity.type.PermissionType;
import com.ballon.domain.admin.service.AdminService;
import com.ballon.domain.admin.service.PermissionService;
import com.ballon.domain.category.dto.CreateCategoryRequest;
import com.ballon.domain.category.service.CategoryService;
import com.ballon.domain.event.dto.EventApplicationResponse;
import com.ballon.domain.event.dto.EventRequest;
import com.ballon.domain.event.dto.EventResponse;
import com.ballon.domain.event.dto.EventSearchApplicationRequest;
import com.ballon.domain.event.entity.type.EventStatus;
import com.ballon.domain.event.service.EventService;
import com.ballon.domain.partner.dto.PartnerResponse;
import com.ballon.domain.partner.dto.PartnerSearchRequest;
import com.ballon.domain.partner.dto.PartnerSearchResponse;
import com.ballon.domain.partner.service.PartnerService;
import com.ballon.domain.product.dto.ProductApplicationSearchRequest;
import com.ballon.domain.product.dto.ProductApplicationSearchResponse;
import com.ballon.domain.product.service.ProductApplicationService;
import com.ballon.domain.report.dto.AiReportResponse;
import com.ballon.domain.report.entity.type.AiReportType;
import com.ballon.domain.report.service.AiReportService;
import com.ballon.domain.settlement.dto.SettlementSearchRequest;
import com.ballon.domain.settlement.dto.SettlementSearchResponse;
import com.ballon.domain.settlement.service.SettlementService;
import com.ballon.domain.user.dto.UserResponse;
import com.ballon.domain.user.dto.UserSearchRequest;
import com.ballon.domain.user.dto.UserSearchResponse;
import com.ballon.domain.user.service.UserService;
import com.ballon.global.UserUtil;
import com.ballon.global.cache.CategoryCacheStore;
import com.ballon.global.common.aop.CheckPermission;
import com.ballon.global.common.aop.CheckSuperAdmin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final PermissionService permissionService;
    private final PartnerService partnerService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventService eventService;
    private final ProductApplicationService productApplicationService;
    private final SettlementService settlementService;
    private final AiReportService aiReportService;

    @Operation(
            summary = "관리자 본인 정보 조회",
            description = "관리자 본인의 정보를 조회할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 본인 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = AddressResponse.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/me")
    public AdminResponse getAdminByAdminId() {
        return adminService.getAdminByAdminId(UserUtil.getAdminId());
    }

    @Operation(
            summary = "관리자 조회",
            description = "기존 관리자를 조회합니다. 슈퍼 관리자가 다른 관리자를 조회할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 조회 성공",
                    content = @Content(schema = @Schema(implementation = AdminResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckSuperAdmin
    @GetMapping("/search")
    public ResponseEntity<Page<AdminResponse>> searchAdmins(
            @Parameter(description = "검색 조건") AdminSearchRequest adminSearchRequest,
            Pageable pageable
    ) {
        return ResponseEntity.ok(adminService.searchAdmins(adminSearchRequest, pageable));
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
        adminService.removeAdminByAdminId(adminId);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "모든 권한 조회",
            description = "모든 권한을 조회합니다. 슈퍼 관리자가 다른 관리자를 생성할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionResponse.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckSuperAdmin
    @GetMapping("/permissions")
    public List<PermissionResponse> findAllPermissions() {

        return permissionService.findAllPermissions();
    }

    @Operation(
            summary = "입점업체 검색",
            description = "이름, 이메일, 활성여부, 카테고리 조건으로 입점업체를 검색합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = PartnerSearchResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/partner/search")
    public Page<PartnerSearchResponse> searchPartners(
            @Parameter(description = "검색 조건") PartnerSearchRequest condition,
            Pageable pageable
    ) {
        return partnerService.searchPartners(condition, pageable);
    }

    @Operation(
            summary = "입점업체 조회",
            description = "기존 입점업체를 수정합니다. 입점업체 관리 권한을 가진 관리자가 입점업체 수정할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "입점업체 조회 성공",
                    content = @Content(schema = @Schema(implementation = PartnerResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckPermission(PermissionType.MANAGE_PARTNER)
    @PutMapping("/partner/{partner-id}")
    public ResponseEntity<PartnerResponse> getPartnerByPartnerId(
            @PathVariable("partner-id") Long partnerId
    ) {
        return ResponseEntity.ok(partnerService.getPartnerByPartnerId(partnerId));
    }

    @Operation(
            summary = "입점업체 활성/비활성",
            description = "입점업체를 활성/비활성합니다. 입점업체 관리 권한을 가진 관리자가 입점업체를 활성/비활성 할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "입점업체 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckPermission(PermissionType.MANAGE_PARTNER)
    @PutMapping("/partner/{partner-id}/active")
    public ResponseEntity<Void> activePartner(@PathVariable("partner-id") Long partnerId, @RequestBody Boolean active) {
        partnerService.activePartnerByPartnerId(partnerId, active);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "카테고리 생성",
            description = "카테고리를 생성합니다. 카테고리 관리 권한을 가진 관리자가 카테고리를 생성 할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 생성 성공",
                    content = @Content(schema = @Schema(implementation = CategoryCacheStore.Node.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckPermission(PermissionType.MANAGE_CATEGORY)
    @PostMapping("/categories")
    public CategoryCacheStore.Node createCategory(@RequestBody CreateCategoryRequest categoryRequest) {
        return categoryService.createCategory(categoryRequest);
    }

    @Operation(
            summary = "카테고리 삭제",
            description = "카테고리를 삭제합니다. 카테고리 관리 권한을 가진 관리자가 카테고리를 삭제 할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckPermission(PermissionType.MANAGE_CATEGORY)
    @PostMapping("/categories/{category-id}")
    public ResponseEntity<Void> createCategory(@PathVariable("category-id") Long categoryId) {
         categoryService.deleteCategory(categoryId);

         return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "사용자 검색",
            description = "이메일, 이름, 나이, 성별, 권한 조건으로 사용자를 검색합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = UserSearchResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckPermission(PermissionType.MANAGE_USER)
    @GetMapping("/users/search")
    public Page<UserSearchResponse> searchUsers(
            @Parameter(name = "검색조건") UserSearchRequest req,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable
    ) {
        return userService.search(req, pageable);
    }


    @Operation(
            summary = "사용자 조회",
            description = "기존 사용자를 검색합니다. 사용자 관리 권한을 가진 관리자가 사용자를 조회 할 때 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckPermission(PermissionType.MANAGE_USER)
    @GetMapping("/users/{user-id}")
    public UserResponse getUserByUserId(@PathVariable("user-id") Long userId) {
        return userService.getUserByUserId(userId);
    }

    @Operation(
            summary = "이벤트 생성",
            description = "새로운 이벤트를 생성합니다. 이벤트 관리 권한을 가진 관리자가 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "이벤트 생성 성공",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckPermission(PermissionType.MANAGE_EVENT)
    @PostMapping("/events")
    public EventResponse createEvent(@RequestBody @Validated EventRequest eventRequest) {
        return eventService.createEvent(eventRequest);
    }

    @Operation(
            summary = "이벤트 수정",
            description = "기존 이벤트를 수정합니다. 이벤트 관리 권한을 가진 관리자가 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이벤트 수정 성공",
                    content = @Content(schema = @Schema(implementation = EventResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음")
    })
    @CheckPermission(PermissionType.MANAGE_EVENT)
    @PutMapping("/events/{event-id}")
    public EventResponse updateEvent(@PathVariable("event-id") Long eventId, @RequestBody EventRequest eventRequest) {
        return eventService.updateEvent(eventId, eventRequest);
    }

    @Operation(
            summary = "이벤트 삭제",
            description = "기존 이벤트를 삭제합니다. 이벤트 관리 권한을 가진 관리자가 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "이벤트 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "이벤트를 찾을 수 없음")
    })
    @CheckPermission(PermissionType.MANAGE_EVENT)
    @DeleteMapping("/events/{event-id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("event-id") Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "이벤트 신청 내역 조회",
            description = "파트너들이 신청한 이벤트 내역을 조건에 따라 조회합니다. 이벤트 관리 권한을 가진 관리자가 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이벤트 신청 내역 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EventApplicationResponse.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckPermission(PermissionType.MANAGE_EVENT)
    @GetMapping("/events/application")
    public Page<EventApplicationResponse> searchEventApplication(
            @Parameter(name = "검색 조건") EventSearchApplicationRequest request,
            Pageable pageable
    ) {
        return eventService.searchEventApplications(request, pageable);
    }

    @Operation(
            summary = "이벤트 신청 상태 변경",
            description = "특정 이벤트 신청의 상태를 변경합니다. (예: PENDING → APPROVED) 이벤트 관리 권한을 가진 관리자가 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "이벤트 신청 상태 변경 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 이벤트 신청을 찾을 수 없음")
    })
    @CheckPermission(PermissionType.MANAGE_EVENT)
    @PutMapping("/events/application/{application-id}")
    public ResponseEntity<Void> updateApplicationStatus(
            @PathVariable("application-id") Long applicationId,
            @RequestBody EventStatus status
            ) {
        eventService.updateStatusByEventApplication(applicationId, status);

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "상품 관련 신청 내역 조회",
            description = "파트너들이 신청한 상품 내역을 조건에 따라 조회합니다. 상품 관리 권한을 가진 관리자가 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이벤트 신청 내역 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductApplicationSearchResponse.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @CheckPermission(PermissionType.MANAGE_PRODUCT)
    @GetMapping("/products/application")
    public Page<ProductApplicationSearchResponse> searchProductApplication(ProductApplicationSearchRequest request, Pageable pageable) {
        return productApplicationService.searchApplications(request, pageable);
    }

    @Operation(
            summary = "정산 내역 조회",
            description = "파트너들의 정산 내역을 조건에 따라 조회합니다. 관리자가 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정산 신청 내역 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SettlementSearchResponse.class)))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/settlement")
    public Page<SettlementSearchResponse> searchSettlement(SettlementSearchRequest request, Pageable pageable) {
        return settlementService.searchSettlements(request, pageable);
    }

    @Operation(
            summary = "AI 리포트 조회",
            description = "AI 리포트를 타입에 따라 조회합니다. 관리자가 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "AI 리포트 조회 성공",
                    content = @Content(schema = @Schema(implementation = AiReportResponse.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/ai-report")
    public AiReportResponse getAiReportResponseByAiReportType(@RequestParam("ai_report_type") String aiReportType) {
        return aiReportService.getAiReportResponseByAiReportType(AiReportType.fromValue(aiReportType));
    }
}
