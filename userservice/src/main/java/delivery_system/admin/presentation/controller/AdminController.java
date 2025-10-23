package delivery_system.admin.presentation.controller;

import delivery_system.admin.application.service.AdminService;
import delivery_system.admin.presentation.dto.request.ReqUpdateRoleDtoV1;
import delivery_system.admin.presentation.dto.response.ResUpdateRoleDtoV1;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value="/admin")
@Transactional
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 권한 변경
    @PutMapping("/role/update/{userId}")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<ResUpdateRoleDtoV1> updateUserRole(
            @PathVariable String userId,
            @Valid @RequestBody ReqUpdateRoleDtoV1 reqUpdateRoleDtoV1
    ) {
        System.out.println("controller user role ====> " + reqUpdateRoleDtoV1.getRole());
        ResUpdateRoleDtoV1 user = adminService.updateUserRole(userId, reqUpdateRoleDtoV1);
        return ResponseEntity.ok(user);
    }

}
