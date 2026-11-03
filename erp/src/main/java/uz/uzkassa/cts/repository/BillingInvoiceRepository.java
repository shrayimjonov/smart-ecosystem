package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.BillingInvoice;

@Repository
public interface BillingInvoiceRepository extends BaseRepository<BillingInvoice, Long> {
}
