package uz.uzkassa.cts.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.reference.ReferenceDTO;
import uz.uzkassa.cts.dto.reference.admin.ReferenceDetailAdminDTO;
import uz.uzkassa.cts.dto.reference.admin.ReferenceListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.filter.ReferenceFilter;
import uz.uzkassa.cts.repository.ReferenceRepository;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.ReferenceService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class ReferenceServiceImpl extends BaseServiceImpl<Reference, Long> implements ReferenceService, CacheConstants {

    private final ReferenceRepository referenceRepository;
    private final CacheService cacheService;
    private final ValidationContext validationContext;

    public ReferenceServiceImpl(ReferenceRepository referenceRepository, CacheService cacheService, ValidationContext validationContext) {
        super(referenceRepository);
        this.referenceRepository = referenceRepository;
        this.cacheService = cacheService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(ReferenceDTO referenceDTO) {
        validationContext.validate(referenceDTO);
        Reference reference = new Reference();
        reference.setType(referenceDTO.getType());
        reference.setCode(referenceDTO.getCode());
        reference.setShortName(referenceDTO.getShortName());
        reference.setColor(referenceDTO.getColor());
        reference.setNameUzLatin(referenceDTO.getNameUzLatin());
        reference.setNameUzCyrillic(referenceDTO.getNameUzCyrillic());
        reference.setNameRu(referenceDTO.getNameRu());
        reference.setExternalId(referenceDTO.getExternalId());
        reference.setExternalCode(referenceDTO.getExternalCode());
        reference.setParentId(referenceDTO.getParentId());
        reference.setSortOrder(referenceDTO.getSortOrder());
        reference.setCompanyId(getCompanyId(referenceDTO.getCompanyId()));
        referenceRepository.save(reference);
        cacheService.clearByEntityType(reference.getEntityType());
        return reference.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, ReferenceDTO referenceDTO) {
        referenceDTO.setId(id);
        validationContext.validate(referenceDTO);
        return referenceRepository.findById(id)
                .map(reference -> {
                    reference.setType(referenceDTO.getType());
                    reference.setCode(referenceDTO.getCode());
                    reference.setShortName(referenceDTO.getShortName());
                    reference.setColor(referenceDTO.getColor());
                    reference.setNameUzLatin(referenceDTO.getNameUzLatin());
                    reference.setNameUzCyrillic(referenceDTO.getNameUzCyrillic());
                    reference.setNameRu(referenceDTO.getNameRu());
                    reference.setExternalId(referenceDTO.getExternalId());
                    reference.setExternalCode(referenceDTO.getExternalCode());
                    reference.setParentId(referenceDTO.getParentId());
                    reference.setSortOrder(referenceDTO.getSortOrder());
                    referenceRepository.save(reference);
                    cacheService.clearByEntityType(reference.getEntityType());
                    return id;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(REFERENCE));

    }

    @Override
    public Page<ReferenceListAdminDTO> getListForAdmin(ReferenceFilter filter) {
        ResultList<Reference> resultList = referenceRepository.getResultList(filter);
        List<ReferenceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(reference -> {
                    ReferenceListAdminDTO referenceListDTO = new ReferenceListAdminDTO();
                    referenceListDTO.setId(reference.getId());
                    referenceListDTO.setType(reference.getType());
                    referenceListDTO.setCode(reference.getCode());
                    referenceListDTO.setNameUzLatin(reference.getNameUzLatin());
                    referenceListDTO.setNameUzCyrillic(reference.getNameUzCyrillic());
                    referenceListDTO.setNameRu(reference.getNameRu());
                    referenceListDTO.setStatus(reference.getStatus());
                    if (reference.getParentId() != null) {
                        referenceListDTO.setParent(reference.getParent().toCommonDTO());
                    }
                    return referenceListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonCodeDTO> lookUp(ReferenceFilter filter) {
        return referenceRepository.getResultList(filter)
                .getList()
                .stream()
                .map(Reference::toCommonCodeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReferenceDetailAdminDTO get(Long id) {
        return referenceRepository
                .findById(id)
                .map(reference -> {
                    ReferenceDetailAdminDTO referenceDetailDTO = new ReferenceDetailAdminDTO();
                    referenceDetailDTO.setId(reference.getId());
                    referenceDetailDTO.setType(reference.getType());
                    referenceDetailDTO.setCode(reference.getCode());
                    referenceDetailDTO.setShortName(reference.getShortName());
                    referenceDetailDTO.setColor(reference.getColor());
                    referenceDetailDTO.setExternalId(reference.getExternalId());
                    referenceDetailDTO.setExternalCode(reference.getExternalCode());
                    if (reference.getParentId() != null) {
                        referenceDetailDTO.setParent(reference.getParent().toCommonDTO());
                    }
                    referenceDetailDTO.setNameUzLatin(reference.getNameUzLatin());
                    referenceDetailDTO.setNameUzCyrillic(reference.getNameUzCyrillic());
                    referenceDetailDTO.setNameRu(reference.getNameRu());
                    referenceDetailDTO.setSortOrder(reference.getSortOrder());
                    if (reference.getCompanyId() != null) {
                        referenceDetailDTO.setCompany(reference.getCompany().toCommonDTO());
                    }
                    return referenceDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(REFERENCE));

    }

    @Override
    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        referenceRepository
                .findById(id)
                .ifPresentOrElse(reference -> {
                    reference.setStatus(status);
                    referenceRepository.save(reference);
                    cacheService.clearByEntityType(reference.getEntityType());
                }, () -> validationService.entityNotFoundExceptionThrow(REFERENCE).get());

    }

    @Override
    @Cacheable(cacheNames = REFERENCE_ITEMS_BY_TYPE_CACHE)
    public List<CommonCodeDTO> getItems(ReferenceType type) {
        ReferenceFilter filter = new ReferenceFilter();
        filter.setType(type.name());
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return referenceRepository.getResultList(filter)
                .getList()
                .stream()
                .map(Reference::toCommonCodeDTO)
                .collect(Collectors.toList());
    }
}
