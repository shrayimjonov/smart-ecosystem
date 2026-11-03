package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.ListInvoice;
import uz.uzkassa.silen.enumeration.Status;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListInvoiceRepository extends JpaRepository<ListInvoice, Long>, ListInvoiceRepositoryCustom {
    //    @Lock(LockModeType.PESSIMISTIC_READ)//Во избежании изменений двумя и более потоками берем lock на заданную строку
//    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    Optional<ListInvoice> findOneByFacturaId(String facturaId);

    List<ListInvoice> findAllByFacturaIdIn(Collection<String> collection);

    @Query("select id from ListInvoice where status=:status")
    List<Long> findIdAllByStatus(@Param("status") Status status);

}
