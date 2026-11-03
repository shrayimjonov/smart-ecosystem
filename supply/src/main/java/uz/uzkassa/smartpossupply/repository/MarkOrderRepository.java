package uz.uzkassa.smartpossupply.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrder;

@Repository
public interface MarkOrderRepository extends BaseRepository<MarkOrder, Long> {
    @Query("SELECT coalesce(max(mo.incrementNumber), 0) FROM MarkOrder mo where mo.companyId=:companyId")
    Long getMaxIncrementNumber(@Param("companyId") Long companyId);
}
