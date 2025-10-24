package delivery_system.user.domain.repository;
import delivery_system.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

////<<< PoEAA / Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByUserId(String userId); // 중복 체크용
    Optional<UserEntity> findByUserId(String userId); // ✅ 유저 조회용
}
