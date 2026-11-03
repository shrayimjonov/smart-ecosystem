package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.address.Neighborhood;

import java.util.List;
import java.util.Optional;


public interface NeighborhoodRepository extends BaseRepository<Neighborhood, Long> {

    Optional<Neighborhood> findFirstByDistrict_CodeAndCode(Integer districtCode, Integer code);

    List<Neighborhood> findAllByDistrictId(Long districtId);
}
