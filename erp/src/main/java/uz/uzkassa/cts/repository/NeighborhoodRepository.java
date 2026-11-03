package uz.uzkassa.cts.repository;


import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.address.Neighborhood;
import uz.uzkassa.cts.repository.custom.NeighborhoodRepositoryCustom;

import java.util.Optional;

@Repository
public interface NeighborhoodRepository extends BaseRepository<Neighborhood, Long>, NeighborhoodRepositoryCustom {

    Optional<Neighborhood> findByCode(Integer code);
}
