package delivery_system.cart.domain.repository;

import delivery_system.cart.presentation.dto.MenuDetailsDto;
import java.util.Optional;
import java.util.UUID;

// 메뉴 정보(가격, 이름, 옵션)를 DB에서 조회하는 역할
public interface ItemRepository {
    Optional<MenuDetailsDto> findMenuDetailsById(UUID menuId);
}