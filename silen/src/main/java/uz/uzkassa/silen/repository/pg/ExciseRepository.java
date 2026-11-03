package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Excise;
import uz.uzkassa.silen.enumeration.ProductType;

import java.util.Optional;

/**
 * Spring Data repository for the Excise entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExciseRepository extends JpaRepository<Excise, Long> {
    Optional<Excise> findOneByType(ProductType type);
}
