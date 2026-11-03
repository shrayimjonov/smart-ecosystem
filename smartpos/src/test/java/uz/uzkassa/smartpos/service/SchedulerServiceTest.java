package uz.uzkassa.smartpos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uz.uzkassa.smartpos.dto.scheduling.request.ProductScheduleRequest;
import uz.uzkassa.smartpos.dto.scheduling.request.ScheduleRequest;
import uz.uzkassa.smartpos.dto.scheduling.response.ProductScheduleResponse;
import uz.uzkassa.smartpos.dto.scheduling.response.ScheduleResponse;
import uz.uzkassa.smartpos.repository.clickhouse.receipt.ClickhouseReceiptGenericRepository;
import uz.uzkassa.smartpos.repository.clickhouse.receiptdetail.ClickhouseReceiptDetailRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {
    @Mock
    private ProductBranchRepository productBranchRepository;
    @Mock
    private ClickhouseReceiptGenericRepository clickhouseReceiptGenericRepository;
    @Mock
    private ClickhouseReceiptDetailRepository clickhouseReceiptDetailRepository;
    @InjectMocks
    private SchedulerService schedulerService;

    @Test
    void creditTestWhenPaymentsEmpty() {
        ScheduleRequest request = new ScheduleRequest();
        request.setBranchId(1L);
        request.setForCredit(true);
        request.setMonth(7);
        ArrayList<ProductScheduleRequest> products = new ArrayList<>();
        ProductScheduleRequest product = new ProductScheduleRequest();
        product.setId(1L);
        product.setQty(BigDecimal.valueOf(7));
        product.setPrice(BigDecimal.valueOf(10000));
        product.setVatPercent(BigDecimal.valueOf(12));
        products.add(product);
        product = new ProductScheduleRequest();
        product.setId(2L);
        product.setQty(BigDecimal.valueOf(5));
        product.setPrice(BigDecimal.valueOf(40000));
        products.add(product);
        request.setProducts(products);

        ScheduleResponse response = schedulerService.schedule(request);
        assertNotNull(response);
        Map<Long, BigDecimal> totalCostMap = response.getProducts().stream().collect(Collectors.toMap(ProductScheduleResponse::getId, ProductScheduleResponse::getTotalCost));
        Map<Long, BigDecimal> totalVatMap = new HashMap<>();
        for (ProductScheduleResponse productScheduleResponse : response.getProducts()) {
            totalVatMap.put(productScheduleResponse.getId(), productScheduleResponse.getTotalVat());
        }
        Map<Long, BigDecimal> totalCostSumMap = response.getMonthlySchedules().stream().flatMap(monthlyScheduleResponse -> monthlyScheduleResponse.getProducts().stream()).collect(Collectors.groupingBy(ProductScheduleResponse::getId, Collectors.reducing(BigDecimal.ZERO, ProductScheduleResponse::getTotalCost, BigDecimal::add)));
        Map<Long, BigDecimal> totalVatSumMap = new HashMap<>();
        response.getMonthlySchedules().forEach(monthlyScheduleResponse -> monthlyScheduleResponse.getProducts().forEach(productScheduleResponse -> {
            if (totalVatSumMap.containsKey(productScheduleResponse.getId()) && productScheduleResponse.getTotalVat() != null) {
                totalVatSumMap.put(productScheduleResponse.getId(), totalVatSumMap.get(productScheduleResponse.getId()).add(productScheduleResponse.getTotalVat()));
            } else {
                totalVatSumMap.put(productScheduleResponse.getId(), productScheduleResponse.getTotalVat());
            }
        }));
        totalCostMap.forEach((id, totalCost) -> {
            BigDecimal totalCostSum = totalCostSumMap.get(id);
            assertEquals(0, totalCost.compareTo(totalCostSum));
        });
        totalVatMap.forEach((id, totalVat) -> {
            BigDecimal totalVatSum = totalVatSumMap.get(id);
            assertTrue((totalVat == null && totalVatSum == null) || (totalVat != null && totalVatSum != null && totalVat.compareTo(totalVatSum) == 0));
        });
    }

    @Test
    void advanceTestWhenPaymentsEmpty() {
        ScheduleRequest request = new ScheduleRequest();
        request.setBranchId(1L);
        request.setForCredit(false);
        ArrayList<ProductScheduleRequest> products = new ArrayList<>();
        ProductScheduleRequest product = new ProductScheduleRequest();
        product.setId(1L);
        product.setQty(BigDecimal.valueOf(7));
        product.setPrice(BigDecimal.valueOf(10000));
        product.setVatPercent(BigDecimal.valueOf(12));
        product.setAdvancePercent(BigDecimal.valueOf(30));
        products.add(product);
        product = new ProductScheduleRequest();
        product.setId(2L);
        product.setQty(BigDecimal.valueOf(5));
        product.setPrice(BigDecimal.valueOf(40000));
        product.setAdvancePercent(BigDecimal.valueOf(70));
        products.add(product);
        request.setProducts(products);

        ScheduleResponse response = schedulerService.schedule(request);
        assertNotNull(response);
        Map<Long, BigDecimal> totalCostMap = response.getProducts().stream().collect(Collectors.toMap(ProductScheduleResponse::getId, ProductScheduleResponse::getTotalCost));
        Map<Long, BigDecimal> totalVatMap = new HashMap<>();
        for (ProductScheduleResponse productScheduleResponse : response.getProducts()) {
            totalVatMap.put(productScheduleResponse.getId(), productScheduleResponse.getTotalVat());
        }
        Map<Long, BigDecimal> totalCostSumMap = response.getMonthlySchedules().stream().flatMap(monthlyScheduleResponse -> monthlyScheduleResponse.getProducts().stream()).collect(Collectors.groupingBy(ProductScheduleResponse::getId, Collectors.reducing(BigDecimal.ZERO, ProductScheduleResponse::getTotalCost, BigDecimal::add)));
        Map<Long, BigDecimal> totalVatSumMap = new HashMap<>();
        response.getMonthlySchedules().forEach(monthlyScheduleResponse -> monthlyScheduleResponse.getProducts().forEach(productScheduleResponse -> {
            if (totalVatSumMap.containsKey(productScheduleResponse.getId()) && productScheduleResponse.getTotalVat() != null) {
                totalVatSumMap.put(productScheduleResponse.getId(), totalVatSumMap.get(productScheduleResponse.getId()).add(productScheduleResponse.getTotalVat()));
            } else {
                totalVatSumMap.put(productScheduleResponse.getId(), productScheduleResponse.getTotalVat());
            }
        }));
        totalCostMap.forEach((id, totalCost) -> {
            BigDecimal totalCostSum = totalCostSumMap.get(id);
            assertEquals(0, totalCost.compareTo(totalCostSum));
        });
        totalVatMap.forEach((id, totalVat) -> {
            BigDecimal totalVatSum = totalVatSumMap.get(id);
            assertTrue((totalVat == null && totalVatSum == null) || (totalVat != null && totalVatSum != null && totalVat.compareTo(totalVatSum) == 0));
        });
    }
}