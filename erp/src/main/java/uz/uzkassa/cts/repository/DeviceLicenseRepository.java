package uz.uzkassa.cts.repository;


import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.DeviceLicense;

import java.util.Optional;

@Repository
public interface DeviceLicenseRepository extends BaseRepository<DeviceLicense, Long> {

    Optional<DeviceLicense> findByDeviceSerialNumber(String serialNumber);
}
