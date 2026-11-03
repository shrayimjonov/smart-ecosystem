package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.category.Category;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.category.CategoryDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryDetailDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryListDTO;
import uz.uzkassa.smartpossupply.dto.category.CategoryTreeDTO;
import uz.uzkassa.smartpossupply.dto.category.MobileCategoryTreeDTO;
import uz.uzkassa.smartpossupply.dto.payload.CategoryTransferPayload;
import uz.uzkassa.smartpossupply.dto.payload.CreateCatalogFromTasnifCatalogPayload;
import uz.uzkassa.smartpossupply.dto.soliq.MxikDTO;
import uz.uzkassa.smartpossupply.dto.soliq.PackageDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TasnifPackageDTO;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.enums.EnvironmentType;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.integration.TasnifClient;
import uz.uzkassa.smartpossupply.mappers.CategoryMapper;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqCategoryProducer;
import uz.uzkassa.smartpossupply.rabbitmq.producer.RabbitMqTasnifCatalogProducer;
import uz.uzkassa.smartpossupply.repository.CategoryRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;
import uz.uzkassa.smartpossupply.utils.MarkUtils;
import uz.uzkassa.smartpossupply.validator.ValidationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryService extends BaseService {

    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    CategoryMapper categoryMapper;
    RabbitMqTasnifCatalogProducer rabbitMqTasnifCatalogProducer;
    TasnifClient tasnifClient;
    ProductService productService;
    RabbitMqCategoryProducer rabbitMqCategoryProducer;
    ValidationContext validationContext;

    @Transactional
    public Long create(CategoryDTO categoryDTO) {
        validationContext.validate(categoryDTO);
        return this.createEntity(categoryDTO).getId();
    }

    private Category createEntity(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        categoryRepository.saveAndFlush(category);
        return category;
    }

    @Transactional
    public Category createOrGet(CategoryDTO categoryDTO) {
        Category category = categoryRepository.findFirstByBranchIdAndCatalogCodeAndEnvironmentTypeAndDeletedIsFalse(categoryDTO.getBranchId(), categoryDTO.getCatalogCode(), categoryDTO.getEnvironmentType()).orElse(null);
        if (category == null) {
            category = this.createEntity(categoryDTO);
        }
        return category;
    }

    @Transactional
    public void createDefault(Branch branch) {
        Category category = new Category();
        category.setName("Другие");
        category.setMain(true);
        category.setBranchId(branch.getId());
        category.setEnvironmentType(EnvironmentType.WEB);
        categoryRepository.save(category);
    }

    public CategoryDetailDTO get(Long id) {
        return categoryRepository.getDetail(id).map(category -> {
            CategoryDetailDTO categoryDetailDTO = new CategoryDetailDTO();
            categoryDetailDTO.setId(category.getId());
            categoryDetailDTO.setName(category.getName());
            categoryDetailDTO.setBranch(category.getBranch().toCommonDTO());
            if (category.getParent() != null) {
                categoryDetailDTO.setParent(category.getParent().toCommonDTO());
            }
            return categoryDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Category.class.getSimpleName(), "id", id));
    }

    @Transactional
    public Long update(Long id, CategoryDTO categoryDTO) {
        return categoryRepository.findById(id).map(category -> {
            categoryDTO.setId(id);
            validationContext.validate(categoryDTO);

            category.setName(categoryDTO.getName());
            category.setEnvironmentType(categoryDTO.getEnvironmentType());

            Branch branch = branchRepository.findById(categoryDTO.getBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "id", categoryDTO.getBranchId()));
            category.setBranch(branch);
            category.setBranchId(branch.getId());

            if (categoryDTO.getParentId() != null) {
                Category parent = categoryRepository.findById(categoryDTO.getParentId()).orElseThrow(validationService.entityNotFoundExceptionThrow(Category.class.getSimpleName(), "parentId", categoryDTO.getParentId()));
                category.setParent(parent);
                category.setParentId(parent.getId());
            }
            categoryRepository.save(category);
            return category.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Category.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        categoryRepository.findById(id).map(category -> {
            if (category.isMain()) {
                throw validationService.badRequestExceptionThrow("Вы не можете обновить статус основной категории", "Not allowed to change main category").get();
            }
            category.setStatus(status);
            categoryRepository.save(category);
            return category;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Category.class.getSimpleName(), "id", id));
    }

    public Page<CategoryListDTO> getList(BaseFilter filter) {
        if (!StringUtils.hasText(filter.getType())) {
            filter.setType(EnvironmentType.WEB.getCode());
        }
        ResultList<Category> resultList = categoryRepository.getResultList(filter);
        List<CategoryListDTO> result = resultList
                .getList()
                .stream()
                .map(category -> {
                    CategoryListDTO categoryListDTO = new CategoryListDTO();
                    categoryListDTO.setId(category.getId());
                    categoryListDTO.setName(category.getName());
                    categoryListDTO.setBranch(category.getBranch().toCommonDTO());
                    if (category.getParent() != null) {
                        categoryListDTO.setParent(category.getParent().toCommonDTO());
                    }
                    return categoryListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    public List<CommonDTO> lookUp(BaseFilter filter) {
        if (!StringUtils.hasText(filter.getType())) {
            filter.setType(EnvironmentType.WEB.getCode());
        }
        filter.setStatus(CommonStatus.ACTIVE.getCode());
        return categoryRepository.lookUp(filter)
                .stream()
                .map(Category::toCommonDTO)
                .toList();
    }

    public List<CategoryTreeDTO> treeList(BaseFilter filter) {
        if (!StringUtils.hasText(filter.getType())) {
            filter.setType(EnvironmentType.WEB.getCode());
        }
        List<CategoryTreeDTO> categories;
        if (companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS)) {
            categories = categoryRepository.treeListSorted(filter);
        } else {
            categories = categoryRepository.treeList(filter);
        }
        Map<Long, CategoryTreeDTO> map = categories.stream().collect(Collectors.toMap(CategoryTreeDTO::getId, category -> category, (a, b) -> b, LinkedHashMap::new));
        List<CategoryTreeDTO> result = new ArrayList<>();
        categories.forEach(category -> {
            CategoryTreeDTO parentCategory = map.get(category.getParentId());
            if (parentCategory == null) {
                result.add(category);
                return;
            }
            parentCategory.getChildren().add(category);
        });

        result.forEach(category -> sumProductCount(category, category.getChildren()));
        return result;
    }


    public List<MobileCategoryTreeDTO> treeListForMobile(BaseFilter filter) {
        filter.setEnabledSorting(companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS));
        List<MobileCategoryTreeDTO> categories = categoryRepository.treeListForMobile(filter);
        List<MobileCategoryTreeDTO> result = new ArrayList<>();
        categories.forEach(category -> {
            filter.setParentId(category.getId());
            List<MobileCategoryTreeDTO> childCategories = categoryRepository.treeListForMobile(filter);
            category.getChildren().addAll(childCategories);
            result.add(category);
        });
        return categories.stream().filter(mobileCategoryTreeDTO -> mobileCategoryTreeDTO.getChildren().isEmpty()).toList();
    }

    private Long sumProductCount(CategoryTreeDTO category, List<CategoryTreeDTO> children) {
        Long sum = category.getProductCount();
        for (CategoryTreeDTO child : children) {
            sum = sum + sumProductCount(child, child.getChildren());
        }
        category.setProductCount(sum);
        return sum;
    }

    public void sync(Long branchId) {
        branchRepository
                .findById(branchId)
                .map(branch -> {
                    Company company = branch.getCompany();
                    rabbitMqTasnifCatalogProducer.createCatalogFromTasnifCatalogQueue(
                            new CreateCatalogFromTasnifCatalogPayload(company.getId(), company.getTin(), branchId, getCurrentUserId(), getUnit().getId(), company.getProductGroup() != null ? company.getProductGroup().name() : null)
                    );
                    return branch;
                }).orElseThrow(validationService.entityNotFoundExceptionThrow(Branch.class.getSimpleName(), "branchId", branchId));
    }

    public List<MxikDTO> getCategories(String tin) {
        return tasnifClient.getCompanyCatalog(tin);
    }

    @Transactional
    public void createCatalogFromTasnifCatalogQueue(CreateCatalogFromTasnifCatalogPayload payload) {
        List<MxikDTO> mxikDTOList = tasnifClient.getCompanyCatalog(payload.getTin());
        for (MxikDTO mxikDTO : mxikDTOList) {
            if (
                    !StringUtils.hasText(mxikDTO.getGroupName()) ||
                            !StringUtils.hasText(mxikDTO.getClassName()) ||
                            !StringUtils.hasText(mxikDTO.getPositionName())
            ) {
                continue;
            }
            Category groupCategory = categoryRepository
                    .findFirstByNameAndBranchIdAndDeletedIsFalse(mxikDTO.getGroupName().trim(), payload.getBranchId())
                    .orElse(null);
            if (groupCategory == null) {
                groupCategory = new Category();
                groupCategory.setName(mxikDTO.getGroupName().trim());
                groupCategory.setBranchId(payload.getBranchId());
                groupCategory = categoryRepository.save(groupCategory);
                categoryRepository.flush();
            }

            Category classCategory = categoryRepository
                    .findFirstByNameAndBranchIdAndDeletedIsFalse(mxikDTO.getClassName().trim(), payload.getBranchId())
                    .orElse(null);
            if (classCategory == null) {
                classCategory = new Category();
                classCategory.setName(mxikDTO.getClassName().trim());
                classCategory.setParentId(groupCategory.getId());
                classCategory.setBranchId(payload.getBranchId());
                classCategory = categoryRepository.save(classCategory);
                categoryRepository.flush();
            }

            Category positionCategory = categoryRepository
                    .findFirstByNameAndBranchIdAndDeletedIsFalse(mxikDTO.getPositionName().trim(), payload.getBranchId())
                    .orElse(null);
            if (positionCategory == null) {
                positionCategory = new Category();
                positionCategory.setName(mxikDTO.getPositionName().trim());
                positionCategory.setCatalogCode(mxikDTO.getMxikCode());
                positionCategory.setCatalogName(mxikDTO.getName());
                positionCategory.setParentId(classCategory.getId());
                positionCategory.setBranchId(payload.getBranchId());
                positionCategory = categoryRepository.save(positionCategory);
                categoryRepository.flush();
            }

            if (StringUtils.hasText(mxikDTO.getAttributeName())) {
                Product product = productRepository
                        .findFirstByTasnifIdAndBranchIdAndDeletedIsFalse(mxikDTO.getPkey(), payload.getBranchId())
                        .orElse(null);

                if (product == null) {
                    product = productService.getProductByBarcodeAndVatBarcode(mxikDTO.getInternationalCode(), mxikDTO.getMxikCode(), payload.getBranchId());
                }
                if (product == null) {
                    product = new Product();
                }

                product.setTasnifId(mxikDTO.getPkey());
                product.setCatalogName(mxikDTO.getName());
                product.setVatBarcode(mxikDTO.getMxikCode());
                if (product.getId() == null && !CollectionUtils.isEmpty(mxikDTO.getPackageNames())) {
                    product.setPackageName(mxikDTO.getPackageNames().getFirst().getName());
                    product.setPackageCode(mxikDTO.getPackageNames().getFirst().getCode());
                }
                if (product.getId() == null) {
                    product.setName(mxikDTO.getName());
                    product.setBarcode(mxikDTO.getInternationalCode());
                    product.setBrand(mxikDTO.getBrandName());
                    product.setCategoryId(positionCategory.getId());
                    product.setBranchId(payload.getBranchId());
                    product.setCompanyId(payload.getCompanyId());
                    product.setUnitId(payload.getUnitId());
                    product.setCreatedDate(LocalDateTime.now());
                    product.setCreatorId(payload.getCreatorId());
                } else {
                    product.setUpdatedDate(LocalDateTime.now());
                    product.setUpdaterId(payload.getCreatorId());
                }
                if (product.getProductType() == null) {
                    product.setProductType(MarkUtils.getProductType(payload.getProductGroup() != null ? ProductGroup.valueOf(payload.getProductGroup()) : null, mxikDTO.getMxikCode()));
                    product.setHasMark(product.getProductType() != null);
                }
                productRepository.saveAndFlush(product);
            }
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        categoryRepository.findById(id).map(category -> {
            if (category.isMain()) {
                throw validationService.badRequestExceptionThrow("Вы не можете удалить основную категорию", "Not allowed to delete main category").get();
            }
            if (productRepository.existsByCategoryIdAndDeletedIsFalse(id)) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.YOU_CAN_NOT_DELETE_CATEGORY)).get();
            }
            if (!CollectionUtils.isEmpty(category.getChildren())) {
                throw validationService.badRequestExceptionThrow(localizationService.localize(MessageConstants.YOU_CAN_NOT_DELETE_CATEGORY_SUB)).get();
            }
            categoryRepository.deleteById(id);
            return category;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(Category.class.getSimpleName(), "id", id));
    }

    /**
     * Get product package info's from Tasnif api.
     * getPackagesByCatalogCode - it returns packages registered by tasnif moderators or default packages
     * getCompanyCatalogByProduct - it returns packages registered by company itself at tasnif.
     */
    public List<TasnifPackageDTO> getPackages(String catalogCode, String tin) {
        Map<Integer, TasnifPackageDTO> packages = new HashMap<>();
        List<TasnifPackageDTO> resultList = tasnifClient.getPackagesByCatalogCode(catalogCode);
        if (!CollectionUtils.isEmpty(resultList)) {
            for (TasnifPackageDTO tasnifPackageDTO : resultList) {
                packages.put(tasnifPackageDTO.getCode(), tasnifPackageDTO);
            }
        }
        List<MxikDTO> result = tasnifClient.getCompanyCatalogByProduct(tin, catalogCode);
        if (!CollectionUtils.isEmpty(result)) {
            for (MxikDTO mxikDTO : result) {
                for (PackageDTO packageDTO : mxikDTO.getPackageNames()) {
                    packages.putIfAbsent(Integer.valueOf(packageDTO.getCode()), new TasnifPackageDTO(packageDTO.getNameRu(), Integer.valueOf(packageDTO.getCode()), packageDTO.getNameUz()));
                }
            }
        }
        return new ArrayList<>(packages.values());
    }

    public void transfer(CategoryTransferPayload categoryTransferPayload) {
        rabbitMqCategoryProducer.transfer(categoryTransferPayload);
    }

    @Transactional
    public void transferQueue(CategoryTransferPayload categoryTransferPayload) {
        Branch branch = branchRepository.findById(categoryTransferPayload.getToBranchId()).orElseThrow(validationService.notFoundExceptionThrow(Branch.class.getSimpleName()));
        categoryRepository
                .findAllByBranchIdAndParentIdIsNullAndDeletedIsFalse(categoryTransferPayload.getFromBranchId())
                .forEach(fromCategory -> this.recursion(fromCategory, branch, null));
    }

    private void recursion(Category fromCategory, Branch branch, Long parentId) {
        Category category = categoryRepository.findFirstByBranchIdAndNameIgnoreCaseAndDeletedIsFalse(branch.getId(), fromCategory.getName()).orElseGet(Category::new);
        category.setName(fromCategory.getName());
        category.setEnvironmentType(fromCategory.getEnvironmentType());
        category.setCatalogCode(fromCategory.getCatalogCode());
        category.setCatalogName(fromCategory.getCatalogName());
        category.setMain(fromCategory.isMain());
        category.setBranchId(branch.getId());
        category.setParentId(parentId);
        categoryRepository.save(category);
        if (CollectionUtils.isNotEmpty(fromCategory.getProducts())) {
            productService.transfer(fromCategory.getProducts(), category, branch);
            fromCategory.getChildren().forEach(fromChildCategory -> this.recursion(fromChildCategory, branch, category.getId()));
        }
    }
}
