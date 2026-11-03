package uz.uzkassa.smartpossupply.repository.kpi;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.kpi.KPI;
import uz.uzkassa.smartpossupply.dto.base.ResultList;
import uz.uzkassa.smartpossupply.filters.KPIFilter;
import uz.uzkassa.smartpossupply.repository.impl.BaseRepositoryImpl;

public class KPIRepositoryImpl extends BaseRepositoryImpl implements KPIRepositoryCustom {
    @Override
    public ResultList<KPI> getResultList(KPIFilter filter) {
        ResultList<KPI> resultList = new ResultList<>();

        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT k FROM KPI k WHERE k.deleted = FALSE");

        StringBuilder countJpql = new StringBuilder();
        countJpql.append("SELECT COUNT(k.id) FROM KPI k WHERE k.deleted = FALSE");

        if (filter.getCompanyId() != null) {
            jpql.append(" AND k.companyId = :companyId");
            countJpql.append(" AND k.companyId = :companyId");
        }

        if (filter.getRole() != null) {
            jpql.append(" AND k.role = :role");
            countJpql.append(" AND k.role = :role");
        }
        if (filter.getType() != null) {
            jpql.append(" AND k.type = :type");
            countJpql.append(" AND k.type = :type");
        }
        if (filter.getStatus() != null) {
            jpql.append(" AND k.status = :status");
            countJpql.append(" AND k.status = :status");
        }

        if (filter.isNotEmpty()) {
            jpql.append(" AND (");
            jpql.append("lower(k.name)").append(filter.getSearchKey());
            jpql.append(")");

            countJpql.append(" AND (");
            countJpql.append("lower(k.name)").append(filter.getSearchKey());
            countJpql.append(")");

        }

        TypedQuery<KPI> query = entityManager
                .createQuery(jpql.toString(), KPI.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql.toString(), Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getRole() != null) {
            query.setParameter("role", filter.getRole());
            countQuery.setParameter("role", filter.getRole());
        }
        if (filter.getType() != null) {
            query.setParameter("type", filter.getType());
            countQuery.setParameter("type", filter.getType());
        }
        if (filter.getStatus() != null) {
            query.setParameter("status", filter.getStatus());
            countQuery.setParameter("status", filter.getStatus());
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
            countQuery.setParameter("searchKey", filter.getSearchValue());
        }

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setCount(count);
            resultList.setList(query.getResultList());
        }
        return resultList;
    }
}
