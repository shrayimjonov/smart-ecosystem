package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RentAct;
import uz.uzkassa.cts.enums.ContractStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentActRepository extends BaseRepository<RentAct, Long> {
    List<RentAct> findByOfferId(Long offerId);

    Optional<RentAct> findByIdAndCustomerId(Long id, Long customerId);
    List<RentAct> findByCustomerIdAndStatus(Long customerId, ContractStatus status);
    List<RentAct> findByCustomerIdAndStatusIn(Long customerId, List<ContractStatus> statuses);

    @EntityGraph(attributePaths = {"accessories", "offer", "customer"})
    Optional<RentAct> findById(Long id);
}
