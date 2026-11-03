package uz.uzkassa.cts.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.inventory.Category;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.category.CategoryDTO;
import uz.uzkassa.cts.dto.category.admin.CategoryDetailAdminDTO;
import uz.uzkassa.cts.dto.category.admin.CategoryListAdminDTO;
import uz.uzkassa.cts.dto.category.admin.CategoryTreeAdminDTO;
import uz.uzkassa.cts.dto.categoryfield.CategoryFieldBatchDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.CategoryFieldRepository;
import uz.uzkassa.cts.repository.CategoryRepository;
import uz.uzkassa.cts.service.CategoryFieldService;
import uz.uzkassa.cts.service.CategoryService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category, Long> implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryFieldRepository categoryFieldRepository;
    private final CategoryFieldService categoryFieldService;
    private final ValidationContext validationContext;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryFieldRepository categoryFieldRepository, CategoryFieldService categoryFieldService, ValidationContext validationContext) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
        this.categoryFieldRepository = categoryFieldRepository;
        this.categoryFieldService = categoryFieldService;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(CategoryDTO categoryDTO) {
        validationContext.validate(categoryDTO);
        Category category = new Category();
        category.setNameUzLatin(categoryDTO.getNameUzLatin());
        category.setNameUzCyrillic(categoryDTO.getNameUzCyrillic());
        category.setNameRu(categoryDTO.getNameRu());
        category.setParentId(categoryDTO.getParentId());
        category.setCompanyId(categoryDTO.getCompanyId());
        categoryRepository.save(category);
        if (categoryDTO.getParentId() != null) {
            List<Long> categoryFields = categoryFieldRepository.findAllFieldIdsByCategoryId(categoryDTO.getParentId());
            CategoryFieldBatchDTO categoryFieldBatchDTO = new CategoryFieldBatchDTO();
            categoryFieldBatchDTO.setFieldIds(categoryFields);
            categoryFieldBatchDTO.setCategoryId(category.getId());
            categoryFieldService.createBatch(categoryFieldBatchDTO);
        }
        return category.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CategoryDTO categoryDTO) {
        return categoryRepository.findById(id).map(category -> {
            category.setNameUzLatin(categoryDTO.getNameUzLatin());
            category.setNameUzCyrillic(categoryDTO.getNameUzCyrillic());
            category.setNameRu(categoryDTO.getNameRu());
            category.setParentId(categoryDTO.getParentId());
            category.setCompanyId(categoryDTO.getCompanyId());
            return categoryRepository.save(category).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CATEGORY));
    }

    @Override
    public CategoryDetailAdminDTO getForAdmin(Long id) {
        return categoryRepository.findById(id).map(category -> {
            CategoryDetailAdminDTO categoryDetailDTO = new CategoryDetailAdminDTO();
            categoryDetailDTO.setId(category.getId());
            categoryDetailDTO.setNameUzLatin(category.getNameUzLatin());
            categoryDetailDTO.setNameUzCyrillic(category.getNameUzCyrillic());
            categoryDetailDTO.setNameRu(category.getNameRu());
            if (category.getParentId() != null) {
                categoryDetailDTO.setParent(category.getParent().toCommonDTO());
            }
            if (category.getCompanyId() != null) {
                categoryDetailDTO.setCompany(category.getCompany().toShortDTO());
            }
            return categoryDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(CATEGORY));
    }

    @Override
    public Page<CategoryListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Category> resultList = categoryRepository.getResultList(filter);
        List<CategoryListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(category -> {
                    CategoryListAdminDTO categoryListDTO = new CategoryListAdminDTO();
                    categoryListDTO.setId(category.getId());
                    categoryListDTO.setName(category.getName());
                    if (category.getParentId() != null) {
                        categoryListDTO.setParent(category.getParent().toCommonDTO());
                    }
                    if (category.getCompanyId() != null) {
                        categoryListDTO.setCompany(category.getCompany().toShortDTO());
                    }
                    return categoryListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(BaseFilter filter) {
        return categoryRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryTreeAdminDTO> treeList(BaseFilter filter) {
        List<CategoryTreeAdminDTO> categories = categoryRepository.treeList(filter);
        Map<Long, CategoryTreeAdminDTO> map = categories.stream().collect(Collectors.toMap(CategoryTreeAdminDTO::getId, category -> category, (a, b) -> b, LinkedHashMap::new));
        List<CategoryTreeAdminDTO> result = new ArrayList<>();
        categories.forEach(category -> {
            CategoryTreeAdminDTO parentCategory = map.get(category.getParentId());
            if (parentCategory == null) {
                result.add(category);
                return;
            }
            parentCategory.getChildren().add(category);
        });
        result.forEach(category -> sumProductCount(category, category.getChildren()));
        return result;
    }

    private Long sumProductCount(CategoryTreeAdminDTO category, List<CategoryTreeAdminDTO> children) {
        Long sum = category.getProductCount();
        for (CategoryTreeAdminDTO child : children) {
            sum = sum + sumProductCount(child, child.getChildren());
        }

        category.setProductCount(sum);
        return sum;
    }
}
