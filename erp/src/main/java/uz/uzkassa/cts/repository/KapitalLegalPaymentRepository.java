package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.KapitalLegalPayment;

import java.util.Optional;

@Repository
public interface KapitalLegalPaymentRepository extends BaseRepository<KapitalLegalPayment, Long> {
    @Query("select k from  KapitalLegalPayment k where k.id = (select max(i.id) from KapitalLegalPayment i)")
    KapitalLegalPayment findTopOrderByIdDesc();

    Optional<KapitalLegalPayment> findByOperDay(String date);
}
