package uz.uzkassa.smartpos.repository.clickhouse.sales;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.dto.SalesDynamicsDto;
import uz.uzkassa.smartpos.dto.analytics.ReceiptStatDto;
import uz.uzkassa.smartpos.dto.clickhouse.SalesClickhouseInfoDto;
import uz.uzkassa.smartpos.dto.receipt.CreditPaymentDTO;
import uz.uzkassa.smartpos.dto.sales.SalesReportStatsDto;
import uz.uzkassa.smartpos.dto.sales.SalesTopBranchDto;
import uz.uzkassa.smartpos.filter.SalesFilter;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesClickhouseRepository {
    ReceiptStatDto statistics(SalesFilter filter);

    Page<SalesClickhouseInfoDto> getSalesList(SalesFilter filter);

    Optional<SalesClickhouseInfoDto> findById(Long id);

    Optional<SalesClickhouseInfoDto> findFirstOriginByUid(String originUid);

    List<SalesTopBranchDto> topBranches(SalesFilter filter);

    List<SalesDynamicsDto> salesDynamics(SalesFilter filter);

    SalesReportStatsDto getSalesReportStats(SalesFilter filter);

    List<CreditPaymentDTO> findAllCreditPayments(String originUid);
}
