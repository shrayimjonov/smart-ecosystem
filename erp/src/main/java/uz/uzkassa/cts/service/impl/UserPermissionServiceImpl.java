package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.permission.UserPermission;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.cts.dto.permission.UserPermissionDTO;
import uz.uzkassa.cts.repository.PermissionRepository;
import uz.uzkassa.cts.repository.UserPermissionRepository;
import uz.uzkassa.cts.service.UserPermissionService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class UserPermissionServiceImpl extends BaseServiceImpl<UserPermission, Long> implements UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final PermissionRepository permissionRepository;
    private final ValidationContext validationContext;

    public UserPermissionServiceImpl(UserPermissionRepository userPermissionRepository, PermissionRepository permissionRepository, ValidationContext validationContext) {
        super(userPermissionRepository);
        this.userPermissionRepository = userPermissionRepository;
        this.permissionRepository = permissionRepository;
        this.validationContext = validationContext;
    }

    @Transactional
    public void create(UserPermissionDTO userPermissionDTO) {
        validationContext.validate(userPermissionDTO);

        Map<String, UserPermission> userPermissionsMap = userPermissionRepository.findAllByUserId(userPermissionDTO.getUserId())
                .stream()
                .collect(Collectors.toMap(rolePermission -> rolePermission.getUser().getId().toString().concat("_").concat(rolePermission.getPermission().getCode()), Function.identity()));

        List<UserPermission> userPermissions = userPermissionDTO.getPermissionCodes().stream().map(permissionCode -> {
            String key = userPermissionDTO.getUserId().toString().concat("_").concat(permissionCode);
            UserPermission userPermission = userPermissionsMap.get(key);
            if (userPermission == null) {
                userPermission = new UserPermission();
                userPermission.setUserId(userPermissionDTO.getUserId());
                userPermission.setPermissionId(permissionRepository.findFirstByCodeAndDeletedIsFalse(permissionCode).get().getId());
            }
            userPermissionsMap.remove(key);
            return userPermission;
        }).collect(Collectors.toList());
        userPermissionRepository.saveAll(userPermissions);
        if (!CollectionUtils.isEmpty(userPermissionsMap)) {
            userPermissionRepository.deleteAllInBatch(userPermissionsMap.values());
        }
    }

    @Transactional
    public void create(UserRolePermissionPayload userRolePermissionPayload) {
        List<UserPermission> userPermissions = permissionRepository.findAllById(userRolePermissionPayload.getPermissions()).stream().map(permission -> {
            UserPermission userPermission = userPermissionRepository
                    .findFirstByUserIdAndPermissionId(userRolePermissionPayload.getUserId(), permission.getId()).orElseGet(UserPermission::new);
            userPermission.setUserId(userRolePermissionPayload.getUserId());
            userPermission.setPermissionId(permission.getId());
            return userPermission;
        }).collect(Collectors.toList());
        userPermissionRepository.saveAll(userPermissions);
    }

    @Override
    public List<CommonCodeDTO> getListByUserId(Long userId) {
        if (userId == null) {
            userId = getCurrentUserId();
        }
        return userPermissionRepository.findAllByUserId(userId).stream().map(userPermission -> userPermission.getPermission().toCommonCodeDTO()).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAllByUserId(Long id) {
        userPermissionRepository.deleteAllByUserId(id);
    }
}
