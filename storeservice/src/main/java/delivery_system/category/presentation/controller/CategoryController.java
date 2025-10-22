package delivery_system.category.presentation.controller;
import delivery_system.category.application.service.CategoryService;
import delivery_system.global.infra.dto.CategoryCreateRequest;
import delivery_system.global.infra.dto.CategoryResponse;
import delivery_system.global.infra.dto.CategoryUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor // ✅ 생성자 주입
public class CategoryController {

    private final CategoryService categoryService;

    // --- 1. 카테고리 목록 조회 (가게 사장님/고객 등 모두 사용) ---
    // (이 API는 사장님이 가게 등록 시, 카테고리 목록을 불러올 때 사용합니다)
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    // --- 2. 카테고리 생성 (MANAGER 전용) ---
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')") // ⬅️ MANAGER 역할만 실행 가능
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest categoryCreateRequest, Authentication authentication) {

        //(created_by가 String 타입)
        String managerId = authentication.getName();

        CategoryResponse createdCategory = categoryService.createCategory(categoryCreateRequest, managerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    // --- 3. 카테고리 수정 (MANAGER 전용) ---
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CategoryUpdateRequest update,
            Authentication authentication) {
        String managerId = authentication.getName();
        CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, update, managerId);
        return ResponseEntity.ok(updatedCategory);
    }

    // --- 4. 카테고리 삭제 (MANAGER 전용) ---
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable UUID categoryId,
            Authentication authentication) {

        String managerId = authentication.getName();

        categoryService.deleteCategory(categoryId, managerId); // (소프트 삭제 권장)
        return ResponseEntity.noContent().build();
    }
}