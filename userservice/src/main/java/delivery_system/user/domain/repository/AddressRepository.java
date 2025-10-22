package delivery_system.user.domain.repository;

import delivery_system.user.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID; // ✅ Address 엔티티의 ID 타입

@Repository // 이 인터페이스를 스프링 빈(Bean)으로 등록
public interface AddressRepository extends JpaRepository<Address, UUID> {

    // JpaRepository<Address, UUID> 의미:
    // "이 리포지토리는 'Address' 엔티티를 관리하고,
    //  그 엔티티의 기본 키(PK) 타입은 'UUID'입니다."

    // (필요한 경우 여기에 커스텀 쿼리 메서드를 추가할 수 있습니다.)
    // 예: user_id로 모든 주소 찾기
    // List<Address> findByUser(User user);
}