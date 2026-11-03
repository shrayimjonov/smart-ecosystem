package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Accessory;
import uz.uzkassa.cts.dto.accessory.AccessoryDTO;
import uz.uzkassa.cts.dto.accessory.AccessoryDetailDTO;
import uz.uzkassa.cts.dto.accessory.AccessoryListDTO;
import uz.uzkassa.cts.dto.accessory.AccessoryLookupDTO;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.AccessoryRepository;
import uz.uzkassa.cts.service.AccessoryService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional(readOnly = true)
public class AccessoryServiceImpl extends BaseServiceImpl<Accessory, Long> implements AccessoryService {

    AccessoryRepository accessoryRepository;
    ValidationContext validationContext;

    public AccessoryServiceImpl(AccessoryRepository accessoryRepository, ValidationContext validationContext) {
        super(accessoryRepository);
        this.accessoryRepository = accessoryRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(AccessoryDTO accessoryDTO) {
        validationContext.validate(accessoryDTO);
        Accessory accessory = new Accessory();
        accessory.setName(accessoryDTO.getName());
        accessory.setModelId(accessoryDTO.getModelId());
        accessory.setAccessoryType(accessoryDTO.getType());
        accessory.setDescription(accessoryDTO.getDescription());
        accessory.setPrice(accessoryDTO.getPrice());
        accessory.setRentPrice(accessoryDTO.getRentPrice());
        accessory.setCatalogCode(accessoryDTO.getCatalog().getCode());
        accessory.setCatalogName(accessoryDTO.getCatalog().getName());
        accessory.setPackageName(accessoryDTO.getApackage().getName());
        accessory.setPackageCode(accessoryDTO.getApackage().getCode());
        accessory.setCode(accessoryDTO.getCode());
        accessory.setCodeForUI(accessoryDTO.getCodeForUI());
        accessory.setBonus(accessoryDTO.getBonus());
        return accessoryRepository.save(accessory).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, AccessoryDTO accessoryDTO) {
        accessoryDTO.setId(id);
        validationContext.validate(accessoryDTO);
        return accessoryRepository
                .findById(accessoryDTO.getId())
                .map(accessory -> {
                    accessory.setName(accessoryDTO.getName());
                    accessory.setModelId(accessoryDTO.getModelId());
                    accessory.setAccessoryType(accessoryDTO.getType());
                    accessory.setDescription(accessoryDTO.getDescription());
                    accessory.setPrice(accessoryDTO.getPrice());
                    accessory.setRentPrice(accessoryDTO.getRentPrice());
                    accessory.setCatalogCode(accessoryDTO.getCatalog().getCode());
                    accessory.setCatalogName(accessoryDTO.getCatalog().getName());
                    accessory.setPackageName(accessoryDTO.getApackage().getName());
                    accessory.setPackageCode(accessoryDTO.getApackage().getCode());
                    accessory.setCode(accessoryDTO.getCode());
                    accessory.setCodeForUI(accessoryDTO.getCodeForUI());
                    accessory.setBonus(accessoryDTO.getBonus());
                    return accessoryRepository.save(accessory).getId();
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(ACCESSORIES));
    }

    @Override
    public AccessoryDetailDTO get(Long id) {
        return accessoryRepository
                .findById(id)
                .map(accessory -> {
                    AccessoryDetailDTO accessoryDetailDTO = new AccessoryDetailDTO();
                    accessoryDetailDTO.setId(accessory.getId());
                    accessoryDetailDTO.setName(accessory.getName());
                    accessoryDetailDTO.setModel(accessory.getModel().toCommonDTO());
                    accessoryDetailDTO.setDescription(accessory.getDescription());
                    accessoryDetailDTO.setPrice(accessory.getPrice());
                    accessoryDetailDTO.setRentPrice(accessory.getRentPrice());
                    accessoryDetailDTO.setCatalog(new CommonCodeDTO(accessory.getCatalogCode(), accessory.getCatalogName()));
                    accessoryDetailDTO.setApackage(new CommonCodeDTO(accessory.getPackageCode(), accessory.getPackageName()));
                    accessoryDetailDTO.setCode(accessory.getCode());
                    accessoryDetailDTO.setCodeForUI(accessory.getCodeForUI());
                    accessoryDetailDTO.setBonus(accessory.getBonus());
                    accessoryDetailDTO.setStatus(accessory.getStatus());
                    accessoryDetailDTO.setType(accessory.getAccessoryType());
                    return accessoryDetailDTO;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(ACCESSORIES));
    }

    @Override
    public Page<AccessoryListDTO> getList(BaseFilter filter) {
        ResultList<Accessory> resultList = accessoryRepository.getResultList(filter);
        List<AccessoryListDTO> result = resultList
                .getList()
                .stream()
                .map(accessory -> {
                    AccessoryListDTO accessoryListDTO = new AccessoryListDTO();
                    accessoryListDTO.setId(accessory.getId());
                    accessoryListDTO.setName(accessory.getName());
                    accessoryListDTO.setModel(accessory.getModel().toCommonDTO());
                    accessoryListDTO.setPrice(accessory.getPrice());
                    accessoryListDTO.setRentPrice(accessory.getRentPrice());
                    accessoryListDTO.setCatalog(new CommonCodeDTO(accessory.getCatalogCode(), accessory.getCatalogName()));
                    accessoryListDTO.setApackage(new CommonCodeDTO(accessory.getPackageCode(), accessory.getPackageName()));
                    accessoryListDTO.setCode(accessory.getCode());
                    accessoryListDTO.setCodeForUI(accessory.getCodeForUI());
                    accessoryListDTO.setBonus(accessory.getBonus());
                    accessoryListDTO.setStatus(accessory.getStatus());
                    accessoryListDTO.setType(accessory.getAccessoryType());
                    return accessoryListDTO;
                }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<AccessoryLookupDTO> lookUp(BaseFilter filter) {
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return accessoryRepository.getResultList(filter)
                .getList()
                .stream()
                .map(accessory -> {
                    AccessoryLookupDTO accessoryLookupDTO = new AccessoryLookupDTO();
                    accessoryLookupDTO.setId(accessory.getId());
                    accessoryLookupDTO.setName(accessory.getName());
                    accessoryLookupDTO.setType(accessory.getAccessoryType());
                    return accessoryLookupDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        accessoryRepository.findById(id).map(accessory -> {
            accessory.setStatus(status);
            accessoryRepository.save(accessory);
            return accessory.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(ACCESSORIES));
    }
}
