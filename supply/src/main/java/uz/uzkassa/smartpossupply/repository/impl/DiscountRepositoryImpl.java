package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.discount.Discount;
import uz.uzkassa.smartpossupply.enums.CommonStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.DiscountRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.12.2024
 */
public class DiscountRepositoryImpl extends BaseFilterableRepositoryImpl<Discount, BaseFilter> implements DiscountRepositoryCustom {

    @Override
    protected Class<Discount> getEntityClass() {
        return Discount.class;
    }

    @Override
    protected String getEntityAlias() {
        return "d";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
        filter.setBranchId(getBranchId(filter, false));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Discount d ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH d.branch b ")
                .append("JOIN FETCH d.creator creator ");
        } else {
            sql.append("JOIN d.branch b ")
                .append("JOIN d.creator creator ");
        }

        // Base conditions
        sql.append("WHERE d.deleted = false ")
            .append("AND b.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getBranchId() != null, "AND b.id = :branchId ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND d.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND d.validTo >= :fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND d.validTo <= :toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(d.name)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        query.setParameter("companyId", filter.getCompanyId());

        setParameterIfNotNull(query, "branchId", filter.getBranchId());
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        setParameterIfNotNull(query, "fromDate", filter.getFrom());
        setParameterIfNotNull(query, "toDate", filter.getTo());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
