package delivery_system.application;
import delivery_system.infra.util.GeoCodingService;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import delivery_system.domain.*;
import delivery_system.security.JwtUtil;
import java.util.concurrent.TimeUnit;
import delivery_system.infra.dto.TokenResponse;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final GeoCodingService geoCodingService;
    private final AddressRepository addressRepository;

    public UserService(
            final UserRepository userRepository,
            final AddressRepository addressRepository,
            final PasswordEncoder passwordEncoder,
            @Qualifier("authRedisTemplate") RedisTemplate<String, String> redisTemplate,
            final JwtUtil jwtUtil,
            final GeoCodingService geoCodingService
    ) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository; // ✅ [추가]
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
        this.jwtUtil = jwtUtil;
        this.geoCodingService = geoCodingService;
    }

    public void signup(String userId, String rawPassword, String rawAddress, String role) {
        if (userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다: " + userId);
        }

        // ✅ 주소를 좌표로 변환
        // ✅ 입력받은 주소를 좌표로 변환하고 address 필드에 저장
        // ✅ 좌표 변환
        Point geom = geoCodingService.getCoordinateAsPoint(rawAddress);
        // ✅ User 객체 생성
        User user = new User();
        user.setUserId(userId);
        user.setRole(UserRole.valueOf(role.toUpperCase()));
        // ✅ 비밀번호 암호화는 도메인 메서드에서 한 번만 수행
        user.signup(rawPassword, passwordEncoder);
        user.setCreated_by(userId); // "생성한 사람"을 자기 자신의 ID로 설정

        // ✅ [수정] save() 대신 saveAndFlush() 사용
        // User의 INSERT 쿼리를 즉시 실행(flush)시켜
        // 'user' 객체를 'persistent' (영속) 상태로 만듭니다.
        userRepository.saveAndFlush(user);

        Address address = new Address();
        address.setUser(user);
        address.setAddressName("우리집");
        address.setAddress(rawAddress);
        address.setGeom(geom);
        address.setIsDefault(true);
        address.setCreated_by(userId);


        addressRepository.save(address); // ⬅️ p_address 테이블에 저장
        System.out.println("✅ User signup completed: " + userId);
    }

    /**
     * ✅ [수정] 로그인 메서드
     * - 반환 타입을 String -> TokenResponseDto로 변경
     * - Access Token, Refresh Token 동시 발급
     * - 두 토큰 모두 Redis에 저장
     */
    public TokenResponse login(String userId, String rawPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // ✅ [수정] Access Token 생성 시 user.getRole() 전달
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        // ✅ [수정] Redis에 두 토큰 모두 저장
        // 1. Access Token 저장 (Key: access:userId)
        redisTemplate.opsForValue().set(
                "access:" + user.getUserId(),
                accessToken,
                1, TimeUnit.HOURS // Access Token 만료 시간
        );

        // 2. Refresh Token 저장 (Key: refresh:userId)
        redisTemplate.opsForValue().set(
                "refresh:" + user.getUserId(),
                refreshToken,
                7, TimeUnit.DAYS // Refresh Token 만료 시간
        );

        System.out.println("✅ 로그인 성공: " + userId);

        // ✅ [수정] DTO에 담아 두 토큰 모두 반환
        return new TokenResponse(accessToken, refreshToken);
    }
}








