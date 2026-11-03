package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.domain.permission.Permission;
import uz.uzkassa.smartpossupply.domain.permission.RolePermission;
import uz.uzkassa.smartpossupply.domain.permission.UserPermission;
import uz.uzkassa.smartpossupply.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.smartpossupply.dto.permission.PermissionDTO;
import uz.uzkassa.smartpossupply.dto.permission.PermissionListDTO;
import uz.uzkassa.smartpossupply.dto.permission.SyncPermissionListDTO;
import uz.uzkassa.smartpossupply.dto.permission.UserRolePermissionDTO;
import uz.uzkassa.smartpossupply.enums.PermissionType;
import uz.uzkassa.smartpossupply.enums.Role;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.mappers.PermissionMapper;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqPermissionProducer;
import uz.uzkassa.smartpossupply.repository.PermissionRepository;
import uz.uzkassa.smartpossupply.repository.RolePermissionRepository;
import uz.uzkassa.smartpossupply.repository.UserPermissionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;


@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PermissionService extends BaseService {

    PermissionRepository permissionRepository;
    UserPermissionRepository userPermissionRepository;
    RolePermissionRepository rolePermissionRepository;
    RabbitMqPermissionProducer rabbitMqPermissionProducer;
    PermissionMapper permissionMapper;

    @Transactional
    public Long create(PermissionDTO permissionDTO) {
        if (permissionRepository.findByCodeAndPermissionTypeAndDeletedFalse(permissionDTO.getCode(), permissionDTO.getPermissionType()).isPresent()) {
            throw validationService.conflictExceptionThrow("Permission has already been existed with this code: " + permissionDTO.getCode()).get();
        }
        Permission permission = permissionMapper.toEntity(permissionDTO);
        permissionRepository.save(permission);
        return permission.getId();
    }

    @Transactional
    public Long update(Long id, PermissionDTO permissionDTO) {
        if (permissionRepository.findByCodeAndPermissionTypeAndIdNotAndDeletedIsFalse(permissionDTO.getCode(), permissionDTO.getPermissionType(), id).isPresent()) {
            throw validationService.conflictExceptionThrow("Permission has already been existed with this code: " + permissionDTO.getCode()).get();
        }
        return permissionRepository
            .findById(id)
            .map(permission -> {
                permission.setName(permissionDTO.getName());
                permission.setCode(permissionDTO.getCode());
                permission.setPosition(permissionDTO.getPosition());
                permission.setParentId(permissionDTO.getParentId());
                permission.setPermissionType(permissionDTO.getPermissionType());
                permission.setSection(permissionDTO.isSection());
                permissionRepository.save(permission);
                return permission.getId();
            })
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Permission.class.getSimpleName(), "id", id));
    }

    public PermissionDTO get(Long id) {
        return permissionRepository
            .findById(id)
            .map(permissionMapper::toDTO)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Permission.class.getSimpleName(), "id", id));
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
                permissionListDTO.setCode(permission.getCode());
                permissionListDTO.setPosition(permission.getPosition());
                permissionListDTO.setSection(permission.isSection());
                permissionListDTO.setParentId(permission.getParentId());
                permissionListDTO.setPermissionType(permission.getPermissionType());

                if (!CollectionUtils.isEmpty(permission.getChildren())) {
                    permissionListDTO.setChildren(recursion(permission.getChildren()));
                }
                return permissionListDTO;
            })
            .toList();
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Permission permission = permissionRepository
            .findById(id)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(Permission.class.getSimpleName(), "id", id));
        userPermissionRepository.deleteAllByPermissionId(permission.getId());
        userPermissionRepository.flush();

        rolePermissionRepository.deleteAllByPermissionId(permission.getId());
        permissionRepository.delete(permission);
    }

    @Transactional
    public void saveUserPermissions(Long userId, UserRolePermissionDTO userRolePermissionDTO) {
        List<Permission> permissions = permissionRepository.findAllByPermissionTypeAndCodeIn(userRolePermissionDTO.getPermissionType(), userRolePermissionDTO.getPermissions());
        List<UserPermission> userPermissions = new ArrayList<>();
        permissions.forEach(permission -> {
            UserPermission userPermission = userPermissionRepository
                .findFirstByUserIdAndPermissionId(userId, permission.getId())
                .orElse(new UserPermission());
            if (userPermission.getId() == null) {
                userPermission.setPermissionId(permission.getId());
                userPermission.setUserId(userId);
            }
            userPermission.setHasAccess(true);
            userPermission.setManual(true);
            userPermissions.add(userPermission);
        });

        userPermissionRepository.saveAll(userPermissions);
        List<Long> ids = permissions.stream().map(BaseEntity::getId).toList();
        if (CollectionUtils.isEmpty(ids)) {
            userPermissionRepository.updateAllAccess(userId);
        }
        userPermissionRepository.updateAccess(userId, ids);
    }

    public List<String> getUserPermissions(Long userId) {
        return userPermissionRepository.findAllPermissionCodesByUserId(userId);
    }

    @Transactional
    public void deleteUserPermissionsByUserId(Long userId) {
        userPermissionRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public void saveRolePermissions(Role role, UserRolePermissionDTO userRolePermissionDTO) {
        List<Permission> permissions = permissionRepository.findAllByPermissionTypeAndCodeIn(userRolePermissionDTO.getPermissionType(), userRolePermissionDTO.getPermissions());
        rolePermissionRepository.deleteAllByRoleAndPermission_PermissionType(role, userRolePermissionDTO.getPermissionType());
        rolePermissionRepository.flush();
        List<RolePermission> rolePermissions = new ArrayList<>();
        permissions.forEach(permission -> {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRole(role);
            rolePermission.setPermissionId(permission.getId());
            rolePermissions.add(rolePermission);
        });
        rolePermissionRepository.saveAll(rolePermissions);
        List<Long> ids = permissions.stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<Long> userIds = userRepository.getIdsByRole(role);
        userIds.forEach(userId -> rabbitMqPermissionProducer.save(new UserRolePermissionPayload(userId, ids)));
    }

    public List<String> getRolePermissions(Role role) {
        return rolePermissionRepository.findAllPermissionCodesByRole(role);
    }

    @Transactional
    public void createUserPermission(UserRolePermissionPayload userRolePermissionPayload) {
        if (!CollectionUtils.isEmpty(userRolePermissionPayload.getPermissionIds())) {
            List<UserPermission> userPermissions = new ArrayList<>();
            userRolePermissionPayload
                .getPermissionIds()
                .forEach(permissionId -> {
                    UserPermission userPermission = userPermissionRepository
                        .findFirstByUserIdAndPermissionId(userRolePermissionPayload.getUserId(), permissionId)
                        .orElse(new UserPermission());
                    if (userPermission.getId() == null) {
                        userPermission.setPermissionId(permissionId);
                        userPermission.setUserId(userRolePermissionPayload.getUserId());
                        userPermission.setManual(false);
                    }
                    userPermission.setHasAccess(true);
                    userPermissions.add(userPermission);
                });

            userPermissionRepository.saveAllAndFlush(userPermissions);
        } else {
            userPermissionRepository.updateAllAccess(userRolePermissionPayload.getUserId());
        }
        userPermissionRepository.updateAccessByRole(userRolePermissionPayload.getUserId(), userRolePermissionPayload.getPermissionIds());
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
}
