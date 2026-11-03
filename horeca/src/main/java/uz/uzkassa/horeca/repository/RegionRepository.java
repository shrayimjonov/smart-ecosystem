package uz.uzkassa.horeca.repository;

import uz.uzkassa.horeca.domain.address.Region;

import java.util.Optional;

public interface RegionRepository extends BaseRepository<Region, Long> {

    Optional<Region> findFirstByCode(Integer code);
}
