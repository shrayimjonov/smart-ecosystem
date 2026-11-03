package uz.uzkassa.smartpos.repository.postgres.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingHistory;

public interface MarkingHistoryRepository extends JpaRepository<MarkingHistory, Long>, JpaSpecificationExecutor<MarkingHistory> {
}
