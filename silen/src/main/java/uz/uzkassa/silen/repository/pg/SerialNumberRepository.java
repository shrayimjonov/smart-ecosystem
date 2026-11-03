package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.SerialNumber;

import java.util.Optional;

@Repository
public interface SerialNumberRepository extends JpaRepository<SerialNumber, String>, SerialNumberCustomRepository {
    Optional<SerialNumber> findByIdAndOrganizationId(String serialNumberId, String organizationId);

    Optional<SerialNumber> findBySerialNumberAndOrganizationId(String serialNumber, String organizationId);
}
