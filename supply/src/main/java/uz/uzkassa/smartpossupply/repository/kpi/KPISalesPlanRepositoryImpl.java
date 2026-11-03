package uz.uzkassa.smartpossupply.repository.kpi;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.dto.kpi.KPISalesPlanDTO;
import uz.uzkassa.smartpossupply.repository.impl.BaseRepositoryImpl;

public class KPISalesPlanRepositoryImpl extends BaseRepositoryImpl implements KPISalesPlanRepositoryCustom {

    @Override
    public boolean existByFields(KPISalesPlanDTO kpiSalesPlanDTO) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("""
                   SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
                        FROM KPI k
                        WHERE k.companyId = :companyId
                          AND k.role = :role
                          AND k.type = :type
                          AND k.productAddType = :productAddType
                          AND k.salesPlanType = :salesPlanType
                          AND k.bonusType = :bonusType
                          AND k.deleted = false
                """);

        if (kpiSalesPlanDTO.getEmployeeIds() != null && !kpiSalesPlanDTO.getEmployeeIds().isEmpty()) {
            jpql.append(" AND EXISTS (SELECT 1 FROM k.employees e WHERE e.id IN :employeeIds)");
        }

        if (kpiSalesPlanDTO.getId() != null) {
            // exclude current row when updating
            jpql.append(" AND k.id != :id");
        }

        TypedQuery<Boolean> query = entityManager
                .createQuery(jpql.toString(), Boolean.class);

        query.setParameter("companyId", kpiSalesPlanDTO.getCompanyId());
        query.setParameter("role", kpiSalesPlanDTO.getRole());
        query.setParameter("type", kpiSalesPlanDTO.getType());
        query.setParameter("productAddType", kpiSalesPlanDTO.getProductAddType());
        query.setParameter("salesPlanType", kpiSalesPlanDTO.getSalesPlanType());
        query.setParameter("bonusType", kpiSalesPlanDTO.getBonusType());

        if (kpiSalesPlanDTO.getEmployeeIds() != null && !kpiSalesPlanDTO.getEmployeeIds().isEmpty()) {
            query.setParameter("employeeIds", kpiSalesPlanDTO.getEmployeeIds());
        }

        if (kpiSalesPlanDTO.getId() != null) {
            query.setParameter("id", kpiSalesPlanDTO.getId());
        }

        return query.getSingleResult();
    }
}
