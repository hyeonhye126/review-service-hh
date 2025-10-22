package delivery_system.store.presentation.controller;


import delivery_system.store.application.service.StoreService;
import delivery_system.store.presentation.dto.response.ResGetStoreByIdDtoV1;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/stores")
@Transactional
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // 가게 조회
    @GetMapping
    public ResponseEntity<List<ResGetStoreByIdDtoV1>> getAllStores() {
        List<ResGetStoreByIdDtoV1> stores = storeService.findAll();
        return ResponseEntity.ok(stores);
    }
}