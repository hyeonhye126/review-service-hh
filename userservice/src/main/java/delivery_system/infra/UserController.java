package delivery_system.infra;

import delivery_system.application.UserService;
import delivery_system.infra.dto.LoginRequest;
import delivery_system.infra.dto.SignupRequest;
import delivery_system.infra.dto.TokenResponse;
import delivery_system.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import delivery_system.domain.*;

import java.util.Map;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/users")
@Transactional
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/test")
    public String test() {
        return "UserService is running!";
    }
    @GetMapping("/users/health")
    public String health() {
        return "✅ UserService is running!";
    }

    @PostMapping("/signup")
    public String signup(@Valid @RequestBody SignupRequest request, HttpServletRequest req) {
        System.out.println("📩 [UserController] /signup 요청 도착: " + req.getRequestURI());
        userService.signup(request.getUsername(), request.getPassword(), request.getAddress(), request.getRole());
        return "User signed up: " + request.getUsername();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {try {
        // ✅ [수정] 반환 타입이 String -> TokenResponseDto
        TokenResponse tokenDto = userService.login(request.getUsername(), request.getPassword());

        // ✅ [수정] DTO 객체를 그대로 반환 (JSON으로 직렬화됨)
        return ResponseEntity.ok(tokenDto);

    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "로그인 처리 중 오류가 발생했습니다."));
    }
    }
}
//>>> Clean Arch / Inbound Adaptor
