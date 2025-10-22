// src/main/java/delivery_system/cart/domain/entity/Item.java
package delivery_system.cart.domain.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_menu") // ⭐️ DB 테이블명: p_menu
@Where(clause = "deleted_at IS NULL AND is_hidden = false") // 삭제되지 않고 숨김이 아닌 메뉴만 조회
public class Item {

    @Id
    @Column(name = "menu_id", columnDefinition = "uuid") // ⭐️ 메뉴 식별자
    private UUID itemId;

    @Column(name = "store_id", columnDefinition = "uuid", nullable = false) // ⭐️ 가게 식별자
    private UUID storeId;

    @Column(name = "menu_name", length = 200, nullable = false) // ⭐️ 메뉴이름
    private String itemName;

    @Column(name = "menu_fee", nullable = false) // ⭐️ 메뉴가격
    private int basePrice;

    // p_menu_opt_relation 테이블을 통한 다대다 관계 매핑 (Join Table 사용)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "p_menu_opt_relation", // ⭐️ 연결 테이블: p_menu_opt_relation
            joinColumns = @JoinColumn(name = "menu_id"), // 이 엔티티(p_menu)의 컬럼
            inverseJoinColumns = @JoinColumn(name = "menu_opt_id") // 상대 엔티티(p_menu_opt)의 컬럼
    )
    private Set<ItemOpt> options = new HashSet<>(); // ⭐️ ItemOpt 사용 (옵션 그룹)

    // 기타 DB 컬럼 (생략 가능하지만 추가함)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_hidden", nullable = false)
    private boolean isHidden;

    // ... 수정 시각, 삭제 시각 등은 도메인 로직에 필요 없으므로 생략 가능 ...


    /**
     * 특정 옵션 값 ID에 해당하는 가격을 조회합니다.
     * @param optionValueId 사용자가 선택한 옵션 값의 ID (p_menu_opt_value.menu_opt_value_id)
     * @return 해당 옵션 값의 추가 금액
     */
    public int getOptionFeeByValueId(UUID optionValueId) {
        return this.options.stream()
                // 각 옵션 그룹(ItemOpt)에서 해당 값을 찾습니다.
                .flatMap(opt -> opt.getValues().stream())
                // 옵션 값의 ID를 비교합니다.
                .filter(val -> val.getOptionValueId().equals(optionValueId))
                .findFirst()
                .map(ItemOptionValue::getFee) // ⭐️ ItemOptionValue 엔티티 사용
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않거나 활성화되지 않은 옵션 값 ID입니다: " + optionValueId));
    }
}