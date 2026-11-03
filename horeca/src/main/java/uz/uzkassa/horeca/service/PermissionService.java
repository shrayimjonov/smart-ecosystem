package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.permission.Permission;
import uz.uzkassa.horeca.domain.permission.RolePermission;
import uz.uzkassa.horeca.domain.permission.UserPermission;
import uz.uzkassa.horeca.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.horeca.dto.permission.PermissionDTO;
import uz.uzkassa.horeca.dto.permission.PermissionListDTO;
import uz.uzkassa.horeca.dto.permission.SyncPermissionListDTO;
import uz.uzkassa.horeca.enums.PermissionType;
import uz.uzkassa.horeca.enums.Role;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.mappers.PermissionMapper;
import uz.uzkassa.horeca.rabbitmq.producer.RabbitMqPermissionProducer;
import uz.uzkassa.horeca.repository.PermissionRepository;
import uz.uzkassa.horeca.repository.RolePermissionRepository;
import uz.uzkassa.horeca.repository.UserPermissionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PermissionService extends BaseService {

    UserPermissionRepository userPermissionRepository;

    RabbitMqPermissionProducer rabbitMqPermissionProducer;

    PermissionRepository permissionRepository;

    PermissionMapper permissionMapper;

    RolePermissionRepository rolePermissionRepository;

    @Transactional
    public void deleteUserPermissionsByUserId(Long userId) {
        userPermissionRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public Long create(PermissionDTO permissionDTO) {
        if (permissionRepository.findByCodeAndDeletedFalse(permissionDTO.getCode()).isPresent()) {
            throw conflictExceptionThrow("Permission has already been existed with this code: " + permissionDTO.getCode()).get();
        }
        Permission permission = permissionMapper.toEntity(permissionDTO);
        permission = permissionRepository.save(permission);
        return permission.getId();
    }

    @Transactional
    public Long update(Long id, PermissionDTO permissionDTO) {
        if (permissionRepository.findByCodeAndIdNotAndDeletedIsFalse(permissionDTO.getCode(), id).isPresent()) {
            throw conflictExceptionThrow("Permission has already been existed with this code: " + permissionDTO.getCode()).get();
        }
        return permissionRepository
            .findById(id)
            .map(permission -> {
                permission.setCode(permissionDTO.getCode());
                permission.setName(permissionDTO.getName());
                permission.setPosition(permission.getPosition());
                permission.setParentId(permission.getParentId());
                permission.setPermissionType(permissionDTO.getPermissionType());
                permission.setSection(permissionDTO.isSection());
                permissionRepository.save(permission);
                return permission.getId();
            })
            .orElseThrow(notFoundExceptionThrow(Permission.class.getSimpleName(), "id", permissionDTO.getCode()));
    }

    public PermissionDTO get(Long id) {
        return permissionRepository
            .findById(id)
            .map(permissionMapper::toDTO)
            .orElseThrow(notFoundExceptionThrow(Permission.class.getSimpleName(), "id", id));
    }

    public List<PermissionListDTO> getList(BaseFilter filter) {
        return recursion(
            permissionRepository.findByParentIdAndPermissionTypeAndDeletedIsFalseOrderByPositionAsc(
                filter.getParentId(),
                PermissionType.valueOf(filter.getType())
            )
        );
    }

    private List<PermissionListDTO> recursion(Set<Permission> permissions) {
        return permissions
            .stream()
            .map(permission -> {
                PermissionListDTO permissionListDTO = new PermissionListDTO();

                permissionListDTO.setId(permission.getId());
                permissionListDTO.setName(permission.getName());
                permissionListDTO.setPosition(permission.getPosition());
                permissionListDTO.setSection(permission.isSection());
                permissionListDTO.setParentId(permission.getParentId());
                permissionListDTO.setPermissionType(permission.getPermissionType());
                permissionListDTO.setCode(permission.getCode());
                if (!CollectionUtils.isEmpty(permission.getChildren())) {
                    permissionListDTO.setChildren(recursion(permission.getChildren()));
                }
                return permissionListDTO;
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteById(Long id) {
        Permission permission = permissionRepository
            .findById(id)
            .orElseThrow(notFoundExceptionThrow(Permission.class.getSimpleName(), "id", id));
        userPermissionRepository.deleteAllByPermissionId(permission.getId());
        userPermissionRepository.flush();

        rolePermissionRepository.deleteAllByPermissionId(permission.getId());
        permissionRepository.delete(permission);
    }

    @Transactional
    public void saveUserPermissions(Long userId, List<String> permissions) {
        List<UserPermission> userPermissions = new ArrayList<>();
        List<Long> permissionList = new ArrayList<>();
        permissions.forEach(permissionCode -> {
            Permission permission = permissionRepository
                .findByCodeAndDeletedFalse(permissionCode)
                .orElseThrow(notFoundExceptionThrow(Permission.class.getSimpleName(), "code", permissionCode));
            UserPermission userPermission = userPermissionRepository
                .findFirstByUserIdAndPermissionId(userId, permission.getId())
                .orElse(new UserPermission());
            if (userPermission.getId() == null) {
                userPermission.setPermissionId(permission.getId());
                userPermission.setUserId(userId);
            }
            userPermission.setHasAccess(true);
            userPermissions.add(userPermission);
            permissionList.add(permission.getId());
        });
        if (!CollectionUtils.isEmpty(userPermissions)) {
            userPermissionRepository.saveAll(userPermissions);
        }
        userPermissionRepository.updateAccess(userId, permissionList, false);
    }

    public List<String> getUserPermissions(Long userId) {
        return userPermissionRepository.findAllPermissionCodesByUserId(userId);
    }

    @Transactional
    public void saveRolePermissions(Role role, List<String> permissions) {
        boolean existByRole = rolePermissionRepository.existsByRole(role);
        rolePermissionRepository.deleteAllByRole(role);
        rolePermissionRepository.flush();

        List<RolePermission> rolePermissions = new ArrayList<>();
        permissions.forEach(permissionCode -> {
            Permission permission = permissionRepository
                .findByCodeAndDeletedFalse(permissionCode)
                .orElseThrow(notFoundExceptionThrow(Permission.class.getSimpleName(), "code", permissionCode));
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermissionId(permission.getId());
            rolePermissions.add(rolePermission);
        });
        rolePermissionRepository.saveAll(rolePermissions);
//        if (!existByRole) {
            List<Long> userIds = userRepository.getIdsByRoleAndDeletedIsFalse(role);
            userIds.forEach(userId -> {
                rabbitMqPermissionProducer.sendToQueue(new UserRolePermissionPayload(userId, role.getCode(), permissions));
            });
//        }
    }

    public List<String> getRolePermissions(Role role) {
        return rolePermissionRepository.findAllPermissionCodesByRole(role);
    }

    public List<SyncPermissionListDTO> getAllList(BaseFilter filter) {
        return permissionRepository
            .getAll(PermissionType.valueOf(filter.getType()))
            .stream()
            .map(permission -> {
                SyncPermissionListDTO permissionListDTO = wrap(permission);
                if (permission.getParent() != null) {
                    permissionListDTO.setParent(wrap(permission.getParent()));
                }
                return permissionListDTO;
            })
            .collect(Collectors.toList());
    }

    private SyncPermissionListDTO wrap(Permission permission) {
        SyncPermissionListDTO permissionListDTO = new SyncPermissionListDTO();
        permissionListDTO.setId(permission.getId());
        permissionListDTO.setName(permission.getName());
        permissionListDTO.setCode(permission.getCode());
        permissionListDTO.setSection(permission.isSection());
        permissionListDTO.setPosition(permission.getPosition());
        return permissionListDTO;
    }

    @Transactional
    public void createUserPermission(UserRolePermissionPayload userRolePermissionPayload) {
        List<UserPermission> userPermissions = new ArrayList<>();
        userRepository
            .findById(userRolePermissionPayload.getUserId())
            .ifPresent(user ->
                userRolePermissionPayload
                    .getPermissions()
                    .forEach(permissionCode -> {
                        Permission permission = permissionRepository
                            .findByCodeAndDeletedFalse(permissionCode)
                            .orElseThrow(notFoundExceptionThrow(Permission.class.getSimpleName(), "code", permissionCode));
                        UserPermission userPermission = userPermissionRepository
                            .findByUserIdAndPermissionId(user.getId(), permission.getId())
                            .orElse(new UserPermission());
                        if (userPermission.getId() == null) {
                            userPermission.setPermissionId(permission.getId());
                            userPermission.setUserId(user.getId());
                            userPermission.setHasAccess(true);
                            userPermissions.add(userPermission);
                        }
                    })
            );
        if (!CollectionUtils.isEmpty(userPermissions)) {
            userPermissionRepository.saveAll(userPermissions);
        }
    }
}
