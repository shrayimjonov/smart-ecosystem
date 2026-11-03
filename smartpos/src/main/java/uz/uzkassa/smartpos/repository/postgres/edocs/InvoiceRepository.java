package uz.uzkassa.smartpos.repository.postgres.edocs;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.edocs.invoice.Invoice;

import java.time.LocalDate;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, InvoiceRepositoryCustom {
    Optional<Invoice> findOneByFacturaId(String facturaId);

    Optional<Invoice> findFirstByNumberAndInvoiceDateAndSellerTin(String number, LocalDate invoiceDate, String sellerTin);

}
