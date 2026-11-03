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
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.horeca.domain.Compensation;
import uz.uzkassa.horeca.domain.FileUpload;
import uz.uzkassa.horeca.domain.menu.MenuItem;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.category.CategoryListDTO;
import uz.uzkassa.horeca.dto.compensation.CompensationDTO;
import uz.uzkassa.horeca.dto.compensation.CompensationDetailCabinetDTO;
import uz.uzkassa.horeca.dto.compensation.CompensationListCabinetDTO;
import uz.uzkassa.horeca.dto.file.FileDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.mappers.FileUploadMapper;
import uz.uzkassa.horeca.repository.CompensationRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CompensationService extends BaseService {

    CompensationRepository compensationRepository;
    FileUploadMapper fileUploadMapper;
    AmazonS3Service amazonS3Service;

    private void validate(CompensationDTO compensationDTO) {
//        if (StringUtils.isEmpty(compensationDTO.getDescription())) {
//            throw badRequestExceptionThrow("Description is required").get();
//        }
        if (compensationDTO.getProductId() == null) {
            throw badRequestExceptionThrow("Product is required").get();
        }
        if (compensationDTO.getPrice() == null && compensationDTO.getPercent() == null) {
            throw badRequestExceptionThrow("Price or percent is required").get();
        }
        if (compensationDTO.getBranchId() == null && getCurrentBranchId() == null) {
            throw badRequestExceptionThrow("Branch is required").get();
        }
    }

    @Transactional
    public Long create(CompensationDTO compensationDTO) {
        this.validate(compensationDTO);
        Compensation compensation = new Compensation();
        compensation.setDescription(compensationDTO.getDescription());
        compensation.setProductId(compensationDTO.getProductId());
        compensation.setPrice(compensationDTO.getPrice());
        compensation.setPercent(compensationDTO.getPercent());
        compensation.setCategoryId(compensationDTO.getCategoryId());
        compensation.setBranchId(getCurrentBranchId(compensationDTO.getBranchId(), true));
        compensationRepository.save(compensation);
        return compensation.getId();
    }

    @Transactional
    public Long update(Long id, CompensationDTO compensationDTO) {
        compensationDTO.setId(id);
        this.validate(compensationDTO);
        return compensationRepository
                .findById(id)
                .map(penalty -> {
                    penalty.setDescription(compensationDTO.getDescription());
                    penalty.setProductId(compensationDTO.getProductId());
                    penalty.setPrice(compensationDTO.getPrice());
                    penalty.setPercent(compensationDTO.getPercent());
                    compensationRepository.save(penalty);
                    return penalty.getId();
                })
                .orElseThrow(notFoundExceptionThrow(Compensation.class.getSimpleName(), "id", id));
    }

    public Page<CompensationListCabinetDTO> getListForCabinet(BaseFilter filter) {
        ResultList<Compensation> resultList = compensationRepository.getResultList(filter);
        List<CompensationListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(penalty -> {
                    CompensationListCabinetDTO penaltyListDTO = new CompensationListCabinetDTO();
                    penaltyListDTO.setId(penalty.getId());
                    penaltyListDTO.setDescription(penalty.getDescription());
                    penaltyListDTO.setProduct(toProductPriceDTO(penalty.getProduct()));
                    penaltyListDTO.setPrice(penalty.getPrice());
                    penaltyListDTO.setPercent(penalty.getPercent());
                    CategoryListDTO categoryListDTO = new CategoryListDTO();
                    categoryListDTO.setId(penalty.getCategory().getId());
                    categoryListDTO.setName(penalty.getCategory().getName());
                    if (StringUtils.isNotEmpty(penalty.getCategory().getCatalogCode())) {
                        categoryListDTO.setCatalog(penalty.getCategory().toCatalogDTO());
                    }
                    categoryListDTO.setPackageCode(penalty.getCategory().getPackageCode());
                    categoryListDTO.setPackageName(penalty.getCategory().getPackageName());
                    categoryListDTO.setStatus(penalty.getCategory().getStatus());
                    categoryListDTO.setType(penalty.getCategory().getCategoryType());
                    categoryListDTO.setBranch(penalty.getCategory().getBranch().toCommonDTO());
                    if (penalty.getCategory().getParent() != null) {
                        categoryListDTO.setParent(penalty.getCategory().getParent().toCommonDTO());
                    }
                    penaltyListDTO.setCategory(categoryListDTO);
                    if (penalty.getPhotoId() != null) {
                        penaltyListDTO.setPhoto(penalty.getPhoto().toFileDTO());
                    }
                    return penaltyListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public CompensationDetailCabinetDTO getForCabinet(Long id) {
        return compensationRepository
                .findById(id)
                .map(penalty -> {
                    CompensationDetailCabinetDTO penaltyDetailDTO = new CompensationDetailCabinetDTO();
                    penaltyDetailDTO.setId(penalty.getId());
                    penaltyDetailDTO.setDescription(penalty.getDescription());
                    penaltyDetailDTO.setProduct(toProductPriceDTO(penalty.getProduct()));
                    penaltyDetailDTO.setPrice(penalty.getPrice());
                    penaltyDetailDTO.setPercent(penalty.getPercent());
                    penaltyDetailDTO.setBranch(penalty.getBranch().toCommonDTO());
                    penaltyDetailDTO.setCategory(penalty.getCategory().toCommonDTO());
                    if (penalty.getPhotoId() != null) {
                        penaltyDetailDTO.setPhoto(penalty.getPhoto().toFileDTO());
                    }
                    return penaltyDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Compensation.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void delete(Long id) {
        compensationRepository.deleteById(id);
    }

    @Transactional
    public void createBatch(List<CompensationDTO> compensationDTOList) {
        compensationDTOList.forEach(this::create);
    }

    @Transactional
    public void updateBatch(List<CompensationDTO> compensationDTOList) {
        compensationDTOList.forEach(compensationDTO -> this.update(compensationDTO.getId(), compensationDTO));
    }

    @Transactional
    public FileDTO uploadPhoto(Long id, MultipartFile photo) {
        return compensationRepository
                .findById(id)
                .map(compensation -> {
                    if (compensation.getPhotoId() != null) {
                        amazonS3Service.delete(compensation.getPhotoId());
                    }
                    FileUpload fileUpload = amazonS3Service.uploadProductPhoto(photo, compensation.getId());
                    compensation.setPhotoId(fileUpload.getId());
                    compensation.setPhoto(fileUpload);
                    compensationRepository.save(compensation);
                    return fileUploadMapper.toFileDTO(fileUpload);
                })
                .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void removePhoto(Long id) {
        compensationRepository
                .findById(id)
                .map(menuItem -> {
                    FileUpload photo = menuItem.getPhoto();
                    if (photo != null) {
                        menuItem.setPhoto(null);
                        menuItem.setPhotoId(null);
                        compensationRepository.save(menuItem);
                        amazonS3Service.delete(photo.getId());
                    }
                    return menuItem;
                })
                .orElseThrow(notFoundExceptionThrow(MenuItem.class.getSimpleName(), "id", id));
    }
}
