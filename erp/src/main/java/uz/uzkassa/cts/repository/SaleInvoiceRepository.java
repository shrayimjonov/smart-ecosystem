package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.SaleInvoice;
import uz.uzkassa.cts.repository.custom.SaleInvoiceRepositoryCustom;

import java.util.List;

@Repository
public interface SaleInvoiceRepository extends BaseRepository<SaleInvoice, Long>, SaleInvoiceRepositoryCustom {

    SaleInvoice findBySaleContract_Id(Long saleContractId);

    List<SaleInvoice> findAllByXfileIdIsNull();
}
