package uz.uzkassa.smartpossupply.repository.kpi;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.uzkassa.smartpossupply.domain.kpi.salesplan.KPISalesPlanItem;

public interface KPISalesPlanItemRepository extends JpaRepository<KPISalesPlanItem, Long> {
}
