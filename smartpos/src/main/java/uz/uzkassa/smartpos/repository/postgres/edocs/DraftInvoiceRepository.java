package uz.uzkassa.smartpos.repository.postgres.edocs;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.DraftInvoice;

public interface DraftInvoiceRepository extends JpaRepository<DraftInvoice, Long> {
}
