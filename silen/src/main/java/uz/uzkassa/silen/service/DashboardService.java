package uz.uzkassa.silen.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.silen.domain.DashboardStats;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.Product;
import uz.uzkassa.silen.dto.TopSelectItem;
import uz.uzkassa.silen.dto.dashboard.*;
import uz.uzkassa.silen.dto.filter.Filter;
import uz.uzkassa.silen.dto.filter.OrganizationFilter;
import uz.uzkassa.silen.dto.filter.ProductFilter;
import uz.uzkassa.silen.dto.filter.TopFilter;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;
import uz.uzkassa.silen.enumeration.ProductType;
import uz.uzkassa.silen.repository.pg.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {
    private final OrganizationRepository organizationRepository;
    private final ProductRepository productRepository;
    private final PartyRepository partyRepository;
    private final DropoutRepository dropoutRepository;
    private final StoreInRepository storeInRepository;
    private final AggregationRepository aggregationRepository;
    private final ShipmentRepository shipmentRepository;
    private final DashboardStatsRepository dashboardStatsRepository;

    public List<TopSelectItem> topErrors(Filter filter) {
        return aggregationRepository.topErrors(filter);
    }

    public List<TopSelectItem> topOrders(Filter filter) {
        return partyRepository.topOrders(filter);
    }

    public List<TopSelectItem> topDropout(Filter filter) {
        return dropoutRepository.topDropout(filter);
    }

    public List<TopSelectItem> topStoreIn(Filter filter) {
        return storeInRepository.topStoreIn(filter);
    }

    public List<OrganizationsDTO> getOrganizations() {
        return organizationRepository.findOrganizations();
    }


    public List<DashboardStatsDTO> findAllByFilter(TopFilter filter) {
        filter.setSize(9);
        return dashboardStatsRepository.findAllByFilter(filter);
    }

//    @Scheduled(cron = "0 0 2 * * *")
    public void fillTopShipped() {
        TopFilter filter = new TopFilter();
        filter.setFrom(LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0));
        filter.setTo(LocalDateTime.now().minusHours(2));
        filter.setSize(500);
        boolean isLast = false;
        int page = 0;
        do {
            filter.setPage(page++);
            List<DashboardStatsDTO> shippedByProductAndUnit = shipmentRepository.getShippedByProductAndUnit(filter);

            if (CollectionUtils.isEmpty(shippedByProductAndUnit)) {
                isLast = true;
            } else {
                for (DashboardStatsDTO dashboardStatsDTO : shippedByProductAndUnit) {
                    DashboardStats dashboardStats = new DashboardStats();
                    dashboardStats.setOrganizationId(dashboardStatsDTO.getOrganizationId());
                    dashboardStats.setProductId(dashboardStatsDTO.getProductId());
                    dashboardStats.setUnit(dashboardStatsDTO.getUnit().getCode());
                    dashboardStats.setQuantity(dashboardStatsDTO.getQuantity());
                    dashboardStats.setType(DailyOperationTypes.Shipment);
                    dashboardStatsRepository.save(dashboardStats);
                }
            }
        } while (isLast);
    }

//    @Scheduled(cron = "0 0 2 * * *")
    public void dashboardStatsProduction() {
        TopFilter filter = new TopFilter();
        filter.setFrom(LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0));
        filter.setTo(LocalDateTime.now().minusHours(2));
        filter.setSize(500);
        boolean isLast = false;
        int page = 0;
        do {
            filter.setPage(page++);
            List<DashboardStatsDTO> dashboardStatsDTOS = aggregationRepository.dashboardStats(filter);
            if (CollectionUtils.isEmpty(dashboardStatsDTOS)) {
                isLast = true;
            } else {
                for (DashboardStatsDTO dashboardStatsDTO : dashboardStatsDTOS) {
                    DashboardStats dashboardStats = new DashboardStats();
                    dashboardStats.setOrganizationId(dashboardStatsDTO.getOrganizationId());
                    dashboardStats.setProductId(dashboardStatsDTO.getProductId());
                    dashboardStats.setUnit(dashboardStatsDTO.getUnit().getCode());
                    dashboardStats.setQuantity(dashboardStatsDTO.getQuantity());
                    dashboardStats.setType(DailyOperationTypes.Production);
                    dashboardStatsRepository.save(dashboardStats);
                }
            }
        } while (isLast);
    }

    public List<StatsV2DTO> getStatsTemp(DashboardFilter filter) {
        Set<ProductType> productTypes = ProductType.byOrganizationTypes(filter.getOrganizationTypes());
        List<StatsV2DTO> result = new ArrayList<>();
        for (ProductType type : productTypes) {
            result.add(new StatsV2DTO(type));
        }
        return result;
    }

    public List<OrganizationStatsDTO> getDataTemp(OrganizationFilter filter) {
        Set<ProductType> productTypes = ProductType.byOrganizationTypes(filter.getOrganizationTypes());
        List<StatsV2DTO> stats = new ArrayList<>();
        for (ProductType type : productTypes) {
            stats.add(new StatsV2DTO(type));
        }

        List<Organization> organizations = organizationRepository.findAllByFilter(filter).getContent();
        List<OrganizationStatsDTO> result = new ArrayList<>();
        for (Organization organization : organizations) {
            result.add(new OrganizationStatsDTO(organization.getId(), organization.getName(), stats));
        }

        return result;
    }

    public List<OrganizationStatsDTO> getDataFromWarehouse(OrganizationFilter filter) {
        Set<ProductType> productTypes = ProductType.byOrganizationTypes(filter.getOrganizationTypes());
        List<StatsV2DTO> stats = new ArrayList<>();
        for (ProductType type : productTypes) {
            stats.add(new StatsV2DTO(type));
        }

        List<Organization> organizations = organizationRepository.findAllByFilter(filter).getContent();
        List<OrganizationStatsDTO> result = new ArrayList<>();
        for (Organization organization : organizations) {
            result.add(new OrganizationStatsDTO(organization.getId(), organization.getName(), stats));
        }

        return result;
    }

    public List<StatsV2DTO> getProductsTemp(DashboardFilter filter) {
        ProductFilter productFilter = new ProductFilter();
        productFilter.setOrganizationId(filter.getOrganizationId());
        productFilter.setProductType(filter.getProductType());
        List<Product> products = productRepository.findAllByFilter(productFilter).getContent();
        List<StatsV2DTO> result = new ArrayList<>();
        for (Product product : products) {
            result.add(new StatsV2DTO(product.getName()));
        }
        return result;
    }

    public List<RiskStatsDTO> getRiskDataTemp(DashboardFilter filter) {
        Set<ProductType> productTypes = ProductType.byOrganizationTypes(filter.getOrganizationTypes());
        List<RiskStatsDTO> result = new ArrayList<>();
        for (ProductType type : productTypes) {
            RiskStatsDTO dto = new RiskStatsDTO(type);
            ProductFilter productFilter = new ProductFilter();
            productFilter.setProductType(type);
            List<Product> products = productRepository.findAllByFilter(productFilter).getContent();
            for (Product product : products) {
                dto.getData().add(new RiskStatsDTO(product.getName()));
            }
            result.add(dto);
        }
        return result;
    }
}
