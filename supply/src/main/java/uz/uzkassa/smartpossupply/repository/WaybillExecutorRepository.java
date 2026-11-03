package uz.uzkassa.smartpossupply.repository;


import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.waybill.WaybillExecutor;

import java.util.Optional;

@Repository
public interface WaybillExecutorRepository extends BaseRepository<WaybillExecutor, Long> {

    Optional<WaybillExecutor> findFirstByWaybillIdAndOperationNumber(Long waybillId, String operationNumber);
}
