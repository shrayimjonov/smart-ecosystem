package uz.uzkassa.smartpos.repository.postgres.common;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.common.Neighborhood;

import java.util.Collection;
import java.util.List;

public interface NeighborhoodRepository extends JpaRepository<Neighborhood, Long> {
    List<Neighborhood> findByCodeIn(Collection<Long> codes);

    List<Neighborhood> findByCodeNotInAndCityCodeAndRegionCode(Collection<Long> codes, Long cityCode, Long regionCode);

    List<Neighborhood> findAllByCity_Id(Long cityId);
}
