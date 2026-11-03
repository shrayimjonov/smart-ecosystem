package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.permission.RolePermission;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.permission.RolePermissionDTO;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.repository.PermissionRepository;
import uz.uzkassa.cts.repository.RolePermissionRepository;
import uz.uzkassa.cts.service.RolePermissionService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class RolePermissionServiceImpl extends BaseServiceImpl<RolePermission, Long> implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;
    private final ValidationContext validationContext;

    public RolePermissionServiceImpl(RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository, ValidationContext validationContext) {
        super(rolePermissionRepository);
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.validationContext = validationContext;
    }

    @Transactional
    public List<String> create(RolePermissionDTO rolePermissionDTO) {
        validationContext.validate(rolePermissionDTO);
        Map<String, RolePermission> rolePermissionsMap = rolePermissionRepository.findAllByRole(rolePermissionDTO.getRole())
                .stream()
                .collect(Collectors.toMap(rolePermission -> rolePermission.getRole().getCode().concat("_").concat(rolePermission.getPermission().getCode()), Function.identity()));

        List<RolePermission> rolePermissions = new ArrayList<>();
        List<String> nonExistentPermissions = new ArrayList<>();
        rolePermissionDTO.getPermissionCodes().forEach(permissionCode -> {
            String key = rolePermissionDTO.getRole().getCode().concat("_").concat(permissionCode);
            if (rolePermissionsMap.get(key) == null) {
                permissionRepository.findFirstByCodeAndDeletedIsFalse(permissionCode)
                        .ifPresentOrElse(permission -> {
                            RolePermission rolePermission = new RolePermission();
                            rolePermission.setRole(rolePermissionDTO.getRole());
                            rolePermission.setPermissionId(permission.getId());
                            rolePermissions.add(rolePermission);
                        }, () -> nonExistentPermissions.add(permissionCode));
            }
            rolePermissionsMap.remove(key);
        });
        if (!CollectionUtils.isEmpty(rolePermissions)) {
            rolePermissionRepository.saveAll(rolePermissions);
        }
        if (!CollectionUtils.isEmpty(rolePermissionsMap)) {
            rolePermissionRepository.deleteAllInBatch(rolePermissionsMap.values());
        }
        return nonExistentPermissions;
    }

    @Override
    public List<CommonCodeDTO> getListByRole(Role role) {
        return rolePermissionRepository.findAllByRole(role).stream().map(rolePermission -> rolePermission.getPermission().toCommonCodeDTO()).collect(Collectors.toList());
    }
}
