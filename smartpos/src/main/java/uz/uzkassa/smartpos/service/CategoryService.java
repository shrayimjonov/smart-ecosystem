package uz.uzkassa.smartpos.service;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.constants.Constants;
import uz.uzkassa.smartpos.domain.postgres.catalog.Category;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranch;
import uz.uzkassa.smartpos.domain.postgres.catalog.CategoryBranchId;
import uz.uzkassa.smartpos.domain.postgres.catalog.ProductBranch;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.dto.TransferCatalogDTO;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.dto.catalog.*;
import uz.uzkassa.smartpos.dto.filter.BaseFilter;
import uz.uzkassa.smartpos.dto.mq.CategoryDeletePayload;
import uz.uzkassa.smartpos.enums.CompanyType;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.rabbitmq.MqConstants;
import uz.uzkassa.smartpos.rabbitmq.producer.RabbitMqProducer;
import uz.uzkassa.smartpos.repository.postgres.catalog.*;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService extends SmartposService {
    private final ProductBranchRepository productBranchRepository;
    private final CategoryBranchRepository categoryBranchRepository;
    private final CategoryRepository categoryRepository;
    private final ProductService productService;
    private final RabbitMqProducer rabbitMqProducer;
    private final ProductColorGroupRepository productColorGroupRepository;
    private final ProductItemRepository productItemRepository;

    @Transactional
    public CategoryDTO createNew(final CategoryDTO categoryDTO) {
        final Long branchId = categoryDTO.getBranchId() != null ? categoryDTO.getBranchId() : getCurrentBranchId();
        CategoryBranch category = new CategoryBranch();
        category.setCategoryId(categoryBranchRepository.nextCategoryId());
        category.setBranchId(branchId);
        category.setName(categoryDTO.getName());
        category.setParentId(categoryDTO.getParentId());
        categoryBranchRepository.save(category);
        categoryDTO.setId(category.getCategoryId());
        return categoryDTO;
    }

    @Transactional
    public CategoryDTO update(CategoryDTO categoryDTO) throws EntityNotFound {
        CategoryBranch category = categoryBranchRepository.findById(new CategoryBranchId(categoryDTO.getId(), categoryDTO.getBranchId()))
                .orElseThrow(notFound(localizationService.getMessage("category"), categoryDTO.getId()));
        category.setName(categoryDTO.getName());
        category.setParentId(categoryDTO.getParentId());
        categoryBranchRepository.save(category);
        return categoryDTO;
    }

    @Transactional
    public void transferCatalog(TransferCatalogDTO dto) {
        Thread.startVirtualThread(() -> {
            Long fromCityId = getBranchCityId(dto.getFromBranchId());
            List<CategoryBranch> categoryBranches = categoryBranchRepository.findAllByBranchIdAndEnabledIsTrue(dto.getFromBranchId());
            dto.getToBranchIds().parallelStream().forEach(toBranchId -> {
                Long toCityId = getBranchCityId(toBranchId);
                List<CategoryBranch> toCategoryBranches = categoryBranches.stream()
                        .map(categoryBranch -> new CategoryBranch(categoryBranch, toBranchId))
                        .collect(Collectors.toList());
                categoryBranchRepository.saveAll(toCategoryBranches);
                categoryBranches.forEach(categoryBranch -> transferProducts(dto, toBranchId, categoryBranch, fromCityId, toCityId));
                rabbitMqProducer.sendWithDelayTime(new TransferCatalogDTO(dto.getFromBranchId(), toBranchId), MqConstants.PRODUCT_ITEMS_TRANSFER_QUEUE, 5000L);
            });
        });
    }

    private void transferProducts(TransferCatalogDTO dto, Long toBranchId, CategoryBranch categoryBranch, Long fromCityId, Long toCityId) {
        int page = 0;
        Page<ProductBranch> fromProductBranchesPage = productBranchRepository.findAllByBranchIdAndCityIdAndCategoryIdAndDeletedIsFalseOrderByProductId(dto.getFromBranchId(), fromCityId, categoryBranch.getCategoryId(), PageRequest.of(page++, 500));
        while (!fromProductBranchesPage.isEmpty()) {
            Page<ProductBranch> toProductBranches = fromProductBranchesPage.map(productBranch -> new ProductBranch(productBranch, toBranchId, toCityId));
            productBranchRepository.saveAll(toProductBranches);
            fromProductBranchesPage = productBranchRepository.findAllByBranchIdAndCityIdAndCategoryIdAndDeletedIsFalseOrderByProductId(dto.getFromBranchId(), fromCityId, categoryBranch.getCategoryId(), PageRequest.of(page++, 500));
        }
    }

    @Transactional
    public void delete(Long categoryId, final Long branchId) {
        final String login = SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT);
        rabbitMqProducer.send(new CategoryDeletePayload(login, categoryId, branchId), MqConstants.CATEGORY_DELETE_QUEUE);
    }

    @Transactional
    public void delete(CategoryDeletePayload payload) {
        final CategoryBranch categoryBranch = categoryBranchRepository.findById(new CategoryBranchId(payload.categoryId(), payload.branchId()))
                .orElseThrow(notFound(localizationService.getMessage("category")));
        deleteRecursive(Lists.newArrayList(categoryBranch), payload.branchId(), getBranchCityId(payload.branchId()), payload.login());
    }

    @Transactional
    public void deleteRecursive(List<CategoryBranch> categories, Long branchId, Long cityId, String login) {
        if (CollectionUtils.isEmpty(categories)) return;
        List<Long> categoryIds = categories.stream().map(CategoryBranch::getCategoryId).collect(Collectors.toList());
        List<Long> productIds = productBranchRepository.findAllByBranchIdAndCityIdAndCategoryIdInAndDeletedIsFalse(branchId, cityId, categoryIds);
        productBranchRepository.deleteByBranchIdAndCityIdAndCategoryIdIn(login, branchId, cityId, categoryIds);
        Lists.partition(productIds, 1000).forEach(ids -> {
            productColorGroupRepository.deleteByBranchIdAndCityIdAndProductIdIn(branchId, cityId, ids);
            productItemRepository.deleteByAndBranchIdAndCityIdAndProductIdIn(branchId, cityId, ids);
            productService.deleteAllProductBranchReferencesByIds(cityId, branchId, ids);
        });
        categoryBranchRepository.disableCategoryBranches(login, branchId, categoryIds);
        categories.forEach(category -> deleteRecursive(new ArrayList<>(category.getChildCategories()), branchId, cityId, login));
    }

    public List<CategoryDTO> getEnabledCategories(Long branchId) {
        final Long currentBranchId = branchId != null ? branchId : getCurrentBranchId();
        final Long cityId = getBranchCityId(currentBranchId);
        List<CategoryDTO> enabledCategories = categoryBranchRepository.findEnabledCategories(currentBranchId, cityId);
        return convertToTree(enabledCategories);
    }

    /*
        Конвертация категорий в древовидную форму
    */
    private List<CategoryDTO> convertToTree(List<CategoryDTO> categories) {
        Map<Long, CategoryDTO> map = new LinkedHashMap<>();
        List<CategoryDTO> result = new ArrayList<>();

        for (CategoryDTO category : categories) {
            map.put(category.getId(), category);
        }

        categories.forEach(category -> {
            CategoryDTO parentCategory = map.get(category.getParentId());
            if (parentCategory == null) {
                result.add(category);
                return;
            }
            parentCategory.getChildren().add(category);
        });

        result.forEach(category -> sumProductCount(category, category.getChildren()));
        return result;
    }

    /*
        Вычисление количества товаров в данной категории вкючая всех подкатегорий
     */
    private CategoryProductCountPayload sumProductCount(CategoryDTO category, List<CategoryDTO> children) {
        Long count = category.getProductCount();
        Long creditCount = category.getCreditCount();
        Long advanceCount = category.getAdvanceCount();
        for (CategoryDTO child : children) {
            CategoryProductCountPayload payload = sumProductCount(child, child.getChildren());
            count += payload.getCount();
            creditCount += payload.getCreditCount();
            advanceCount += payload.getAdvanceCount();
        }

        category.setProductCount(count);
        category.setCreditCount(creditCount);
        category.setAdvanceCount(advanceCount);
        return new CategoryProductCountPayload(count, creditCount, advanceCount);
    }

    public List<CommonDTO> choose(BaseFilter filter) {
        if (!isAdmin() || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (filter.getBranchId() == null && !isOwner()) {
            filter.setBranchId(getCurrentBranchId());
        }
        return categoryBranchRepository.choose(filter);
    }

    public boolean isCategoryChanged(LocalDateTime lastSyncDate) {
        final Long branchId = getCurrentBranchId();
        return categoryBranchRepository.existsByBranchIdAndLastModifiedDateAfter(branchId, lastSyncDate);
    }

    @Transactional
    public Long create(CategoryCreateDto dto) {
        CategoryBranch category = new CategoryBranch();
        category.setCategoryId(categoryBranchRepository.nextCategoryId());
        category.setBranchId(dto.branchId());
        category.setName(dto.name());
        category.setParentId(dto.parentId());
        categoryBranchRepository.save(category);
        return category.getCategoryId();
    }

    @Transactional
    public void update(Long id, CategoryCreateDto dto) {
        CategoryBranch categoryBranch = categoryBranchRepository.findById(new CategoryBranchId(id, dto.branchId()))
                .stream().filter(CategoryBranch::isEnabled)
                .findAny()
                .orElseThrow(notFound(localizationService.getMessage("category")));
        categoryBranch.setName(dto.name());
        categoryBranch.setParentId(dto.parentId());
        categoryBranchRepository.save(categoryBranch);
    }

    public List<CategoryListDto> findAll(Long branchId) {
        final Long id;
        if (branchId != null) {
            id = branchId;
        } else {
            id = getCurrentBranchId();
        }
        List<CategoryResult> all = categoryBranchRepository.findAll(id, getBranchCityId(id));
        if (CollectionUtils.isEmpty(all)) {
            return List.of();
        } else {
            return convertListToTree(all);
        }
    }

    public List<CategoryListDto> convertListToTree(List<CategoryResult> categories) {
        Map<Long, CategoryListDto> map = categories.stream().collect(Collectors.toMap(CategoryResult::id, CategoryListDto::new));
        List<CategoryListDto> result = new ArrayList<>();
        categories.forEach(categoryResult -> {
            CategoryListDto category = map.get(categoryResult.id());
            if (categoryResult.parentId() == null || !map.containsKey(categoryResult.parentId())) {
                result.add(category);
            } else {
                map.get(categoryResult.parentId()).addChildren(category);
            }
        });
        return result.stream().peek(this::calculate).toList();
    }

    private CategoryListDto calculate(CategoryListDto dto) {
        if (CollectionUtils.isEmpty(dto.getChildren())) {
            return dto;
        }
        CategoryListDto result = dto.getChildren().stream().map(this::calculate).reduce(this::merge).orElseGet(CategoryListDto::new);
        dto.setProductCount(dto.getProductCount() + result.getProductCount());
        dto.setAdvanceCount(dto.getAdvanceCount() + result.getAdvanceCount());
        dto.setCreditCount(dto.getCreditCount() + result.getCreditCount());
        return dto;
    }

    private CategoryListDto merge(CategoryListDto dto, CategoryListDto dto1) {
        CategoryListDto result = new CategoryListDto();
        result.setProductCount(dto.getProductCount() + dto1.getProductCount());
        result.setAdvanceCount(dto.getAdvanceCount() + dto1.getAdvanceCount());
        result.setCreditCount(dto.getCreditCount() + dto1.getCreditCount());
        return result;
    }

    public List<CategoryTreeDto> findAllWithoutProductsCount(Long branchId) {
        List<CategoryTreeDto> all = categoryBranchRepository.findAllWithoutProductsCount(branchId);
        if (CollectionUtils.isEmpty(all)) {
            return List.of();
        }
        Map<Long, CategoryTreeDto> map = all.stream().collect(Collectors.toMap(CategoryTreeDto::getId, Function.identity()));
        List<CategoryTreeDto> result = new ArrayList<>();
        all.forEach(categoryResult -> {
            CategoryTreeDto category = map.get(categoryResult.getId());
            if (categoryResult.getParentId() == null || !map.containsKey(categoryResult.getParentId())) {
                result.add(category);
            } else {
                map.get(categoryResult.getParentId()).addChildren(category);
            }
        });
        return result;
    }

    @Transactional
    public CategoryDTO createNewCategory(final CategoryDTO categoryDTO) {
        final Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setNameUz(categoryDTO.getNameUz());
        category.setParentId(categoryDTO.getParentId());
        category.setCompanyType(categoryDTO.getCompanyType());
        category.setMxikCode(categoryDTO.getMxikCode());
        categoryRepository.save(category);
        categoryDTO.setId(category.getId());
        return categoryDTO;
    }

    @Transactional
    public CategoryDTO updateCategory(final CategoryDTO categoryDTO) throws EntityNotFound {
        final Category category = categoryRepository.findById(categoryDTO.getId())
                .orElseThrow(notFound(localizationService.getMessage("category"), categoryDTO.getId()));
        category.setName(categoryDTO.getName());
        category.setNameUz(categoryDTO.getNameUz());
        category.setMxikCode(categoryDTO.getMxikCode());
        categoryRepository.save(category);
        return categoryDTO;
    }

    @Transactional
    public void deleteCategory(final Long categoryId) throws EntityNotFound {
        categoryRepository.deleteById(categoryId);
    }

    public List<CategoryDTO> getCategories(final CompanyType companyType) {
        final List<CategoryDTO> categories = categoryRepository.findAllByCompanyType(companyType)
                .stream()
                .map(Category::getDTO)
                .toList();
        final Map<Long, CategoryDTO> map = new LinkedHashMap<>();
        final List<CategoryDTO> result = new ArrayList<>();

        for (final CategoryDTO category : categories) {
            map.put(category.getId(), category);
        }

        categories.forEach(category -> {
            final CategoryDTO parentCategory = map.get(category.getParentId());
            if (parentCategory == null) {
                result.add(category);
                return;
            }
            parentCategory.getChildren().add(category);
        });
        return result;
    }

    @Transactional
    public void createFashionCatalog(final Long branchId) {
        categoryBranchRepository.createFashionCatalog(branchId);
    }
}
