package delivery_system.admin.application.service;

import delivery_system.admin.presentation.dto.request.ReqUpdateRoleDtoV1;
import delivery_system.admin.presentation.dto.response.ResUpdateRoleDtoV1;
import delivery_system.user.domain.entity.UserEntity;
import delivery_system.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 읽기 전용
public class AdminService {

    private final UserRepository userRepository;

    // 권한 수정
    public ResUpdateRoleDtoV1 updateUserRole(String userId, ReqUpdateRoleDtoV1 reqUpdateRoleDtoV1) {
        UserEntity userEntity = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        userEntity.setRole(reqUpdateRoleDtoV1.getRole());

        return convertToUpdatedRole(userEntity);
    }

    private ResUpdateRoleDtoV1 convertToUpdatedRole(UserEntity userEntity) {
        ResUpdateRoleDtoV1 response = new ResUpdateRoleDtoV1();
        response.setRole(userEntity.getRole().toString());
        response.setUserId(userEntity.getUserId());
        return response;
    }
}
