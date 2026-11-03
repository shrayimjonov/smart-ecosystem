package uz.uzkassa.cts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RentOffer;

import java.util.Optional;

@Repository
public interface RentOfferRepository extends BaseRepository<RentOffer, Long> {

    @EntityGraph(attributePaths = {"branch", "customer"})
    Page<RentOffer> findAll(Specification<RentOffer> specification, Pageable pageable);

    Optional<RentOffer> findByIdAndCustomerId(Long id, Long customerId);
    Optional<RentOffer> findFirstByCustomerIdOrderByCreatedDateDesc(Long customerId);
}
