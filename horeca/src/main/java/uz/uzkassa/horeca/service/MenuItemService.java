package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.constants.Constants;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.domain.product.BatchStatusUpdate;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.dto.menu.*;
import uz.uzkassa.horeca.dto.menu.mobile.MenuItemDetailMobileDTO;
import uz.uzkassa.horeca.dto.menu.mobile.MenuItemListMobileDTO;
import uz.uzkassa.horeca.dto.soliq.ProductSearchDTO;
import uz.uzkassa.horeca.dto.stock.CurrentStockDTO;
import uz.uzkassa.horeca.dto.unit.TerminalMultiUnitDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.enums.EntityTypeEnum;
import uz.uzkassa.horeca.enums.PhotoType;
import uz.uzkassa.horeca.filters.MenuItemFilter;
import uz.uzkassa.horeca.mappers.FileUploadMapper;
import uz.uzkassa.horeca.repository.*;
import uz.uzkassa.horeca.utils.MarkUtils;
import uz.uzkassa.horeca.utils.SecurityUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@RequiredArgsConstructor
@Slf4j
public class MenuItemService extends BaseService {

    MenuItemRepository menuItemRepository;
    AmazonS3Service amazonS3Service;
    FileUploadMapper fileUploadMapper;
    MultiUnitService multiUnitService;
    MultiUnitRepository multiUnitRepository;
    FileUploadRepository fileUploadRepository;
    CurrentStockService currentStockService;
    private final ProductRepository productRepository;
    private final ReceiptRepository receiptRepository;
    private final ReceiptItemRepository receiptItemRepository;

    protected void validate(MenuItemDTO menuItemDTO) {
        if (StringUtils.isEmpty(menuItemDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (menuItemDTO.getMenuId() == null) {
            throw badRequestExceptionThrow("Menu is required").get();
        }
        if (menuItemDTO.getProductId() == null) {
            throw badRequestExceptionThrow("Product is required").get();
        }
        if (CollectionUtils.isEmpty(menuItemDTO.getUnits())) {
            throw badRequestExceptionThrow("Multi units are required").get();
        }
        if (menuItemDTO.getId() == null && menuItemRepository.existsByMenuIdAndProductIdAndDeletedIsFalse(menuItemDTO.getMenuId(), menuItemDTO.getProductId())) {
            throw conflictExceptionThrow("Menu item with this product already exists").get();
        }
        if (menuItemDTO.getId() != null && menuItemRepository.existsByMenuIdAndProductIdAndDeletedIsFalseAndIdNot(menuItemDTO.getMenuId(), menuItemDTO.getProductId(), menuItemDTO.getId())) {
            throw conflictExceptionThrow("Menu item with this product already exists").get();
        }
    }

    @Transactional
    public Long create(MenuItemDTO menuItemDTO) {
        this.validate(menuItemDTO);
        MenuItem menuItem = new MenuItem();
        menuItem.setName(menuItemDTO.getName());
        menuItem.setProductId(menuItemDTO.getProductId());
        menuItem.setMenuId(menuItemDTO.getMenuId());
        menuItemRepository.save(menuItem);
        if (menuItemDTO.getCategoryType() != null) {
            this.uploadExternalPhoto(menuItem.getId(), new ExternalPhotoUploadDTO(getDefaultPhotoUrlByCategoryType(menuItemDTO.getCategoryType())));
        }
        multiUnitService.batchCreate(menuItemDTO.getUnits(), menuItem.getId());
        currentStockService.create(new CurrentStockDTO(menuItem.getId()));
        productRepository.findById(menuItemDTO.getProductId()).ifPresent(product -> {
            product.setMenuIncluded(true);
            productRepository.save(product);
        });
        return menuItem.getId();
    }

    @Transactional
    public Long update(Long id, MenuItemDTO menuItemDTO) {
        return menuItemRepository.findById(id).map(menuItem -> {
            menuItemDTO.setId(id);
            this.validate(menuItemDTO);
            menuItem.setName(menuItemDTO.getName());
            menuItem.setMenuId(menuItemDTO.getMenuId());
            menuItemRepository.save(menuItem);
            if (menuItemDTO.getCategoryType() != null && menuItem.getPhotoId() == null) {
                this.uploadExternalPhoto(menuItem.getId(), new ExternalPhotoUploadDTO(getDefaultPhotoUrlByCategoryType(menuItemDTO.getCategoryType())));
            }
            multiUnitService.batchUpdate(menuItemDTO.getUnits(), menuItem.getId());
            currentStockService.create(new CurrentStockDTO(menuItem.getId()));
            return id;
        }).orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }

    public MenuItemDetailDTO get(Long id) {
        return menuItemRepository
                .findById(id)
                .map(menuItem -> {
                    MenuItemDetailDTO menuItemDetailDTO = new MenuItemDetailDTO();
                    menuItemDetailDTO.setId(menuItem.getId());
                    menuItemDetailDTO.setName(menuItem.getName());
                    menuItemDetailDTO.setProduct(menuItem.getProduct().toCommonDTO());
                    if (menuItem.getPhotoId() != null) {
                        menuItemDetailDTO.setPhoto(menuItem.getPhoto().toFileDTO());
                        if (!CollectionUtils.isEmpty(menuItem.getPhoto().getChildren())) {
                            menuItemDetailDTO.setPhotos(menuItem.getPhoto().getChildren().stream().map(FileUpload::toFileDTO).collect(Collectors.toList()));
                        }
                    }
                    menuItemDetailDTO.setMenu(menuItem.getMenu().toCommonDTO());
                    menuItemDetailDTO.setUnits(multiUnitService.getList(id));
                    menuItemDetailDTO.setStatus(menuItem.getStatus());
                    return menuItemDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }

    public Page<MenuItemListDTO> getList(MenuItemFilter filter) {
        List<MenuItemListDTO> result = menuItemRepository
                .getResultList(filter)
                .getList()
                .stream()
                .map(menuItem -> {
                    MenuItemListDTO menuItemListDTO = new MenuItemListDTO();
                    menuItemListDTO.setId(menuItem.getId());
                    menuItemListDTO.setName(menuItem.getName());
                    menuItemListDTO.setStatus(menuItem.getStatus());
                    menuItemListDTO.setProduct(toProductCommonDTO(menuItem.getProduct()));
                    if (menuItem.getPhotoId() != null) {
                        menuItemListDTO.setPhoto(menuItem.getPhoto().toFileDTO());
                        if (!CollectionUtils.isEmpty(menuItem.getPhoto().getChildren())) {
                            menuItemListDTO.setPhotos(menuItem.getPhoto().getChildren().stream().map(FileUpload::toFileDTO).collect(Collectors.toList()));
                        }
                    }
                    menuItemListDTO.setFavourite(menuItem.isFavourite());
                    menuItemListDTO.setUnits(multiUnitService.getList(menuItem.getId()));
                    menuItemListDTO.setMenu(menuItem.getMenu().toCommonDTO());
                    if (menuItem.getMenu().getParent() != null) {
                        menuItemListDTO.setParentMenu(menuItem.getMenu().getParent().toCommonDTO());
                    }
                    return menuItemListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), result.size());
    }

    public Page<MenuItemListMobileDTO> getListForMobile(MenuItemFilter filter) {
        filter.setSize(1000);
        filter.setPage(0);
        List<MenuItemListMobileDTO> result = menuItemRepository
                .getResultList(filter)
                .getList()
                .stream()
                .map(menuItem -> {
                    MenuItemListMobileDTO menuItemListDTO = new MenuItemListMobileDTO();
                    menuItemListDTO.setId(menuItem.getId());
                    menuItemListDTO.setName(menuItem.getName());
                    menuItemListDTO.setProduct(toProductCommonDTO(menuItem.getProduct()));
                    if (menuItem.getPhotoId() != null) {
                        menuItemListDTO.setPhoto(menuItem.getPhoto().toFileDTO());
                        if (!CollectionUtils.isEmpty(menuItem.getPhoto().getChildren())) {
                            menuItemListDTO.setPhotos(menuItem.getPhoto().getChildren().stream().map(FileUpload::toFileDTO).collect(Collectors.toList()));
                        }
                    }
                    menuItemListDTO.setUnits(multiUnitService.getList(menuItem.getId()));
                    menuItemListDTO.setStatus(menuItem.getStatus());
                    return menuItemListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), result.size());
    }

    public MenuItemDetailMobileDTO getForMobile(Long id) {
        return menuItemRepository
                .findById(id)
                .map(menuItem -> {
                    MenuItemDetailMobileDTO menuItemDetailDTO = new MenuItemDetailMobileDTO();
                    menuItemDetailDTO.setId(menuItem.getId());
                    menuItemDetailDTO.setName(menuItem.getName());
                    menuItemDetailDTO.setProduct(menuItem.getProduct().toCommonDTO());
                    if (menuItem.getPhotoId() != null) {
                        menuItemDetailDTO.setPhoto(menuItem.getPhoto().toFileDTO());
                        if (!CollectionUtils.isEmpty(menuItem.getPhoto().getChildren())) {
                            menuItemDetailDTO.setPhotos(menuItem.getPhoto().getChildren().stream().map(FileUpload::toFileDTO).collect(Collectors.toList()));
                        }
                    }
                    menuItemDetailDTO.setMenu(menuItem.getMenu().toCommonDTO());
                    menuItemDetailDTO.setStatus(menuItem.getStatus());
                    return menuItemDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void favorite(Long id) {
        menuItemRepository
                .findById(id)
                .map(menuItem -> {
                    menuItem.setFavourite(!menuItem.isFavourite());
                    menuItemRepository.save(menuItem);
                    return menuItem;
                })
                .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void delete(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
        if (receiptItemRepository.existsByProductIdAndDeletedIsFalse(menuItem.getProductId())) {
            throw badRequestExceptionThrow(localizationService.localize("menu.item.cant.delete")).get();
        }
        productRepository.findById(menuItem.getProductId()).ifPresent(product -> {
            product.setMenuIncluded(false);
            productRepository.save(product);
        });
        multiUnitService.deleteByMenuItemId(id);
        menuItemRepository.deleteById(id);
    }

    @Transactional
    public FileDTO uploadPhoto(Long id, MultipartFile photo) {
        return menuItemRepository
                .findById(id)
                .map(menuItem -> {
                    if (menuItem.getPhotoId() != null) {
                        amazonS3Service.delete(menuItem.getPhotoId());
                    }
                    FileUpload fileUpload = amazonS3Service.uploadProductPhoto(photo, menuItem.getId());
                    fileUpload.setChildren(this.uploadChildren(photo, id));
                    menuItem.setPhotoId(fileUpload.getId());
                    menuItem.setPhoto(fileUpload);
                    menuItemRepository.save(menuItem);
                    return fileUploadMapper.toFileDTO(fileUpload);
                })
                .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public FileDTO uploadExternalPhoto(Long id, ExternalPhotoUploadDTO photo) {
        return menuItemRepository
                .findById(id)
                .map(menuItem -> {
                    if (menuItem.getPhotoId() != null) {
                        amazonS3Service.delete(menuItem.getPhotoId());
                    }
                    FileUpload fileUpload = new FileUpload();
                    fileUpload.setEntityId(menuItem.getId());
                    fileUpload.setUrl(photo.url());
                    fileUpload.setFileName(UUID.randomUUID() + ".jpg");
                    fileUpload.setKey(SecurityUtils.md5(System.currentTimeMillis() + UUID.randomUUID().toString()));
                    fileUpload.setEntityType(EntityTypeEnum.MENU_ITEM);
                    fileUploadRepository.save(fileUpload);
                    menuItem.setPhoto(fileUpload);
                    menuItem.setPhotoId(fileUpload.getId());
                    menuItemRepository.save(menuItem);
                    return fileUploadMapper.toFileDTO(fileUpload);
                })
                .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void removePhoto(Long id) {
        menuItemRepository
                .findById(id)
                .map(menuItem -> {
                    FileUpload photo = menuItem.getPhoto();
                    if (photo != null) {
                        menuItem.setPhoto(null);
                        menuItem.setPhotoId(null);
                        menuItemRepository.save(menuItem);
                        amazonS3Service.delete(photo.getId());
                        if (!CollectionUtils.isEmpty(photo.getChildren())) {
                            amazonS3Service.delete(photo.getChildren().stream().map(FileUpload::getId).collect(Collectors.toList()));
                        }
                    }
                    return menuItem;
                })
                .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }

    public TerminalMenuItemDetailDTO getForTerminal(Long multiUnitId) {
        return multiUnitRepository.findById(multiUnitId)
                .map(multiUnit -> {
                    TerminalMenuItemDetailDTO terminalMenuItemDetailDTO = new TerminalMenuItemDetailDTO();
                    MenuItem menuItem = multiUnit.getMenuItem();
                    terminalMenuItemDetailDTO.setId(menuItem.getId());
                    terminalMenuItemDetailDTO.setName(menuItem.getName());
                    terminalMenuItemDetailDTO.setProduct(menuItem.getProduct().toCommonDTO());
                    if (menuItem.getPhotoId() != null) {
                        terminalMenuItemDetailDTO.setPhoto(menuItem.getPhoto().toFileDTO());
                    }
                    terminalMenuItemDetailDTO.setMenu(menuItem.getMenu().toCommonDTO());
                    TerminalMultiUnitDTO terminalMultiUnitDTO = new TerminalMultiUnitDTO();
                    terminalMultiUnitDTO.setId(multiUnit.getId());
                    terminalMultiUnitDTO.setUnit(multiUnit.getTechCard().getUnit().toCommonDTO());
                    terminalMultiUnitDTO.setWeight(multiUnit.getWeight());
                    terminalMultiUnitDTO.setPrice(multiUnit.getPrice());
                    terminalMultiUnitDTO.setTechCard(multiUnit.getTechCard().toCommonDTO());
                    terminalMenuItemDetailDTO.setUnit(terminalMultiUnitDTO);
                    return terminalMenuItemDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "multiUnitId", multiUnitId));
    }

    public Page<TerminalMenuItemListDTO> getListForTerminal(MenuItemFilter filter) {
        List<TerminalMenuItemListDTO> result = menuItemRepository
                .getResultListForTerminal(filter)
                .getList()
                .stream()
                .map(multiUnit -> {
                    TerminalMenuItemListDTO menuItemListDTO = new TerminalMenuItemListDTO();
                    menuItemListDTO.setId(multiUnit.getMenuItem().getId());
                    menuItemListDTO.setName(multiUnit.getMenuItem().getName());
                    menuItemListDTO.setProduct(toProductCommonDTO(multiUnit.getMenuItem().getProduct()));
                    if (multiUnit.getMenuItem().getPhotoId() != null) {
                        menuItemListDTO.setPhoto(multiUnit.getMenuItem().getPhoto().toFileDTO());
                    }
                    menuItemListDTO.setFavourite(multiUnit.getMenuItem().isFavourite());
                    TerminalMultiUnitDTO terminalMultiUnitDTO = new TerminalMultiUnitDTO();
                    terminalMultiUnitDTO.setId(multiUnit.getId());
                    terminalMultiUnitDTO.setUnit(multiUnit.getTechCard().getUnit().toCommonDTO());
                    terminalMultiUnitDTO.setWeight(multiUnit.getWeight());
                    terminalMultiUnitDTO.setPrice(multiUnit.getPrice());
                    terminalMultiUnitDTO.setTechCard(multiUnit.getTechCard().toCommonDTO());
                    menuItemListDTO.setUnit(terminalMultiUnitDTO);
                    return menuItemListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), result.size());
    }

    public List<MenuItemListDTO> searchByParams(ProductSearchDTO searchDTO) {
        searchDTO.setBranchId(Optional.ofNullable(searchDTO.getBranchId()).orElse(getCurrentBranchId()));
        if (searchDTO.getBranchId() == null) {
            throw badRequestExceptionThrow("Branch id is required").get();
        }
        String barcode = null;
        if (StringUtils.isNotEmpty(searchDTO.getGtin())) {
            barcode = searchDTO.getGtin();
        } else if (StringUtils.isNotEmpty(searchDTO.getMarkCode())) {
            if (MarkUtils.isNotMark(searchDTO.getMarkCode())) {
                throw badRequestExceptionThrow("Incorrect mark code").get();
            }
            barcode = MarkUtils.getBarcodeFromMarkCode(searchDTO.getMarkCode());
        }
        if (StringUtils.isNotEmpty(barcode)) {
            List<MenuItem> menuItems = menuItemRepository.findAllByProductBarcodeAndProductBranchIdAndDeletedIsFalse(barcode, searchDTO.getBranchId());
            if (menuItems == null) {
                throw notFoundExceptionThrow(Product.class.getSimpleName(), "barcode", barcode).get();
            }
            return menuItems.stream().map(menuItem -> {
                MenuItemListDTO menuItemListDTO = new MenuItemListDTO();
                menuItemListDTO.setId(menuItem.getId());
                menuItemListDTO.setName(menuItem.getName());
                menuItemListDTO.setUnits(multiUnitService.getList(menuItem.getId()));
                if (menuItem.getPhotoId() != null) {
                    menuItemListDTO.setPhoto(menuItem.getPhoto().toFileDTO());
                    if (!CollectionUtils.isEmpty(menuItem.getPhoto().getChildren())) {
                        menuItemListDTO.setPhotos(menuItem.getPhoto().getChildren().stream().map(FileUpload::toFileDTO).collect(Collectors.toList()));
                    }
                }
                menuItemListDTO.setProduct(toProductCommonDTO(menuItem.getProduct()));
                menuItemListDTO.setFavourite(menuItem.isFavourite());
                menuItemListDTO.setMenu(menuItem.getMenu().toCommonDTO());
                menuItemListDTO.setParentMenu(menuItem.getMenu().toCommonDTO());
                return menuItemListDTO;
            }).collect(Collectors.toList());
        }
        throw notFoundExceptionThrow(Product.class.getSimpleName(), "barcode", barcode).get();
    }

    private List<FileUpload> uploadChildren(MultipartFile photo, Long menuItemId) {
        List<FileUpload> photoList = new ArrayList<>();
        MultipartFile lowPhoto = resizePhoto(photo, PhotoType.LOW);
        if (lowPhoto != null) {
            photoList.add(amazonS3Service.uploadProductPhoto(lowPhoto, menuItemId, PhotoType.LOW));
        }
        MultipartFile mediumPhoto = resizePhoto(photo, PhotoType.MEDIUM);
        if (mediumPhoto != null) {
            photoList.add(amazonS3Service.uploadProductPhoto(mediumPhoto, menuItemId, PhotoType.MEDIUM));
        }
        MultipartFile highPhoto = resizePhoto(photo, PhotoType.HIGH);
        if (highPhoto != null) {
            photoList.add(amazonS3Service.uploadProductPhoto(highPhoto, menuItemId, PhotoType.HIGH));
        }
        return photoList;
    }

    public MultipartFile resizePhoto(MultipartFile photo, PhotoType photoType) {
        long now = System.currentTimeMillis();
        BufferedImage image = null;
        try {
            image = ImageIO.read(photo.getInputStream());
        } catch (IOException e) {
            log.error("Reading object error: {}", photo);
        }
        if (image != null) {
            int width;
            int height;
            float n;
            if (PhotoType.LOW.equals(photoType)) {
                n = 100 - ((float) image.getHeight() - LOW_PHOTO_HEIGHT) / image.getHeight() * 100;
                width = Math.round((image.getWidth() * n / 100));
                height = LOW_PHOTO_HEIGHT;
            } else if (PhotoType.MEDIUM.equals(photoType)) {
                n = 100 - ((float) image.getWidth() - Constants.MEDIUM_PHOTO_WIDTH) / image.getWidth() * 100;
                height = Math.round((image.getHeight() * n / 100));
                width = MEDIUM_PHOTO_WIDTH;
            } else {
                n = 100 - ((float) image.getWidth() - Constants.HIGH_PHOTO_WIDTH) / image.getWidth() * 100;
                height = Math.round((image.getHeight() * n / 100));
                width = HIGH_PHOTO_WIDTH;
            }
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.SCALE_SMOOTH);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, width, height, null);
            graphics2D.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(resizedImage, "jpg", outputStream);
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("----------------------Time to resize photo: {} ms ------------------------------", System.currentTimeMillis() - now);
            log.info("---------------------PhotoType: {} ----------width: {} --------height: {} ------------------", photoType, width, height);
            return new CustomMultipartFile(outputStream.toByteArray(), photo.getOriginalFilename(), photo.getContentType());
        }
        return null;
    }

    public String getNameFromUrl(String url) {
//        String name;
//        if (url.contains("/") && url.contains(".")) {
//            name = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
//        } else {
//            name = RandomStringUtils.random(10);
//        }
        return url;
    }

    public List<MenuItem> getListForCronJob() {
        return menuItemRepository.findAllByDeletedIsFalseAndProductDeletedIsFalse();
    }

    public Map<Long, String> updateStatusBatch(BatchStatusUpdate batchStatusUpdate) {
        List<MenuItem> allById = menuItemRepository.findAllById(batchStatusUpdate.getIds());
        if (allById.isEmpty()) notFoundExceptionThrow(Product.class.getSimpleName()).get();
        final CommonStatus status = batchStatusUpdate.getStatus();
        Map<Long, String> errors = new HashMap<>();
        for (MenuItem menuItem : allById) {
            if (status == menuItem.getStatus()) continue;

            if (status == CommonStatus.ACTIVE) {
                if (menuItem.getProduct().getStatus() != CommonStatus.ACTIVE) {
                    errors.put(menuItem.getId(), "Product is not active");
                }
            }
            if (!errors.containsKey(menuItem.getId())) {
                menuItem.setStatus(status);
                menuItemRepository.save(menuItem);
            }
        }
        return errors;
    }
}
