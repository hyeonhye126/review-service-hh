// src/main/java/delivery_system/cart/domain/entity/ItemOptionValue.java
package delivery_system.cart.domain.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_menu_opt_value") // ⭐️ DB 테이블명: p_menu_opt_value
@Where(clause = "deleted_at IS NULL AND is_active = true") // 삭제되지 않고 활성 상태인 옵션 값만 조회
public class ItemOptValue { // ⭐️ 클래스명 ItemOptionValue

    @Id
    @Column(name = "menu_opt_value_id", columnDefinition = "uuid") // ⭐️ 옵션 식별자
    private UUID optionValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_opt_id", nullable = false) // ⭐️ 옵션그룹 식별자 (FK)
    private delivery_system.cart.domain.entity.ItemOpt option; // ⭐️ ItemOpt 참조 (부모: 옵션 그룹)

    @Column(name = "menu_opt_value_name", length = 100, nullable = false) // ⭐️ 옵션 이름
    private String valueName;

    @Column(name = "menu_opt_value_fee", nullable = false) // ⭐️ 옵션 가격
    private int fee;

    // 기타 DB 컬럼 (is_active 등)
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "sort_order", nullable = false)
    private short sortOrder;
}