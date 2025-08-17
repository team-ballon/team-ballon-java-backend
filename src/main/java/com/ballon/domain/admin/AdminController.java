package com.ballon.domain.admin;

import com.ballon.domain.admin.dto.AdminRequest;
import com.ballon.domain.admin.dto.AdminResponse;
import com.ballon.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<AdminResponse> register(@RequestBody @Validated AdminRequest adminRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(adminService.createAdmin(adminRequest));
    }
}
