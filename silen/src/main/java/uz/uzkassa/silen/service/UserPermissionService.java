package uz.uzkassa.silen.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.domain.UserPermission;
import uz.uzkassa.silen.dto.UserPermissionsDTO;
import uz.uzkassa.silen.repository.pg.PermissionRepository;
import uz.uzkassa.silen.repository.pg.UserPermissionRepository;
import uz.uzkassa.silen.repository.pg.UserRepository;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class UserPermissionService extends BaseService {
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final UserPermissionRepository userPermissionRepository;


    @Transactional
    public void save(UserPermissionsDTO userPermissionsDTO) {
        User user = userRepository.findById(userPermissionsDTO.getUserId()).orElseThrow(notFound("User not found"));
        userPermissionRepository.deleteAllByUserId(user.getId());
        List<UserPermission> list = permissionRepository.findAllByCodeIn(userPermissionsDTO.getPermissions())
                .stream().map(permission -> userPermissionRepository.findFirstByUserIdAndPermissionId(user.getId(), permission.getId()).orElse(new UserPermission(user.getId(), permission.getId())))
                .toList();
        userPermissionRepository.saveAll(list);
    }

    public List<String> getPermissionsByUser(String userId) {
        userRepository.findById(userId).orElseThrow(notFound("User not found"));
        return userPermissionRepository.findAllPermissionCodeByUserId(userId);
    }
}
