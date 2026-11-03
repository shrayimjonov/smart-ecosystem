package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.company.CompanyFieldHistory;
import uz.uzkassa.smartpossupply.filters.CompanyFieldHistoryFilter;
import uz.uzkassa.smartpossupply.repository.CompanyFieldHistoryRepositoryCustom;

import java.util.List;

@Repository
public class CompanyFieldHistoryRepositoryImpl extends BaseRepositoryImpl implements CompanyFieldHistoryRepositoryCustom {

    @Override
    public List<CompanyFieldHistory> lookUp(CompanyFieldHistoryFilter filter) {
        filter.setCompanyId(getCompanyId(filter));

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT h FROM CompanyFieldHistory h ");
        sql.append(" WHERE h.deleted = false ");
        sql.append(" AND h.companyId =:companyId ");

        if (filter.getFieldType() != null) {
            sql.append(" AND h.fieldType =:fieldType ");
        }
        if (filter.getUserId() != null) {
            sql.append(" AND h.userId =:userId ");
        }
        if (filter.isNotEmpty()) {
            sql.append(" AND (");
            sql.append(" LOWER(h.identifier)").append(filter.getSearchKey());
            sql.append(" OR LOWER(COALESCE(h.displayValue, ''))").append(filter.getSearchKey());
            sql.append(")");
        }

        sql.append(" ORDER BY h.updatedDate DESC, h.id DESC");

        TypedQuery<CompanyFieldHistory> query = entityManager
            .createQuery(sql.toString(), CompanyFieldHistory.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        query.setParameter("companyId", filter.getCompanyId());

        if (filter.getFieldType() != null) {
            query.setParameter("fieldType", filter.getFieldType());
        }
        if (filter.getUserId() != null) {
            query.setParameter("userId", filter.getUserId());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }

        return query.getResultList();
    }
}