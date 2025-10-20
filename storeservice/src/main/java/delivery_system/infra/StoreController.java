package delivery_system.infra;

import java.util.Optional;

import delivery_system.application.StoreService;
import delivery_system.infra.dto.StoreRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import delivery_system.domain.*;

//<<< Clean Arch / Inbound Adaptor
@RestController
@RequestMapping("/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    // 1. 가게 등록 API
    @PostMapping
    @PreAuthorize("hasRole('OWNER')") // ✅ "OWNER" 역할만 이 API 호출 가능
    public ResponseEntity<?> registerStore(
            @RequestBody StoreRequest request,
            Authentication authentication // ✅ Spring Security에서 현재 로그인한 사용자 정보 가져오기
    ) {
        // 1. 토큰에서 사장님(OWNER)의 ID를 가져옵니다.
        String ownerUserId = authentication.getName();

        // 2. 서비스 로직 호출
        Store savedStore = storeService.createStore(ownerUserId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedStore);
//>>> Clean Arch / Inbound Adaptor
