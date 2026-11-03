package uz.uzkassa.cts.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.Approver;
import uz.uzkassa.cts.enums.EntityTypeEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApproverRepository extends BaseRepository<Approver, Long> {

    @Query("select a from Approver a where a.current = true and a.entityId = :entityId and a.entityType = :entityType ")
    Optional<Approver> findCurrentApprover(@Param("entityId") Long entityId, @Param("entityType") EntityTypeEnum entityType);

    @Query("select a.employeeId from Approver a where a.entityId=:entityId and a.entityType=:entityType")
    List<Long> findAllEmployeeIdsByEntityIdAndEntityType(@Param("entityId") Long entityId, @Param("entityType") EntityTypeEnum entityType);

    @Query("select a from Approver a where a.step = :step and a.entityId = :entityId and a.entityType = :entityType")
    Optional<Approver> findByStepAndEntityIdAndEntityType(@Param("step") Integer step, @Param("entityId") Long entityId, @Param("entityType") EntityTypeEnum entityType);
}
