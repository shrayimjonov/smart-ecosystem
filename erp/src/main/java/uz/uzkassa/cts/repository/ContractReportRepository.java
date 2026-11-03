package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.report.ContractReport;
import uz.uzkassa.cts.enums.ContractReportInvoiceSaleStatus;
import uz.uzkassa.cts.repository.custom.ContractReportRepositoryCustom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractReportRepository extends BaseRepository<ContractReport, Long>, ContractReportRepositoryCustom {

    Optional<ContractReport> findBySerialNumber(String serialNumber);

    Optional<ContractReport> findBySerialNumberAndSaleStatusIn(String serialNumber, List<ContractReportInvoiceSaleStatus> saleStatuses);
}
