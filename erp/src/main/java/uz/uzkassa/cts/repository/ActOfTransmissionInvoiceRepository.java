package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ActOfTransmissionInvoice;
import uz.uzkassa.cts.repository.custom.ActOfTransmissionInvoiceRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActOfTransmissionInvoiceRepository extends BaseRepository<ActOfTransmissionInvoice, Long>, ActOfTransmissionInvoiceRepositoryCustom {
    ActOfTransmissionInvoice findByActOfTransmission_id(Long actOfTransmissionId);

    List<ActOfTransmissionInvoice> findByXfileIdIsNullAndInvoiceDateBetweenOrderByIdAsc(LocalDateTime starDate, LocalDateTime endDate);

    ActOfTransmissionInvoice findByXfileId(String id);

    @Override
//    @EntityGraph(attributePaths = {"details"})
    Page<ActOfTransmissionInvoice> findAll(Specification<ActOfTransmissionInvoice> specification, Pageable pageable);
}
