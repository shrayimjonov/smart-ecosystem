package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RentActOfReturn;

import java.util.List;

@Repository
public interface RentActOfReturnRepository extends BaseRepository<RentActOfReturn, Long> {
    List<RentActOfReturn> findByCustomerId(Long id);

    @EntityGraph(attributePaths = {"customer", "accessories.accessory", "accessories.accessory.model"})
    @Query("select ar from RentActOfReturn ar" +
        " inner join ar.customer c " +
        " inner join fetch ar.accessories acc " +
        " where c.id = :customerId and acc.deviceSerialNumber = :deviceSerialNumber")
    RentActOfReturn findByCustomerIdAndDeviceSerialNumber(@Param("customerId") Long customerId, @Param("deviceSerialNumber") String deviceSerialNumber);
}
