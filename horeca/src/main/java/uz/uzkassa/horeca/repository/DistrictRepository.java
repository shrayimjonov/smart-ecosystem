package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.address.District;

import java.util.List;
import java.util.Optional;


public interface DistrictRepository extends BaseRepository<District, Long> {

    Optional<District> findFirstByRegion_CodeAndCode(Integer regionCode, Integer code);

    List<District> findAllByRegionId(Long regionId);
}
