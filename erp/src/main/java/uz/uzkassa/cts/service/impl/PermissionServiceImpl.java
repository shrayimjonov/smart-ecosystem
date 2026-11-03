package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.permission.Permission;
import uz.uzkassa.cts.dto.permission.PermissionDTO;
import uz.uzkassa.cts.dto.permission.PermissionTreeListDTO;
import uz.uzkassa.cts.dto.permission.admin.PermissionDetailAdminDTO;
import uz.uzkassa.cts.enums.PermissionType;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.PermissionRepository;
import uz.uzkassa.cts.repository.RolePermissionRepository;
import uz.uzkassa.cts.repository.UserPermissionRepository;
import uz.uzkassa.cts.service.PermissionService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class PermissionServiceImpl extends BaseServiceImpl<Permission, Long> implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final ValidationContext validationContext;

    public PermissionServiceImpl(PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository, UserPermissionRepository userPermissionRepository, ValidationContext validationContext) {
        super(permissionRepository);
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userPermissionRepository = userPermissionRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(PermissionDTO permissionDTO) {
        validationContext.validate(permissionDTO);
        Permission permission = new Permission();
        permission.setNameUzLatin(permissionDTO.getNameUzLatin());
        permission.setNameUzCyrillic(permissionDTO.getNameUzCyrillic());
        permission.setNameRu(permissionDTO.getNameRu());
        permission.setCode(permissionDTO.getCode());
        permission.setType(permissionDTO.getType());
        permission.setPosition(permissionDTO.getPosition());
        permission.setParentId(permissionDTO.getParentId());
        permission.setSection(permissionDTO.isSection());
        return permissionRepository.save(permission).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, PermissionDTO permissionDTO) {
        permissionDTO.setId(id);
        validationContext.validate(permissionDTO);
        return permissionRepository.findById(id).map(permission -> {
            permission.setNameUzLatin(permissionDTO.getNameUzLatin());
            permission.setNameUzCyrillic(permissionDTO.getNameUzCyrillic());
            permission.setNameRu(permissionDTO.getNameRu());
            permission.setCode(permissionDTO.getCode());
            permission.setType(permissionDTO.getType());
            permission.setPosition(permissionDTO.getPosition());
            permission.setParentId(permissionDTO.getParentId());
            permission.setSection(permissionDTO.isSection());
            return permissionRepository.save(permission).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PERMISSION));
    }

    @Override
    public List<PermissionTreeListDTO> getTree(BaseFilter filter) {
        return recursion(
                permissionRepository.findByParentIdAndTypeAndDeletedIsFalseOrderByPositionAsc(
                        filter.getParentId(),
                        PermissionType.valueOf(filter.getType())
                )
        );
    }

    private List<PermissionTreeListDTO> recursion(List<Permission> permissions) {
        return permissions
                .stream()
                .map(permission -> {
                    PermissionTreeListDTO permissionTreeListDTO = new PermissionTreeListDTO();
                    permissionTreeListDTO.setId(permission.getId());
                    permissionTreeListDTO.setName(permission.getName());
                    permissionTreeListDTO.setCode(permission.getCode());
                    permissionTreeListDTO.setPosition(permission.getPosition());
                    permissionTreeListDTO.setSection(permission.isSection());
                    permissionTreeListDTO.setType(permission.getType());
                    if (permission.getParentId() != null) {
                        permissionTreeListDTO.setParent(permission.getParent().toCommonCodeDTO());
                    }
                    if (!CollectionUtils.isEmpty(permission.getChildren())) {
                        permissionTreeListDTO.setChildren(recursion(new ArrayList<>(permission.getChildren())));
                    }
                    return permissionTreeListDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public PermissionDetailAdminDTO getForAdmin(Long id) {
        return permissionRepository.findById(id).map(permission -> {
            PermissionDetailAdminDTO permissionDetailDTO = new PermissionDetailAdminDTO();
            permissionDetailDTO.setId(permission.getId());
            permissionDetailDTO.setName(permission.getName());
            permissionDetailDTO.setCode(permission.getCode());
            permissionDetailDTO.setType(permission.getType());
            permissionDetailDTO.setSection(permission.isSection());
            permissionDetailDTO.setPosition(permission.getPosition());
            if (permission.getParentId() != null) {
                permissionDetailDTO.setParent(permission.getParent().toCommonCodeDTO());
            }
            return permissionDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(PERMISSION));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        userPermissionRepository.deleteAllByPermissionId(id);
        rolePermissionRepository.deleteAllByPermissionId(id);
        permissionRepository.deleteById(id);
    }
}
