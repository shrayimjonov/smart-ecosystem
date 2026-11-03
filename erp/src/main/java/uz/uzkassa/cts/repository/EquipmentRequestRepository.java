package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.uzkassa.cts.domain.hrms.EquipmentRequest;
import uz.uzkassa.cts.repository.custom.EquipmentRequestRepositoryCustom;

public interface EquipmentRequestRepository extends BaseRepository<EquipmentRequest, Long>, EquipmentRequestRepositoryCustom {

    @Query("SELECT COALESCE(MAX(er.incrementNumber), 0) FROM EquipmentRequest er WHERE er.companyId = :companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
