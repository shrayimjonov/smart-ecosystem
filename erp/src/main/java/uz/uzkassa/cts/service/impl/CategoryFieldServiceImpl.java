package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.inventory.CategoryField;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldBatchDTO;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldDetailAdminDTO;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldValueDetailAdminDTO;
import uz.uzkassa.cts.dto.variationtype.VariationTypeWithOptionListDTO;
import uz.uzkassa.cts.repository.CategoryFieldRepository;
import uz.uzkassa.cts.service.CategoryFieldService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CategoryFieldServiceImpl extends BaseServiceImpl<CategoryField, Long> implements CategoryFieldService {

    private final CategoryFieldRepository categoryFieldRepository;
    private final ValidationContext validationContext;

    public CategoryFieldServiceImpl(CategoryFieldRepository categoryFieldRepository, ValidationContext validationContext) {
        super(categoryFieldRepository);
        this.categoryFieldRepository = categoryFieldRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public void createBatch(CategoryFieldBatchDTO categoryFieldBatchDTO) {
        validationContext.validate(categoryFieldBatchDTO);

        Map<Long, CategoryField> categoryFieldMap = categoryFieldRepository
                .findAllByCategoryId(categoryFieldBatchDTO.getCategoryId())
                .stream()
                .collect(Collectors.toMap(CategoryField::getId, Function.identity()));

        List<CategoryField> categoryFields = categoryFieldBatchDTO.getFieldIds()
                .stream()
                .map(fieldId -> {
                    CategoryField categoryField = categoryFieldRepository.findFirstByCategoryIdAndFieldId(categoryFieldBatchDTO.getCategoryId(), fieldId).orElseGet(CategoryField::new);
                    categoryField.setCategoryId(categoryFieldBatchDTO.getCategoryId());
                    categoryField.setFieldId(fieldId);
                    if (categoryField.getId() != null) {
                        categoryFieldMap.remove(categoryField.getId());
                    }
                    return categoryField;
                }).collect(Collectors.toList());
        categoryFieldRepository.saveAll(categoryFields);
        if (!CollectionUtils.isEmpty(categoryFieldMap)) {
            categoryFieldRepository.deleteAllInBatch(categoryFieldMap.values());
        }
    }

    @Override
    public CategoryFieldDetailAdminDTO getByCategoryId(Long categoryId) {
        CategoryFieldDetailAdminDTO categoryFieldDetailDTO = new CategoryFieldDetailAdminDTO();
        List<CategoryField> categoryFields = categoryFieldRepository.findAllByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(categoryFields)) {
            return categoryFieldDetailDTO;
        }
        categoryFieldDetailDTO.setCategory(categoryFields.get(0).getCategory().toCommonDTO());
        categoryFields.forEach(categoryField -> categoryFieldDetailDTO.addField(categoryField.getField().getLookUpDTO()));
        return categoryFieldDetailDTO;
    }

    @Override
    public CategoryFieldValueDetailAdminDTO getFieldAndValuesByCategoryId(Long categoryId) {
        CategoryFieldValueDetailAdminDTO categoryFieldValueDetailDTO = new CategoryFieldValueDetailAdminDTO();
        List<CategoryField> categoryFields = categoryFieldRepository.findAllByCategoryId(categoryId);
        if (CollectionUtils.isEmpty(categoryFields)) {
            return categoryFieldValueDetailDTO;
        }
        categoryFieldValueDetailDTO.setCategory(categoryFields.get(0).getCategory().toCommonDTO());
        categoryFields.forEach(categoryField -> {
            VariationTypeWithOptionListDTO variationTypeWithOptionListDTO = new VariationTypeWithOptionListDTO();
            variationTypeWithOptionListDTO.setId(categoryField.getField().getId());
            variationTypeWithOptionListDTO.setName(categoryField.getField().getName());
            variationTypeWithOptionListDTO.setType(categoryField.getField().getType());
            variationTypeWithOptionListDTO.setCode(categoryField.getField().getCode());
            categoryField.getField().getOptions().forEach(variationOption -> variationTypeWithOptionListDTO.addValue(variationOption.toCommonDTO()));
            categoryFieldValueDetailDTO.addField(variationTypeWithOptionListDTO);
        });
        return categoryFieldValueDetailDTO;
    }
}
