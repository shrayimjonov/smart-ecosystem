package uz.uzkassa.smartpos.service;

import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpos.domain.postgres.company.Company;
import uz.uzkassa.smartpos.dto.CategoryDTO;
import uz.uzkassa.smartpos.dto.analytics.*;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.ServiceType;
import uz.uzkassa.smartpos.enums.SortTypeEnum;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptSalesStatsRepository;
import uz.uzkassa.smartpos.repository.clickhouse.receiptdetail.ClickhouseReceiptDetailRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.CategoryBranchRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService extends SmartposService {
    private final ClickhouseReceiptDetailRepository clickhouseReceiptDetailRepository;
    private final ClickhouseReceiptSalesStatsRepository clickhouseReceiptSalesStatsRepository;
    private final UserRepository userRepository;
    private final CategoryBranchRepository categoryBranchRepository;

    public List<OverallSalesStatsDTO> getSalesDynamic(SalesDynamicsFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            reportServiceCheck(filter);
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return clickhouseReceiptSalesStatsRepository.getSalesDynamic(filter);
    }

    public Page<SalesStatsDTO> getProductSalesDynamic(SalesDynamicsFilter filter) {
        if (filter.getFrom() == null || filter.getTo() == null || filter.getProductId() == null || filter.getGranularity() == null) {
            throw badRequest(localizationService.getMessage("specify")).get();
        }
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            reportServiceCheck(filter);
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return clickhouseReceiptDetailRepository.getProductSalesDynamics(filter);
    }

    public OverallSalesStatsDTO getSalesStats(SalesStatsFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return clickhouseReceiptSalesStatsRepository.getSalesStats(filter);
    }

    public EmployeeSalesStatDTO getEmployeeSalesStat(SalesStatsFilter filter) {
        if (filter.getUserId() == null) {
            throw badRequest(localizationService.getMessage("user.id.required")).get();
        }
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            reportServiceCheck(filter);
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        EmployeeSalesStatDTO employeeSalesStatDTO = clickhouseReceiptSalesStatsRepository.getEmployeeSalesStat(filter);
        if (employeeSalesStatDTO == null) {
            return new EmployeeSalesStatDTO();
        }
        employeeSalesStatDTO.setFirstDayAtWork(userRepository.getReferenceById(filter.getUserId()).getCreatedDate());

        return employeeSalesStatDTO;
    }

    public List<CategoryDTO> getCategorySalesStats(SalesStatsFilter filter) { //Todo optimize
        if ("id".equals(filter.getOrderBy())) {
            filter.setOrderBy(null);
        }
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            reportServiceCheck(filter);
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        if (CollectionUtils.size(filter.getBranchIdsWithBranchId()) != 1) {
            throw new BadRequestException("Выберите только один филиал, чтобы получить отчет по категориям");
        }
        filter.setBranchId(filter.getBranchIdsWithBranchId().iterator().next());
        Map<Long, CategorySalesStats> categorySalesStats = clickhouseReceiptDetailRepository.getCategorySalesStats(filter)
                .stream().collect(Collectors.toMap(SalesStatsDTO::getId, CategorySalesStats::new));
        filter.setCityId(getBranchCityId(filter.getBranchId()));
        List<CategoryDTO> enabledCategories = categoryBranchRepository.findEnabledCategories(filter.getBranchId(), filter.getCityId());
        List<CategoryDTO> result = convertToTree(enabledCategories, categorySalesStats);
        result.forEach(category -> sumAggregate(category, category.getChildren()));
        if (StringUtils.isNotBlank(filter.getOrderBy())) {
            Comparator<CategoryDTO> comparator = getCategoryComparator(filter);
            sortRecursive(result, comparator);
        }
        return result;
    }

    @NotNull
    private static Comparator<CategoryDTO> getCategoryComparator(SalesStatsFilter filter) {
        int asc = SortTypeEnum.asc.equals(filter.getSortOrder()) ? 1 : -1;
        return (dto, categoryDTO1) -> switch (filter.getOrderBy()) {
            case "salesCount" ->
                    dto.getSalesStats().getSalesCount().compareTo(categoryDTO1.getSalesStats().getSalesCount());
            case "salesTotal" ->
                    dto.getSalesStats().getSalesTotal().compareTo(categoryDTO1.getSalesStats().getSalesTotal());
            case "nds" -> dto.getSalesStats().getNds().compareTo(categoryDTO1.getSalesStats().getNds());
            case "discount" -> dto.getSalesStats().getDiscount().compareTo(categoryDTO1.getSalesStats().getDiscount());
            case "receiptCount" ->
                    dto.getSalesStats().getReceiptCount().compareTo(categoryDTO1.getSalesStats().getReceiptCount());
            default -> 0;
        } * asc;
    }

    public CategorySalesStats getSalesStatCategoryStat(SalesStatsFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            reportServiceCheck(filter);
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        if (CollectionUtils.size(filter.getBranchIdsWithBranchId()) != 1) {
            throw new BadRequestException("Выберите только один филиал, чтобы получить отчет по категориям");
        }
        List<CategoryDTO> list = getCategorySalesStats(filter);
        CategorySalesStats result = new CategorySalesStats();
        list.forEach(item -> result.combine(item.getSalesStats()));
        return result;
    }


    private List<CategoryDTO> convertToTree(List<CategoryDTO> enabledCategories, Map<Long, CategorySalesStats> categorySalesStats) {
        List<CategoryDTO> result = new ArrayList<>();

        Map<Long, CategoryDTO> map = new LinkedHashMap<>();

        for (CategoryDTO category : enabledCategories) {
            category.setSalesStats(categorySalesStats.getOrDefault(category.getId(), new CategorySalesStats()));
            map.put(category.getId(), category);
        }

        enabledCategories.forEach(category -> {
            CategoryDTO parentCategory = map.get(category.getParentId());
            if (parentCategory == null) {
                result.add(category);
                return;
            }
            parentCategory.getChildren().add(category);
        });
        return result;
    }

    private CategorySalesStats sumAggregate(CategoryDTO category, List<CategoryDTO> children) {
        CategorySalesStats sum = category.getSalesStats();
        for (CategoryDTO child : children) {
            sum.combine(sumAggregate(child, child.getChildren()));
        }
        category.setSalesStats(sum);
        return sum;
    }

    private void sortRecursive(List<CategoryDTO> categories, Comparator<CategoryDTO> comparator) {
        categories.sort(comparator);
        categories.forEach(categoryDTO -> categoryDTO.getChildren().sort(comparator));
    }

    public Page<SalesStatsDTO> getProductSalesStats(SalesStatsFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            reportServiceCheck(filter);
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return clickhouseReceiptDetailRepository.getProductSalesStatsByBranch(filter, false);
    }

    public SalesStatsProductsHeader getProductSalesStatsHeader(SalesStatsFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            reportServiceCheck(filter);
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return clickhouseReceiptDetailRepository.getProductSalesStatsHeader(filter);
    }

    public Page<UserSalesStatsDTO> getUserSalesStats(SalesStatsFilter filter) {
        final boolean hasPermissionToAdmin = hasPermissionToAdmin();
        if (!hasPermissionToAdmin || filter.getCompanyId() == null) {
            filter.setCompanyId(getCurrentCompanyId());
        }
        if (!hasPermissionToAdmin) {
            reportServiceCheck(filter);
            if (!isOwner() && !isBranchAdmin()) {
                filter.setBranchIds(Sets.newHashSet(getCurrentBranchId()));
            }
            if (isBranchAdmin() && CollectionUtils.isEmpty(filter.getBranchIdsWithBranchId())) {
                SecurityUtils.getCurrentBranchGroupId()
                        .ifPresentOrElse(groupId -> filter.setBranchGroupIds(Set.of(groupId)),
                                () -> filter.setBranchIds(Set.of(SecurityUtils.getCurrentBranchId()))
                        );
            }
        }
        filter.setBranchIds(getBranchesByGroup(filter.getBranchGroupIds(), filter.getBranchIdsWithBranchId()));
        return clickhouseReceiptSalesStatsRepository.getUserSalesStats(filter, false);
    }

    public void reportServiceCheck(SalesStatsFilter filter) {
        Company company = companyRepository.findById(filter.getCompanyId()).orElseThrow(notFound(localizationService.getMessage("company")));
        if (!company.isFreeTrial() && !company.getPermissions().getOrDefault(ServiceType.REPORT, LocalDateTime.MIN).isAfter(LocalDateTime.now()) && (filter.getFrom() == null || filter.getFrom().isBefore(LocalDateTime.now().minusDays(1)))) {
            throw new BadRequestException("Срок подписки истек! Чтобы продолжить работу приобретите подписку!", 400);
        }
    }

}
