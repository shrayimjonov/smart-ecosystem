package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RentInvoice;

import java.util.List;

@Repository
public interface RentInvoiceRepository extends BaseRepository<RentInvoice, Long> {
    List<RentInvoice> findByXfileIdIsNull();

    RentInvoice findByXfileId(String xfileId);
}
