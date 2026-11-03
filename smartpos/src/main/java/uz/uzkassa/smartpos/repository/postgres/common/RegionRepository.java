package uz.uzkassa.smartpos.repository.postgres.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.common.City;
import uz.uzkassa.smartpos.domain.postgres.common.Region;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Region entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    @Query("select r.cities from Region r where r.id = :id")
    List<City> findByRegionId(@Param("id") Long id);

    Optional<Region> findOneByCode(Long code);
}
