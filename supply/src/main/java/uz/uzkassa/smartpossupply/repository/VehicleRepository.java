package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.warehouse.Vehicle;

import java.util.Optional;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 23.01.2025
 */
@Repository
public interface VehicleRepository extends BaseRepository<Vehicle, Long>, VehicleRepositoryCustom {

    boolean existsByCompanyIdAndNameIgnoreCase(Long companyId, String name);

    boolean existsByCompanyIdAndRegNoIgnoreCase(Long companyId, String regNo);

    boolean existsByCompanyIdAndNameIgnoreCaseAndIdNot(Long companyId, String name, Long id);

    boolean existsByCompanyIdAndRegNoIgnoreCaseAndIdNot(Long companyId, String regNo, Long id);

    @Query("select v from Vehicle v where v.id=:id")
    @EntityGraph(attributePaths = {"company"})
    Optional<Vehicle> getDetail(@Param("id") Long id);
}
