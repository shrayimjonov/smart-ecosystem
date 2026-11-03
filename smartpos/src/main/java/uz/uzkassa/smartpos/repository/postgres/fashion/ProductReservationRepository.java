package uz.uzkassa.smartpos.repository.postgres.fashion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.fashion.ProductReservation;

@Repository
public interface ProductReservationRepository extends JpaRepository<ProductReservation, Long>, JpaSpecificationExecutor<ProductReservation> {
}
