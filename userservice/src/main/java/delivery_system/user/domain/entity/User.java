package delivery_system.user.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.DomainEvents;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "p_user") // ✅ 실제 테이블명
public class User {
    @Id
    @Column(name = "user_id",unique = true, length = 50)
    private String userId; // ✅ String 타입

    // 🔹 비밀번호 해시
    @Column(nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    //@Column(name = "role", columnDefinition = "role")
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private UserRole role;

    // 🔹 생성/수정/삭제 관리
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime created_at;
    private String created_by;

    @UpdateTimestamp
    private LocalDateTime updated_at;
    private String updated_by;

    private LocalDateTime deleted_at;
    private String deleted_by;

    @Transient
    private final List<Object> domainEvents = new ArrayList<>();

    public void signup(String rawPassword, PasswordEncoder encoder) {
        this.password = encoder.encode(rawPassword);
        //this.domainEvents.add(new SignUp(this));
    }

    public String getId() {
        return userId;
    }

    @DomainEvents
    public Collection<Object> events() {
        return domainEvents;
    }


}
