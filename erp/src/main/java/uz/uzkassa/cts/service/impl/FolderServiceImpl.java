package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Folder;
import uz.uzkassa.cts.dto.document.FolderDTO;
import uz.uzkassa.cts.dto.document.FolderTreeDTO;
import uz.uzkassa.cts.dto.permission.PermissionDTO;
import uz.uzkassa.cts.enums.PermissionType;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.repository.FolderRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.FolderService;
import uz.uzkassa.cts.service.PermissionService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class FolderServiceImpl extends BaseServiceImpl<Folder, Long> implements FolderService {

    private final FolderRepository folderRepository;
    private final PermissionService permissionService;
    private final ValidationContext validationContext;

    public FolderServiceImpl(FolderRepository folderRepository, PermissionService permissionService, ValidationContext validationContext) {
        super(folderRepository);
        this.folderRepository = folderRepository;
        this.permissionService = permissionService;
        this.validationContext = validationContext;
    }

    @Override
    public List<FolderTreeDTO> getTree() {
        if (SecurityUtils.isCurrentUserInRole(Role.SUPER_ADMIN.getRoleCode())) {
            return fillTree(folderRepository.getParents());
        }
        return fillTree(folderRepository.getParents(getCurrentUserId()));
    }

    private List<FolderTreeDTO> fillTree(Set<Folder> folders) {
        return folders
                .stream()
                .map(folder -> {
                    FolderTreeDTO folderTreeDTO = new FolderTreeDTO();
                    folderTreeDTO.setId(folder.getId());
                    folderTreeDTO.setName(folder.getName());
                    if (folder.getParentId() != null) {
                        folderTreeDTO.setParent(folder.getParent().toCommonDTO());
                    }
                    if (SecurityUtils.isCurrentUserInRole(Role.SUPER_ADMIN.getRoleCode())) {
                        folderTreeDTO.setChildren(fillTree(folder.getChildren()));
                    } else {
                        folderTreeDTO.setChildren(fillTree(folderRepository.findChildren(folder.getId(), getCurrentUserId())));
                    }
                    return folderTreeDTO;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long create(FolderDTO folderDTO) {
        validationContext.validate(folderDTO);
        Folder folder = new Folder();
        folder.setName(folderDTO.getName());
        folder.setParentId(folderDTO.getParentId());

        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.setNameUzLatin(folder.getName());
        permissionDTO.setNameUzCyrillic(folder.getName());
        permissionDTO.setNameRu(folder.getName());
        permissionDTO.setCode(folderDTO.getPermissionCode());
        permissionDTO.setType(PermissionType.FOLDER);
        folder.setPermissionId(permissionService.create(permissionDTO));
        return folderRepository.save(folder).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, FolderDTO folderDTO) {
        folderDTO.setId(id);
        validationContext.validate(folderDTO);
        return folderRepository.findById(folderDTO.getId()).map(folder -> {
            folder.setName(folderDTO.getName());
            folder.setParentId(folderDTO.getParentId());
            return folderRepository.save(folder).getId();

        }).orElseThrow(validationService.entityNotFoundExceptionThrow(FOLDER));
    }
}
