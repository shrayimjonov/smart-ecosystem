package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.waybill.WaybillTransport;

import java.util.Optional;

@Repository
public interface WaybillTransportRepository extends BaseRepository<WaybillTransport, Long> {

    Optional<WaybillTransport> findFirstByWaybillId(Long waybillId);
}
