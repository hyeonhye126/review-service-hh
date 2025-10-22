// src/main/java/delivery_system/cart/domain/entity/ItemOpt.java
package delivery_system.cart.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_menu_opt") // ⭐️ DB 테이블명: p_menu_opt
@Where(clause = "deleted_at IS NULL AND is_active = true") // 삭제되지 않고 활성 상태인 그룹만 조회
public class ItemOpt { // ⭐️ 클래스명 ItemOpt

    @Id
    @Column(name = "menu_opt_id", columnDefinition = "uuid") // ⭐️ 옵션그룹 식별자
    private UUID optionId;

    @Column(name = "store_id", columnDefinition = "uuid", nullable = false) // ⭐️ 가게 식별자
    private UUID storeId;

    @Column(name = "menu_opt_name", length = 100, nullable = false) // ⭐️ 옵션그룹이름
    private String optionName;

    // Item과 ItemOpt는 p_menu_opt_relation을 통해 매핑되므로 여기서 @ManyToOne은 사용하지 않습니다.

    // 옵션 그룹(ItemOpt)과 옵션 값(ItemOptionValue)은 일대다 관계입니다.
    @OneToMany(mappedBy = "option", fetch = FetchType.LAZY)
    private Set<ItemOptValue> values = new HashSet<>(); // ⭐️ ItemOptionValue 사용

    // 기타 DB 컬럼 (is_active 등)
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}