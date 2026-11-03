package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.mark.MarkOrderProduct;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.MarkOrderProductRepositoryCustom;

public class MarkOrderProductRepositoryImpl extends BaseFilterableRepositoryImpl<MarkOrderProduct, BaseFilter> implements MarkOrderProductRepositoryCustom {

    @Override
    protected Class<MarkOrderProduct> getEntityClass() {
        return MarkOrderProduct.class;
    }

    @Override
    protected String getEntityAlias() {
        return "mop";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM MarkOrderProduct mop ");

        // Add joins
        if (includeFetch) {
            sql.append("JOIN FETCH mop.product p ")
                .append("JOIN FETCH mop.markOrder mo ");
        } else {
            sql.append("JOIN mop.product p ")
                .append("JOIN mop.markOrder mo ");
        }

        // Base conditions
        sql.append("WHERE mo.companyId = :companyId ")
            .append("AND mop.deleted = false ");

        // Dynamic conditions
        appendCondition(sql, filter.getProductId() != null, "AND p.id = :productId ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(p.name)").append(filter.getSearchKey())
                .append(" OR LOWER(p.barcode)").append(filter.getSearchKey())
                .append(" OR LOWER(p.vatBarcode)").append(filter.getSearchKey())
                .append(" OR LOWER(mo.orderNumber)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "productId", filter.getProductId());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
