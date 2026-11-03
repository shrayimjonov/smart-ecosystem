package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.Query;
import uz.uzkassa.smartpossupply.dto.company.CompanyCommittentLookUpDTO;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.CompanyCommittentRepositoryCustom;

import java.util.List;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 01.06.2023
 */
public class CompanyCommittentRepositoryImpl extends BaseRepositoryImpl implements CompanyCommittentRepositoryCustom {

    @Override
    public List<CompanyCommittentLookUpDTO> lookUp(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        StringBuilder sql = new StringBuilder();
        sql.append("select ct.id, cc.id as committentId, cc.name as committentName, cc.tin as committentTin ");
        sql.append(" from company_committent ct ");
        sql.append(" inner join company c on ct.company_id = c.id ");
        sql.append(" inner join company cc on ct.committent_id = cc.id ");
        sql.append(" where c.id=:companyId");
        if (filter.isNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getSearchKey());
            sql.append(" or lower(c.tin) ").append(filter.getSearchKey());
            sql.append(" or lower(cc.tin) ").append(filter.getSearchKey());
            sql.append(" or lower(cc.name) ").append(filter.getSearchKey());
            sql.append(") ");
        }
        sql.append(" order by ct.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        Query query = entityManager
            .createNativeQuery(sql.toString(), "CommittentLookUpMapper")
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        query.setParameter("companyId", filter.getCompanyId());
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
        return query.getResultList();
    }
}
