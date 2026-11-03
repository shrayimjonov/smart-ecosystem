package uz.uzkassa.smartpos.repository.clickhouse.receiptdetail;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.dto.analytics.ProductStatsDTO;
import uz.uzkassa.smartpos.dto.analytics.SalesStatsDTO;
import uz.uzkassa.smartpos.dto.analytics.SalesStatsProductsHeader;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesDynamicsFilter;
import uz.uzkassa.smartpos.dto.filter.receipt.SalesStatsFilter;
import uz.uzkassa.smartpos.dto.scheduling.request.ProductScheduleRequest;

import java.util.List;

@Repository
public interface ClickhouseReceiptDetailRepository {

    Page<SalesStatsDTO> getProductSalesStatsByBranch(SalesStatsFilter filter, boolean forExcel);

    Page<SalesStatsDTO> getProductSalesStatsByCompany(SalesStatsFilter filter, boolean forExcel);

    SalesStatsProductsHeader getProductSalesStatsHeader(SalesStatsFilter filter);

    Page<SalesStatsDTO> getProductSalesDynamics(SalesDynamicsFilter filter);

    List<ProductStatsDTO> getTopProducts(SalesStatsFilter filter);

    List<SalesStatsDTO> getCategorySalesStats(SalesStatsFilter filter);

    List<ProductScheduleRequest> findCreditDetails(Long receiptId);

    List<ProductScheduleRequest> findAdvanceDetails(Long receiptId);
}
