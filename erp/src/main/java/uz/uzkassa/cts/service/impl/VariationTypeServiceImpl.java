package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.inventory.VariationType;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.variationtype.VariationTypeDTO;
import uz.uzkassa.cts.dto.variationtype.VariationTypeLookUpDTO;
import uz.uzkassa.cts.dto.variationtype.admin.VariationTypeDetailAdminDTO;
import uz.uzkassa.cts.dto.variationtype.admin.VariationTypeListAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.VariationTypeRepository;
import uz.uzkassa.cts.service.VariationTypeService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class VariationTypeServiceImpl extends BaseServiceImpl<VariationType, Long> implements VariationTypeService {

    private final VariationTypeRepository variationTypeRepository;
    private final ValidationContext validationContext;

    public VariationTypeServiceImpl(VariationTypeRepository variationTypeRepository, ValidationContext validationContext) {
        super(variationTypeRepository);
        this.variationTypeRepository = variationTypeRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(VariationTypeDTO variationTypeDTO) {
        validationContext.validate(variationTypeDTO);
        VariationType variationType = new VariationType();
        variationType.setType(variationTypeDTO.getWidgetType());
        variationType.setNameUzLatin(variationTypeDTO.getNameUzLatin());
        variationType.setNameUzCyrillic(variationTypeDTO.getNameUzCyrillic());
        variationType.setNameRu(variationTypeDTO.getNameRu());
        return variationTypeRepository.save(variationType).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, VariationTypeDTO variationTypeDTO) {
        variationTypeDTO.setId(id);
        validationContext.validate(variationTypeDTO);
        return variationTypeRepository.findById(id)
                .map(variationType -> {
                    variationType.setType(variationTypeDTO.getWidgetType());
                    variationType.setNameUzLatin(variationTypeDTO.getNameUzLatin());
                    variationType.setNameUzCyrillic(variationTypeDTO.getNameUzCyrillic());
                    variationType.setNameRu(variationTypeDTO.getNameRu());
                    return variationTypeRepository.save(variationType).getId();
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(VARIATION_TYPE));

    }

    @Override
    public Page<VariationTypeListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<VariationType> resultList = variationTypeRepository.getResultList(filter);
        List<VariationTypeListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(variationType -> {
                    VariationTypeListAdminDTO variationTypeListDTO = new VariationTypeListAdminDTO();
                    variationTypeListDTO.setId(variationType.getId());
                    variationTypeListDTO.setWidgetType(variationType.getType());
                    variationTypeListDTO.setName(variationType.getName());
                    return variationTypeListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<VariationTypeLookUpDTO> lookUp(BaseFilter filter) {
        return variationTypeRepository.getResultList(filter)
                .getList()
                .stream()
                .map(VariationType::getLookUpDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VariationTypeDetailAdminDTO getForAdmin(Long id) {
        return variationTypeRepository
                .findById(id)
                .map(variationType -> {
                    VariationTypeDetailAdminDTO variationTypeDetailDTO = new VariationTypeDetailAdminDTO();
                    variationTypeDetailDTO.setId(variationType.getId());
                    variationTypeDetailDTO.setWidgetType(variationType.getType());
                    variationTypeDetailDTO.setNameUzLatin(variationType.getNameUzLatin());
                    variationTypeDetailDTO.setNameUzCyrillic(variationType.getNameUzCyrillic());
                    variationTypeDetailDTO.setNameRu(variationType.getNameRu());
                    return variationTypeDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(BANK));

    }
}
