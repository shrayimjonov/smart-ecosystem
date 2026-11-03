package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranchProduct;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranchProductHistory;
import uz.uzkassa.smartpossupply.domain.planroute.PlanRouteCustomerBranch;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchProductDTO;
import uz.uzkassa.smartpossupply.dto.customerbranch.CustomerBranchProductListDTO;
import uz.uzkassa.smartpossupply.enums.GenericSettingsType;
import uz.uzkassa.smartpossupply.enums.SortOrderType;
import uz.uzkassa.smartpossupply.filters.CustomerBranchFilter;
import uz.uzkassa.smartpossupply.filters.ProductFilter;
import uz.uzkassa.smartpossupply.repository.CustomerBranchProductHistoryRepository;
import uz.uzkassa.smartpossupply.repository.CustomerBranchProductRepository;
import uz.uzkassa.smartpossupply.repository.PlanRouteCustomerBranchRepository;
import uz.uzkassa.smartpossupply.repository.ProductRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 30.11.2023
 */
@Service
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CustomerBranchProductService extends BaseService {

    PlanRouteCustomerBranchRepository planRouteCustomerBranchRepository;
    CustomerBranchProductRepository customerBranchProductRepository;
    ProductRepository productRepository;
    CustomerBranchProductHistoryRepository productHistoriesRepository;

    @Transactional
    public Long create(CustomerBranchProductDTO customerBranchProductDTO) {
        return planRouteCustomerBranchRepository
                .findById(customerBranchProductDTO.getPlanRouteCustomerBranchId())
                .map(planRouteCustomerBranch -> {
                    HashMap<Long, CustomerBranchProduct> customerBranchProductsMap = new HashMap<>();

                    // Set From and To date to avoid processing the same product more than once a day
                    LocalDate currentDateTime = LocalDateTime.now().toLocalDate();
                    LocalDateTime startOfDay = currentDateTime.atStartOfDay();
                    LocalDateTime endOfDay = currentDateTime.atTime(LocalTime.MAX);

                    customerBranchProductRepository.findAllByPlanRouteCustomerBranchIdAndCreatedDateBetween(planRouteCustomerBranch.getId(), startOfDay, endOfDay)
                            .forEach(customerBranchProduct -> customerBranchProductsMap.put(customerBranchProduct.getId(), customerBranchProduct));
                    List<CustomerBranchProduct> customerBranchProducts = customerBranchProductDTO.getCustomerBranchProductItems().stream().map(customerBranchProductItemDTO -> {
                        CustomerBranchProduct customerBranchProduct;
                        if (customerBranchProductItemDTO.getId() != null && customerBranchProductsMap.get(customerBranchProductItemDTO.getId()) != null) {
                            customerBranchProduct = customerBranchProductsMap.get(customerBranchProductItemDTO.getId());
                        } else {
                            customerBranchProduct = new CustomerBranchProduct();
                            customerBranchProduct.setPlanRouteCustomerBranchId(planRouteCustomerBranch.getId());
                            customerBranchProduct.setProductId(customerBranchProductItemDTO.getProductId());
                            customerBranchProduct.setUnitId(customerBranchProductItemDTO.getUnitId());
                        }
                        customerBranchProduct.setQty(customerBranchProductItemDTO.getQty());
                        customerBranchProduct.setPrice(customerBranchProductItemDTO.getPrice());
                        return customerBranchProduct;
                    }).toList();
                    customerBranchProductRepository.saveAll(customerBranchProducts);
                    saveCustomerBranchProductsHistory(customerBranchProducts);
                    return planRouteCustomerBranch.getId();
                })
                .orElseThrow(validationService.entityNotFoundExceptionThrow(PlanRouteCustomerBranch.class.getSimpleName(), "id", customerBranchProductDTO.getPlanRouteCustomerBranchId()));
    }

    public Page<CustomerBranchProductListDTO> getList(CustomerBranchFilter filter) {
        ProductFilter productFilter = new ProductFilter();
        productFilter.setPage(filter.getPage());
        productFilter.setSearch(filter.getSearch());
        productFilter.setSortOrderType(SortOrderType.LIST.getCode());
        productFilter.setBranchId(filter.getBranchId()); // need to filter among super agent's branches
        productFilter.setEnabledSorting(companySettingsRepository.existsByCompanyIdAndSettings_SettingsType(getCurrentCompanyId(), GenericSettingsType.SORTING_PRODUCTS));
        ResultList<Product> productResultList = productRepository.getResultList(productFilter);
        filter.setProductIds(productResultList.getList().stream().map(Product::getId).toList());

        // Allow updating quantity and price only on the same day. They will be reset to zero the next day.
        LocalDate currentDateTime = LocalDateTime.now().toLocalDate();
        LocalDateTime startOfDay = currentDateTime.atStartOfDay();
        LocalDateTime endOfDay = currentDateTime.atTime(LocalTime.MAX);
        filter.setFrom(startOfDay);
        filter.setTo(endOfDay);

        Map<Long, CustomerBranchProduct> customerBranchProductMap = new HashMap<>();
        customerBranchProductRepository.getResultList(filter).forEach(customerBranchProduct -> customerBranchProductMap.put(customerBranchProduct.getProductId(), customerBranchProduct));
        List<CustomerBranchProductListDTO> result = productResultList.getList().stream().map(product -> {
            CustomerBranchProduct customerBranchProduct = customerBranchProductMap.get(product.getId());
            CustomerBranchProductListDTO customerBranchProductListDTO = new CustomerBranchProductListDTO();
            if (customerBranchProduct != null) {
                customerBranchProductListDTO.setId(customerBranchProduct.getId());
                customerBranchProductListDTO.setQty(customerBranchProduct.getQty());
                customerBranchProductListDTO.setPrice(customerBranchProduct.getPrice());
                customerBranchProductListDTO.setCustomerBranch(Optional.ofNullable(customerBranchProduct.getPlanRouteCustomerBranch().getCustomerBranch()).map(CustomerBranch::toCommonDTO).orElse(null));
            }
            customerBranchProductListDTO.setProduct(product.toCommonDTO());
            customerBranchProductListDTO.setUnit(product.getUnit().toCommonDTO());
            return customerBranchProductListDTO;
        }).toList();
        return new PageImpl<>(result, filter.getSortedPageable(), productResultList.getCount());
    }

    private void saveCustomerBranchProductsHistory(List<CustomerBranchProduct> customerBranchProducts) {
        List<CustomerBranchProductHistory> customerBranchProductHistories = customerBranchProducts.stream().map(customerBranchProduct -> {
            CustomerBranchProductHistory customerBranchProductHistory = new CustomerBranchProductHistory();
            customerBranchProductHistory.setPlanRouteCustomerBranchId(customerBranchProduct.getPlanRouteCustomerBranchId());
            customerBranchProductHistory.setProductId(customerBranchProduct.getProductId());
            customerBranchProductHistory.setUnitId(customerBranchProduct.getUnitId());
            customerBranchProductHistory.setQty(customerBranchProduct.getQty());
            customerBranchProductHistory.setPrice(customerBranchProduct.getPrice());
            return customerBranchProductHistory;
        }).toList();
        productHistoriesRepository.saveAll(customerBranchProductHistories);
    }
}