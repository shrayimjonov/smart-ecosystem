package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.menu.Menu;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.menu.MenuDTO;
import uz.uzkassa.horeca.dto.menu.MenuDetailDTO;
import uz.uzkassa.horeca.dto.menu.MenuListDTO;
import uz.uzkassa.horeca.dto.menu.MenuTreeDTO;
import uz.uzkassa.horeca.dto.menu.mobile.MenuListMobileDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.mappers.FileUploadMapper;
import uz.uzkassa.horeca.repository.MenuRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class MenuService extends BaseService {

    MenuRepository menuRepository;
    AmazonS3Service amazonS3Service;
    FileUploadMapper fileUploadMapper;

    protected void validate(MenuDTO menuDTO) {
        getCurrentBranchId(menuDTO.getBranchId());
        if (StringUtils.isEmpty(menuDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
    }

    @Transactional
    public Long create(MenuDTO menuDTO) {
        this.validate(menuDTO);
        Menu menu = new Menu();
        menu.setName(menuDTO.getName());
        menu.setBranchId(getCurrentBranchId(menuDTO.getBranchId()));
        menu.setParentId(menuDTO.getParentId());
        menu.setStatus(menuDTO.getStatus());
        menuRepository.save(menu);
        return menu.getId();
    }

    public MenuDetailDTO get(Long id) {
        return menuRepository
            .findById(id)
            .map(menu -> {
                MenuDetailDTO menuDetailDTO = new MenuDetailDTO();
                menuDetailDTO.setId(menu.getId());
                menuDetailDTO.setName(menu.getName());
                menuDetailDTO.setStatus(menu.getStatus());
                if (menu.getParentId() != null) {
                    menuDetailDTO.setParent(menu.getParent().toCommonDTO());
                }
                return menuDetailDTO;
            })
            .orElseThrow(notFoundExceptionThrow(Menu.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long update(Long id, MenuDTO menuDTO) {
        return menuRepository
            .findById(id)
            .map(menu -> {
                menuDTO.setId(id);
                this.validate(menuDTO);
                menu.setName(menuDTO.getName());
                menu.setParentId(menuDTO.getParentId());
                menu.setStatus(menuDTO.getStatus());
                menuRepository.save(menu);
                return id;
            })
            .orElseThrow(notFoundExceptionThrow(Menu.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        menuRepository
                .findById(id)
                .map(menu -> {
                    if (CommonStatus.ACTIVE.equals(status)) {
                        menuRepository.findAllByStatusAndParentIdIsNull(status)
                                .forEach(activeMenus -> {
                                    activeMenus.setStatus(CommonStatus.IN_ACTIVE);
                                    menuRepository.save(activeMenus);
                                });
                    }
                    menu.setStatus(status);
                    menuRepository.save(menu);
                    return status;
                }).orElseThrow(notFoundExceptionThrow(Menu.class.getSimpleName(), "id", id));
    }

    public Page<MenuListDTO> getListForCabinet(BaseFilter filter) {
        return menuRepository.getResultList(filter)
                .map(menu -> {
                    MenuListDTO menuListDTO = new MenuListDTO();
                    menuListDTO.setId(menu.getId());
                    menuListDTO.setName(menu.getName());
                    menuListDTO.setStatus(menu.getStatus());
                    if (menu.getParentId() != null) {
                        menuListDTO.setParent(menu.getParent().toCommonDTO());
                    }
                    if (menu.getPhoto() != null) {
                        menuListDTO.setPhoto(menu.getPhoto().toFileDTO());
                    }
                    return menuListDTO;
                });
    }

    public Page<MenuListDTO> getList(BaseFilter filter) {
        return menuRepository.getResultList(filter)
                .map(menu -> {
                    MenuListDTO menuListDTO = new MenuListDTO();
                    menuListDTO.setId(menu.getId());
                    menuListDTO.setName(menu.getName());
                    menuListDTO.setStatus(menu.getStatus());
                    if (menu.getParentId() != null) {
                        menuListDTO.setParent(menu.getParent().toCommonDTO());
                    }
                    if (menu.getPhotoId() != null) {
                        menuListDTO.setPhoto(menu.getPhoto().toFileDTO());
                    }
                    return menuListDTO;
                });
    }

    public Page<MenuListMobileDTO> getListForMobile(BaseFilter filter) {
        return menuRepository.getResultList(filter)
                .map(menu -> {
                    MenuListMobileDTO menuListDTO = new MenuListMobileDTO();
                    menuListDTO.setId(menu.getId());
                    menuListDTO.setName(menu.getName());
                    menuListDTO.setStatus(menu.getStatus());
                    if (menu.getParentId() != null) {
                        menuListDTO.setParent(menu.getParent().toCommonDTO());
                    }
                    if (menu.getPhotoId() != null) {
                        menuListDTO.setPhoto(menu.getPhoto().toFileDTO());
                    }
                    filter.setParentId(menu.getId());
                    menuListDTO.setHasChild(menuRepository.existsByParentIdAndDeletedIsFalse(menu.getId()));
                    return menuListDTO;
                });
    }

    @Transactional
    public void delete(Long id) {
        menuRepository.deleteById(id);
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        return menuRepository.getResultList(filter).stream().map(Menu::toCommonDTO).collect(Collectors.toList());
    }

    public List<MenuTreeDTO> currentActiveMenu(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return new LinkedList<>(treeList(filter));
    }

    public List<MenuTreeDTO> treeList(BaseFilter filter) {
        Page<Menu> resultList = menuRepository.getResultList(filter);
        Map<Long, MenuTreeDTO> parents = resultList.stream()
                .filter(menu -> menu.getParentId() == null)
                .collect(Collectors.toMap(Menu::getId, Menu::toTreeDTO));
        resultList.stream()
                .filter(menu -> menu.getParentId() != null)
                .forEach(menu -> {
                    if (parents.containsKey(menu.getParentId())) {
                        parents.get(menu.getParentId()).getChildren().add(menu.toTreeDTO());
                    }
                });
        return new LinkedList<>(parents.values());
    }

    @Transactional
    public FileDTO uploadPhoto(Long id, MultipartFile photo) {
        return menuRepository
            .findById(id)
            .map(menu -> {
                if (menu.getPhotoId() != null) {
                    amazonS3Service.delete(menu.getPhotoId());
                }
                FileUpload fileUpload = amazonS3Service.uploadProductPhoto(photo, menu.getId());
                menu.setPhotoId(fileUpload.getId());
                menu.setPhoto(fileUpload);
                menuRepository.save(menu);
                return fileUploadMapper.toFileDTO(fileUpload);
            })
            .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void removePhoto(Long id) {
        menuRepository
            .findById(id)
            .map(menu -> {
                FileUpload photo = menu.getPhoto();
                if (photo != null) {
                    menu.setPhoto(null);
                    menu.setPhotoId(null);
                    menuRepository.save(menu);
                    amazonS3Service.delete(photo.getId());
                }
                return menu;
            })
            .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }
}
