package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.inventory.VariationOption;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.variationoption.VariationOptionDTO;
import uz.uzkassa.cts.dto.variationoption.admin.VariationOptionDetailAdminDTO;
import uz.uzkassa.cts.dto.variationoption.admin.VariationOptionListAdminDTO;
import uz.uzkassa.cts.filter.VariationOptionFilter;
import uz.uzkassa.cts.repository.VariationOptionRepository;
import uz.uzkassa.cts.service.VariationOptionService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class VariationOptionServiceImpl extends BaseServiceImpl<VariationOption, Long> implements VariationOptionService {

    private final VariationOptionRepository variationOptionRepository;
    private final ValidationContext validationContext;

    public VariationOptionServiceImpl(VariationOptionRepository variationOptionRepository, ValidationContext validationContext) {
        super(variationOptionRepository);
        this.variationOptionRepository = variationOptionRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(VariationOptionDTO variationOptionDTO) {
        validationContext.validate(variationOptionDTO);
        VariationOption variationOption = new VariationOption();
        variationOption.setNameUzLatin(variationOptionDTO.getNameUzLatin());
        variationOption.setNameUzCyrillic(variationOptionDTO.getNameUzCyrillic());
        variationOption.setNameRu(variationOptionDTO.getNameRu());
        variationOption.setVariationTypeId(variationOptionDTO.getVariationTypeId());
        return variationOptionRepository.save(variationOption).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, VariationOptionDTO variationOptionDTO) {
        variationOptionDTO.setId(id);
        validationContext.validate(variationOptionDTO);
        return variationOptionRepository.findById(id)
                .map(variationOption -> {
                    variationOption.setNameUzLatin(variationOptionDTO.getNameUzLatin());
                    variationOption.setNameUzCyrillic(variationOptionDTO.getNameUzCyrillic());
                    variationOption.setNameRu(variationOptionDTO.getNameRu());
                    variationOption.setVariationTypeId(variationOptionDTO.getVariationTypeId());
                    return variationOptionRepository.save(variationOption).getId();
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(VARIATION_OPTION));

    }

    @Override
    public Page<VariationOptionListAdminDTO> getListForAdmin(VariationOptionFilter filter) {
        ResultList<VariationOption> resultList = variationOptionRepository.getResultList(filter);
        List<VariationOptionListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(variationOption -> {
                    VariationOptionListAdminDTO variationOptionListDTO = new VariationOptionListAdminDTO();
                    variationOptionListDTO.setId(variationOption.getId());
                    variationOptionListDTO.setVariationType(variationOption.getVariationType().toCommonDTO());
                    variationOptionListDTO.setNameUzLatin(variationOption.getNameUzLatin());
                    variationOptionListDTO.setNameUzCyrillic(variationOption.getNameUzCyrillic());
                    variationOptionListDTO.setNameRu(variationOption.getNameRu());
                    return variationOptionListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(VariationOptionFilter filter) {
        return variationOptionRepository.getResultList(filter)
                .getList()
                .stream()
                .map(SimpleEntity::toCommonDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VariationOptionDetailAdminDTO getForAdmin(Long id) {
        return variationOptionRepository
                .findById(id)
                .map(variationOption -> {
                    VariationOptionDetailAdminDTO variationOptionDetailDTO = new VariationOptionDetailAdminDTO();
                    variationOptionDetailDTO.setId(variationOption.getId());
                    variationOptionDetailDTO.setVariationType(variationOption.getVariationType().toCommonDTO());
                    variationOptionDetailDTO.setNameUzLatin(variationOption.getNameUzLatin());
                    variationOptionDetailDTO.setNameUzCyrillic(variationOption.getNameUzCyrillic());
                    variationOptionDetailDTO.setNameRu(variationOption.getNameRu());
                    return variationOptionDetailDTO;
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(VARIATION_OPTION));

    }
}
