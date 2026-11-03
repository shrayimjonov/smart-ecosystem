package uz.uzkassa.silen.service;


import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Permission;
import uz.uzkassa.silen.domain.RolePermission;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.domain.UserPermission;
import uz.uzkassa.silen.dto.PermissionDTO;
import uz.uzkassa.silen.dto.PermissionListDTO;
import uz.uzkassa.silen.dto.RolePermissionDTO;
import uz.uzkassa.silen.dto.mq.UserRolePermissionPayload;
import uz.uzkassa.silen.enumeration.PermissionType;
import uz.uzkassa.silen.enumeration.UserRole;
import uz.uzkassa.silen.mapper.PermissionMapper;
import uz.uzkassa.silen.repository.pg.PermissionRepository;
import uz.uzkassa.silen.repository.pg.RolePermissionRepository;
import uz.uzkassa.silen.repository.pg.UserPermissionRepository;
import uz.uzkassa.silen.repository.pg.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PermissionService extends BaseService {
    PermissionRepository permissionRepository;
    RolePermissionRepository rolePermissionRepository;
    PermissionMapper permissionMapper;
    UserRepository userRepository;
    UserPermissionRepository userPermissionRepository;

    @Transactional
    public Long create(PermissionDTO permissionDTO) {
        if (permissionRepository.findByCodeAndDeletedFalse(permissionDTO.getCode()).isPresent()) {
            throw badRequest("Permission has already been existed with this code: " + permissionDTO.getCode()).get();
        }
        Permission parentPermission = null;
        if (permissionDTO.getParentId() != null) {
            parentPermission = permissionRepository.findById(permissionDTO.getParentId()).orElseThrow(notFound("Parent permission not found"));
        }
        Permission permission = new Permission();
        permission.setName(permissionDTO.getName());
        permission.setCode(permissionDTO.getCode());
        permission.setPosition(permissionDTO.getPosition());
        permission.setSection(permissionDTO.isSection());
        permission.setPermissionType(permissionDTO.getPermissionType());
        if (parentPermission != null) {
            permission.setParent(parentPermission);
            parentPermission.getChildren().add(permission);
        }
        permissionRepository.save(permission);
        return permission.getId();
    }

    @Transactional
    public Long update(Long id, PermissionDTO permissionDTO) {
        if (permissionRepository.findByCodeAndIdNotAndDeletedIsFalse(permissionDTO.getCode(), id).isPresent()) {
            throw badRequest("Permission has already been existed with this code: " + permissionDTO.getCode()).get();
        }
        Permission parentPermission = null;
        if (permissionDTO.getParentId() != null) {
            parentPermission = permissionRepository.findById(permissionDTO.getParentId()).orElseThrow(notFound("Parent permission not found"));
        }

        Permission permission = permissionRepository.findById(id).orElseThrow(notFound("Permission is not found"));
        if (permission.getParent() != null) permission.getParent().getChildren().remove(permission);
        permission.setName(permissionDTO.getName());
        permission.setCode(permissionDTO.getCode());
        permission.setPosition(permissionDTO.getPosition());
        permission.setSection(permissionDTO.isSection());
        permission.setPermissionType(permissionDTO.getPermissionType());
        if (parentPermission != null) {
            permission.setParent(parentPermission);
            parentPermission.getChildren().add(permission);
        }
        if (parentPermission != null) {
            permissionRepository.save(parentPermission);
        }

        return permission.getId();
    }

    public PermissionDTO get(Long id) {
        return permissionRepository
                .findById(id)
                .map(permissionMapper::toDto)
                .orElseThrow(notFound());
    }

    public List<PermissionListDTO> getList(PermissionType type) {
        return recursion(
                permissionRepository.findByParentIdAndPermissionTypeAndDeletedIsFalseOrderByPositionAsc(null, type)
        );
    }

    private List<PermissionListDTO> recursion(Set<Permission> authorities) {
        return authorities
                .stream()
                .map(permission -> {
                    PermissionListDTO permissionListDTO = new PermissionListDTO();
                    permissionListDTO.setId(permission.getId());
                    permissionListDTO.setName(permission.getName());
                    permissionListDTO.setCode(permission.getCode());
                    permissionListDTO.setPosition(permission.getPosition());
                    permissionListDTO.setSection(permission.isSection());
                    permissionListDTO.setParentId(permission.getParentId());
                    permissionListDTO.setPermissionType(permission.getPermissionType());

                    if (CollectionUtils.isNotEmpty(permission.getChildren())) {
                        permissionListDTO.setChildren(recursion(permission.getChildren()));
                    }
                    return permissionListDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Permission permission = permissionRepository
                .findById(id)
                .orElseThrow(notFound());
        rolePermissionRepository.deleteAllByPermissionCode(permission.getCode());
        permissionRepository.delete(permission);
    }

    @Transactional
    public void saveRolePermissions(RolePermissionDTO rolePermissionDTO) {
        List<Permission> permissions = permissionRepository.findAllByCodeIn(rolePermissionDTO.getPermissions());
        rolePermissionRepository.deleteAllByRole(rolePermissionDTO.getRole());
        rolePermissionRepository.flush();

        List<RolePermission> batchInsert = new ArrayList<>();
        List<Long> permissionIds = new ArrayList<>();
        for (Permission permission : permissions) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(rolePermissionDTO.getRole());
            rolePermission.setPermissionCode(permission.getCode());
            batchInsert.add(rolePermission);
            permissionIds.add(permission.getId());
        }
        rolePermissionRepository.saveAllAndFlush(batchInsert);

        final List<String> allUserIdsByRole = userRepository.findAllIdByRole(rolePermissionDTO.getRole());
        allUserIdsByRole.forEach(userId -> rabbitMqProducer.sendRoleChangedEvent(new UserRolePermissionPayload(userId, permissionIds)));
    }

    public Set<String> getRolePermissions(UserRole role) {
        return rolePermissionRepository.findAllPermissionCodesByRole(role);
    }

    @Transactional
    public void roleChangedEvent(UserRolePermissionPayload payload) {
        String userId = payload.getUserId();
        List<UserPermission> permissionsForUser = new ArrayList<>();
        for (Long permissionId : payload.getPermissionIds()) {
            UserPermission userPermission = userPermissionRepository
                    .findFirstByUserIdAndPermissionId(userId, permissionId)
                    .orElse(new UserPermission(userId, permissionId));
            permissionsForUser.add(userPermission);
        }
        userPermissionRepository.saveAllAndFlush(permissionsForUser);
        cacheService.evict(User.class.getSimpleName(), userId);
        cacheService.evict(User.class.getName() + ".authorities", userId);
    }
}

