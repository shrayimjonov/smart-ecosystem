package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.RentDeviceActOfReturn;

import java.util.List;

@Repository
public interface RentDeviceActOfReturnRepository extends BaseRepository<RentDeviceActOfReturn, Long> {
    List<RentDeviceActOfReturn> findByCustomerId(Long id);

    @Query("select ar from RentDeviceActOfReturn ar" +
        " inner join ar.customer c " +
        " inner join fetch ar.rentDeviceActDetail acc " +
        " inner join acc.device d " +
        " where c.id = :customerId and d.serialNumber = :serialNumber")
    RentDeviceActOfReturn findByCustomerIdAndDeviceSerialNumber(@Param("customerId") Long customerId, @Param("serialNumber") String serialNumber);
}
