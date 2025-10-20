package delivery_system.domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

////<<< PoEAA / Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserId(String userId); // 중복 체크용
    Optional<User> findByUserId(String userId); // ✅ 유저 조회용
}
