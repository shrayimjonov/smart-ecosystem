package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RentActAccessory;

import java.util.List;
import java.util.Set;

@Repository
public interface RentActAccessoryRepository extends BaseRepository<RentActAccessory, Long> {

    List<RentActAccessory> findByIdInAndCustomerId(List<Long> ids, Long customerId);

    @EntityGraph(attributePaths = {"accessory"})
    List<RentActAccessory> findByActiveAndActOfReturnIdIsNull(Boolean active);

    @EntityGraph(attributePaths = {"accessory"})
    List<RentActAccessory> findByIdIn(List<Long> ids);

    @EntityGraph(attributePaths = {"accessory"})
    List<RentActAccessory> findByActiveAndCustomerId(Boolean active, Long customerId);

    @Query("select distinct a.customerId from RentActAccessory a where a.active = true and a.actOfReturnId is null")
    Set<Long> findByActiveActOfReturnIdIsNull();
}
