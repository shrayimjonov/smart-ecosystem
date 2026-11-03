package uz.uzkassa.smartpos.repository.postgres.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.common.City;

import java.util.Optional;

@SuppressWarnings("unused")
@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    Optional<City> findOneByCodeAndRegionCode(Long code, Long regionCode);
}
