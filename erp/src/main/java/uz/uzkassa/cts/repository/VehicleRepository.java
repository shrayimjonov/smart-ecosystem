package uz.uzkassa.cts.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Vehicle;
import uz.uzkassa.cts.repository.custom.VehicleRepositoryCustom;

@Repository
public interface VehicleRepository extends BaseRepository<Vehicle, Long>, VehicleRepositoryCustom {
}
