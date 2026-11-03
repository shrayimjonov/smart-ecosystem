package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.silen.domain.Waybill;

import java.util.Optional;

public interface WaybillRepository extends JpaRepository<Waybill, String>, WaybillRepositoryCustom {

    Optional<Waybill> findFirstByWaybillLocalId(String waybillLocalId);
}
