package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.IncomingInvoice;

import java.time.LocalDateTime;
import java.util.Set;

@Repository
public interface IncomingInvoiceRepository extends BaseRepository<IncomingInvoice, Long> {

    @Query("select inc from IncomingInvoice inc " +
            "inner join fetch inc.invoiceDetail det " +
            "where inc.createdDate between :startDate and :endDate " +
            "and det.sellerTin = :tin " +
            "order by inc.id asc ")
    Set<IncomingInvoice> findAllByCreatedDateBetween(@Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate,
                                                     @Param("tin") String tin);
}

