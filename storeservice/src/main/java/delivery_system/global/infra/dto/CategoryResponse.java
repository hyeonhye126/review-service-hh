package delivery_system.global.infra.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID; // ⬅️ [필수] UUID 타입을 import 해야 합니다.

@Getter
@Setter
@NoArgsConstructor // JSON 처리를 위한 기본 생성자
public class CategoryResponse {

    private UUID categoryId;
    private String categoryName;
    private LocalDateTime createdAt;

    // ⬅️ [참고]
    // public void setCreatedAt(LocalDateTime createdAt) { ... }
    // 이 메서드는 Lombok의 @Setter 어노테이션이 자동으로 만들어주므로
    // 직접 작성할 필요가 없습니다.
}