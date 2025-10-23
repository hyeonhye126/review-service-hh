package delivery_system.store.presentation.controller;


import delivery_system.global.infra.dto.CategoryCreateRequest;
import delivery_system.store.application.service.StoreService;
import delivery_system.store.presentation.dto.request.ReqCreateStoreDtoV1;
import delivery_system.store.presentation.dto.response.ResGetStoreByIdDtoV1;
import delivery_system.store.presentation.dto.response.ResGetStoresDtoV1;
import delivery_system.store.presentation.dto.response.ResCreateStoreDtoV1;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value="/stores")
@Transactional
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    //전체 가게 조회
    @GetMapping
    public ResponseEntity<List<ResGetStoresDtoV1>> getAllStores() {
        List<ResGetStoresDtoV1> stores = storeService.findAll();
        return ResponseEntity.ok(stores);
    }

    //가게 상세 조회
    @GetMapping("/{store_id}")
    public ResponseEntity<ResGetStoreByIdDtoV1> getStoreById(@PathVariable UUID store_id) {
        ResGetStoreByIdDtoV1 stores = storeService.findByStoreId(store_id);
        return ResponseEntity.ok(stores);
    }

    //가게 등록
    @PostMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ResCreateStoreDtoV1> createStore(
            @Valid @RequestBody ReqCreateStoreDtoV1 reqCreateStoreDtoV1, Authentication authentication
    ) {
        log.info("createStore ====> {}", reqCreateStoreDtoV1  );

        ResCreateStoreDtoV1 resCreateStoreDtoV1 = storeService.createStore(reqCreateStoreDtoV1);
        return ResponseEntity.status(HttpStatus.CREATED).body(resCreateStoreDtoV1);
    }

    //가게 수정
    @PutMapping("/update")
    @PreAuthorize("hasRole('MANAGER')")
    public
}