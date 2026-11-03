package uz.uzkassa.smartpossupply.repository.kpi;

import jakarta.persistence.Query;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.enums.KPIStatus;
import uz.uzkassa.smartpossupply.repository.impl.BaseRepositoryImpl;

@Component
public class KPIPlanRouteRepositoryImpl extends BaseRepositoryImpl implements KPIPlanRouteRepositoryCustom {

    @Override
    public boolean updateStatus(KPI kpi, KPIStatus status) {
        StringBuilder slq = new StringBuilder("""
                       SELECT EXISTS (
                            SELECT 1
                            FROM kpi k
                            WHERE k.company_id = :companyId
                              AND k.id != :id
                              AND k.deleted = FALSE
                              AND k.status = :status
                              AND k.type = 'PLAN_ROUTE'
                       )
                """);
        Query nativeQuery = entityManager.createNativeQuery(slq.toString(), Boolean.class);
        nativeQuery.setParameter("companyId", kpi.getCompanyId());
        nativeQuery.setParameter("id", kpi.getId());
        nativeQuery.setParameter("status", status.name());
        return (boolean) nativeQuery.getSingleResult();
    }
}
