package uz.uzkassa.smartpos.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpos.domain.postgres.EpsServices;

import java.util.UUID;

public interface EpsServicesRepository extends JpaRepository<EpsServices, UUID> {
}
