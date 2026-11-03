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
import uz.uzkassa.horeca.domain.Category;
import uz.uzkassa.horeca.domain.base.SimpleEntity;
import uz.uzkassa.horeca.domain.product.Product;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.dto.category.*;
import uz.uzkassa.horeca.dto.soliq.MxikDTO;
import uz.uzkassa.horeca.dto.soliq.MxikHistoryDTO;
import uz.uzkassa.horeca.dto.soliq.PackageDTO;
import uz.uzkassa.horeca.dto.tasnif.TasnifMxikHistoryResponseListDTO;
import uz.uzkassa.horeca.dto.tasnif.TasnifPackageDTO;
import uz.uzkassa.horeca.enums.CategoryType;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.integration.TasnifClient;
import uz.uzkassa.horeca.interfaces.CrudAdvanced;
import uz.uzkassa.horeca.repository.CategoryRepository;
import uz.uzkassa.horeca.repository.ProductRepository;
import uz.uzkassa.horeca.web.rest.errors.ConflictException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CategoryService
        extends BaseService
        implements CrudAdvanced<Long, CategoryDTO, CategoryDetailDTO, CategoryListDTO, BaseFilter, CommonStatus, CategoryLookUpDTO> {

    ProductRepository productRepository;
    CategoryRepository categoryRepository;
    TasnifClient tasnifClient;
    ProductService productService;
    DepartmentService departmentService;

    @Override
    public void validate(CategoryDTO categoryDTO) {
        if (StringUtils.isEmpty(categoryDTO.getName())) {
            throw badRequestExceptionThrow("Name is required").get();
        }
        if (categoryDTO.getType() == null) {
            throw badRequestExceptionThrow("Type is required").get();
        }
        if (
                categoryDTO.getParentId() == null &&
                        (CategoryType.FOOD.equals(categoryDTO.getType()) || CategoryType.COMPENSATION.equals(categoryDTO.getType())) &&
                        (
                                categoryDTO.getCatalog() == null ||
                                        StringUtils.isEmpty(categoryDTO.getCatalog().getCode()) ||
                                        StringUtils.isEmpty(categoryDTO.getCatalog().getName())
                        )
        ) {
            throw badRequestExceptionThrow("Catalog code and name are required if category type is FOOD").get();
        }
        if (
                categoryDTO.getParentId() == null &&
                        (CategoryType.FOOD.equals(categoryDTO.getType()) || CategoryType.COMPENSATION.equals(categoryDTO.getType())) &&
                        (StringUtils.isEmpty(categoryDTO.getPackageName()) || StringUtils.isEmpty(categoryDTO.getPackageCode()))
        ) {
            throw badRequestExceptionThrow("Catalog package name and package code are required if category type is FOOD").get();
        }

        Long branchId = getCurrentBranchId(categoryDTO.getBranchId());
        if (
                categoryDTO.getId() == null &&
                        categoryRepository.existsByNameIgnoreCaseAndBranchIdAndDeletedIsFalse(categoryDTO.getName(), branchId)
        ) {
            throw conflictExceptionThrow("Category exists by this name :" + categoryDTO.getName()).get();
        }
        if (
                categoryDTO.getId() != null &&
                        categoryRepository.existsByNameIgnoreCaseAndBranchIdAndDeletedIsFalseAndIdNot(
                                categoryDTO.getName(),
                                branchId,
                                categoryDTO.getId()
                        )
        ) {
            throw conflictExceptionThrow("Category exists by this name :" + categoryDTO.getName()).get();
        }
    }

    @Transactional
    public Long create(CategoryDTO categoryDTO) {
        this.validate(categoryDTO);
        Category category = null;
        if (categoryDTO.getUniqueId() != null) {
            category = categoryRepository.findFirstByUniqueIdAndDeletedIsFalseAndBranchId(categoryDTO.getUniqueId(), categoryDTO.getBranchId()).orElse(null);
        }
        if (category == null) {
            category = new Category();
        }
        category.setUniqueId(Optional.ofNullable(categoryDTO.getUniqueId()).orElse(UUID.randomUUID()));
        category.setName(categoryDTO.getName());
        if (categoryDTO.getCatalog() != null) {
            category.setCatalogCode(categoryDTO.getCatalog().getCode());
            category.setCatalogName(categoryDTO.getCatalog().getName());
            category.setBarcode(categoryDTO.getCatalog().getBarcode());
        }
        category.setPackageCode(categoryDTO.getPackageCode());
        category.setPackageName(categoryDTO.getPackageName());
        category.setStatus(CommonStatus.ACTIVE);
        category.setCategoryType(categoryDTO.getType());
        category.setBranchId(getCurrentBranchId(categoryDTO.getBranchId()));
        category.setParentId(categoryDTO.getParentId());
        category.setOrigin(categoryDTO.getOrigin());
        category.setVat(categoryDTO.getVat());
        categoryRepository.saveAndFlush(category);
        return category.getId();
    }

    public CategoryDetailDTO get(Long id) {
        return categoryRepository
                .findById(id)
                .map(category -> {
                    CategoryDetailDTO categoryDetailDTO = new CategoryDetailDTO();
                    categoryDetailDTO.setId(category.getId());
                    categoryDetailDTO.setName(category.getName());
                    if (StringUtils.isNotEmpty(category.getCatalogCode())) {
                        categoryDetailDTO.setCatalog(category.toCatalogDTO());
                    }
                    categoryDetailDTO.setStatus(category.getStatus());
                    categoryDetailDTO.setType(category.getCategoryType());
                    categoryDetailDTO.setBranch(category.getBranch().toCommonDTO());
                    if (category.getParent() != null) {
                        categoryDetailDTO.setParent(category.getParent().toCommonDTO());
                    }
                    categoryDetailDTO.setPackageCode(category.getPackageCode());
                    categoryDetailDTO.setPackageName(category.getPackageName());
                    categoryDetailDTO.setVat(category.getVat());
                    categoryDetailDTO.setOrigin(category.getOrigin());
                    return categoryDetailDTO;
                })
                .orElseThrow(notFoundExceptionThrow(Category.class.getSimpleName(), "id", id));
    }

    public Page<CategoryListDTO> getList(BaseFilter filter) {
        ResultList<Category> resultList = categoryRepository.getResultList(filter);
        List<CategoryListDTO> result = resultList
                .getList()
                .stream()
                .map(category -> {
                    CategoryListDTO categoryListDTO = new CategoryListDTO();
                    categoryListDTO.setId(category.getId());
                    categoryListDTO.setName(category.getName());
                    if (StringUtils.isNotEmpty(category.getCatalogCode())) {
                        categoryListDTO.setCatalog(category.toCatalogDTO());
                    }
                    categoryListDTO.setPackageCode(category.getPackageCode());
                    categoryListDTO.setPackageName(category.getPackageName());
                    categoryListDTO.setStatus(category.getStatus());
                    categoryListDTO.setType(category.getCategoryType());
                    categoryListDTO.setBranch(category.getBranch().toCommonDTO());
                    if (category.getParent() != null) {
                        categoryListDTO.setParent(category.getParent().toCommonDTO());
                    }
                    return categoryListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getSortedPageable(), resultList.getCount());
    }

    @Transactional
    public Long update(Long id, CategoryDTO categoryDTO) {
        return categoryRepository
                .findById(id)
                .map(category -> {
                    categoryDTO.setId(id);
                    this.validate(categoryDTO);
                    category.setName(categoryDTO.getName());
                    category.setPackageCode(categoryDTO.getPackageCode());
                    category.setPackageName(categoryDTO.getPackageName());
                    category.setOrigin(categoryDTO.getOrigin());
                    category.setVat(categoryDTO.getVat());
                    if (categoryDTO.getCatalog() != null) {
                        category.setCatalogCode(categoryDTO.getCatalog().getCode());
                        category.setCatalogName(categoryDTO.getCatalog().getName());
                        category.setBarcode(categoryDTO.getCatalog().getBarcode());
                    }
                    category.setBranchId(getCurrentBranchId(categoryDTO.getBranchId()));
                    category.setParentId(categoryDTO.getParentId());
                    categoryRepository.save(category);
                    return id;
                })
                .orElseThrow(notFoundExceptionThrow(Category.class.getSimpleName(), "id", id));
    }

    @Transactional
    public void updateStatus(Long id, CommonStatus status) {
        categoryRepository
                .findById(id)
                .map(category -> {
                    category.setStatus(status);
                    categoryRepository.save(category);
                    return category;
                })
                .orElseThrow(notFoundExceptionThrow(Category.class.getSimpleName(), "id", id));
    }

    public List<CategoryLookUpDTO> lookUp(BaseFilter filter) {
        return categoryRepository.getResultList(filter).getList().stream().map(Category::toLookUpDTO).collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        categoryRepository.findById(id).map(category -> {
            if (productRepository.existsByCategoryIdAndDeletedIsFalse(id)) {
                throw badRequestExceptionThrow(localizationService.localize("you.can.not.delete.category")).get();
            }
            if (!CollectionUtils.isEmpty(category.getChildren())) {
                throw badRequestExceptionThrow(localizationService.localize("you.can.not.delete.category")).get();
            }
            categoryRepository.deleteById(id);
            return id;
        }).orElseThrow(notFoundExceptionThrow(Category.class.getSimpleName(), "id", id));
    }

    public List<CategoryTreeDTO> treeList(BaseFilter filter) {
        filter.setParentId(null);
        Map<Long, CategoryTreeDTO> map = categoryRepository
                .getResultList(filter)
                .getList()
                .stream()
                .collect(Collectors.toMap(Category::getId, Category::toTreeDTO, (a, b) -> b, LinkedHashMap::new));
        List<CategoryTreeDTO> result = new ArrayList<>();
        map
                .values()
                .forEach(category -> {
                    CategoryTreeDTO parentCategory = category.getParentId() == null ? null : map.get(category.getParentId());
                    if (parentCategory == null) {
                        result.add(category);
                        return;
                    }
                    parentCategory.getChildren().add(category);
                });
        return result;
    }

    @Transactional
    public void syncTasnifCateringService() {
        Category category = categoryRepository.findFirstByCategoryTypeAndDeletedIsFalse(CategoryType.CATERING_SERVICE).orElseThrow(notFoundExceptionThrow(Category.class.getSimpleName()));
        TasnifMxikHistoryResponseListDTO tasnifMxikHistoryResponseListDTO = tasnifClient.getMxikHistory(category.getCatalogCode());
        if (!CollectionUtils.isEmpty(tasnifMxikHistoryResponseListDTO.getData())) {
            MxikHistoryDTO mxikHistoryDTO = tasnifMxikHistoryResponseListDTO.getData().get(0);
            if (!category.getCatalogCode().equals(mxikHistoryDTO.getMxikCode())) {
                category.setCatalogCode(mxikHistoryDTO.getMxikCode());
                category.setCatalogName(mxikHistoryDTO.getName());
                if (!CollectionUtils.isEmpty(mxikHistoryDTO.getPackageNames())) {
                    category.setPackageCode(mxikHistoryDTO.getPackageNames().get(0).getCode());
                    category.setPackageName(mxikHistoryDTO.getPackageNames().get(0).getName());
                }
                categoryRepository.save(category);

                Product product = productRepository.findFirstByCategoryIdAndDeletedIsFalse(category.getId()).orElseThrow(notFoundExceptionThrow(Product.class.getSimpleName()));
                product.setCatalogCode(mxikHistoryDTO.getMxikCode());
                product.setCatalogName(mxikHistoryDTO.getName());
                product.setPackageCode(mxikHistoryDTO.getName());
                if (!CollectionUtils.isEmpty(mxikHistoryDTO.getPackageNames())) {
                    product.setPackageCode(mxikHistoryDTO.getPackageNames().get(0).getCode());
                    product.setPackageName(mxikHistoryDTO.getPackageNames().get(0).getName());
                }
                productRepository.save(product);
            }
        }
    }

    public List<TasnifPackageDTO> getPackages(String catalogCode, String tin) {
        Map<Integer, TasnifPackageDTO> packages = new HashMap<>();
        for (TasnifPackageDTO tasnifPackageDTO : tasnifClient.getPackagesByCatalogCode(catalogCode)) {
            packages.put(tasnifPackageDTO.getCode(), tasnifPackageDTO);
        }
        for (MxikDTO mxikDTO : tasnifClient.getCompanyCatalogByProduct(tin, catalogCode)) {
            for (PackageDTO packageDTO : mxikDTO.getPackageNames()) {
                packages.putIfAbsent(Integer.valueOf(packageDTO.getCode()), new TasnifPackageDTO(packageDTO.getNameRu(), Integer.valueOf(packageDTO.getCode()), packageDTO.getNameUz()));
            }
        }
        return new ArrayList<>(packages.values());
    }

    @Transactional
    public void transferCategory(TransferDTO transferDTO) {
        Long branchId = transferDTO.getFromBranchId() != null ? transferDTO.getFromBranchId() : getCurrentBranchId();
        if (branchId == null) {
            throw badRequestExceptionThrow("Branch id is required").get();
        }
        if (CollectionUtils.isEmpty(transferDTO.getToBranchIds())) {
            transferDTO.setToBranchIds(branchRepository.findAllByCompanyIdAndDeletedIsFalseAndIdNot(getCurrentCompanyId(), branchId).stream().map(SimpleEntity::getId).collect(Collectors.toList()));
        }
        for (Long toBranchId : transferDTO.getToBranchIds()) {
            departmentService.syncDepartment(branchId,toBranchId);
            this.syncCategory(branchId, toBranchId);
            productService.syncProduct(branchId,toBranchId);
        }
    }

    @Transactional
    public void syncCategory(Long fromBranchId, Long toBranchId) {
        List<Category> categories = categoryRepository.findAllByBranchIdAndDeletedIsFalseAndParentIdIsNull(fromBranchId);
        if (!CollectionUtils.isEmpty(categories)) {
            for (Category category : categories) {
                this.recursion(category, toBranchId, null);
            }
        }
    }

    @Transactional
    public void recursion(Category category, Long toBranchId, Long parentId) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setBranchId(toBranchId);
        categoryDTO.setName(category.getName());
        categoryDTO.setType(category.getCategoryType());
        categoryDTO.setPackageCode(category.getPackageCode());
        categoryDTO.setPackageName(category.getPackageName());
        CatalogDTO catalog = new CatalogDTO();
        catalog.setCode(category.getCatalogCode());
        catalog.setName(category.getCatalogName());
        catalog.setBarcode(category.getBarcode());
        categoryDTO.setCatalog(catalog);
        categoryDTO.setParentId(parentId);
        categoryDTO.setUniqueId(category.getUniqueId());
        Long parentCategoryId = null;
        try {
            parentCategoryId = this.create(categoryDTO);
        } catch (ConflictException e) {
            Optional<Category> parentCategory = categoryRepository.findFirstByNameIgnoreCaseAndBranchIdAndDeletedIsFalse(category.getName(), toBranchId);
            if (parentCategory.isPresent()) {
                parentCategoryId = parentCategory.get().getId();
                Category toCategory=parentCategory.get();
                toCategory.setUniqueId(category.getUniqueId());
                categoryRepository.save(toCategory);
            }
        } catch (Exception e) {
            log.info("Category error: {}", e.getMessage());
        }
        for (Category childCategory : category.getChildren()) {
            this.recursion(childCategory, toBranchId, parentCategoryId);
        }
    }
}
