package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.factura.Contract;
import uz.uzkassa.smartpossupply.enums.ContractStatus;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.ContractRepositoryCustom;

@Repository
public class ContractRepositoryImpl extends BaseFilterableRepositoryImpl<Contract, BaseFilter> implements ContractRepositoryCustom {

    @Override
    protected Class<Contract> getEntityClass() {
        return Contract.class;
    }

    @Override
    protected String getEntityAlias() {
        return "c";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Contract c ");

        // Add joins
        sql.append("JOIN c.customer cus ")
            .append("JOIN c.supplier sup ");

        // Base conditions
        sql.append("WHERE c.deleted = false ")
            .append("AND c.companyId = :companyId ");

        // Dynamic conditions
        appendCondition(sql, filter.getCustomerId() != null, "AND cus.id = :customerId ");
        appendCondition(sql, StringUtils.hasText(filter.getCustomerTin()), "AND cus.tin = :customerTin ");
        appendCondition(sql, StringUtils.hasText(filter.getSupplierTin()), "AND sup.tin = :supplierTin ");
        appendCondition(sql, StringUtils.hasText(filter.getStatus()), "AND c.status = :status ");
        appendCondition(sql, filter.getFrom() != null, "AND c.contractDate >=: fromDate ");
        appendCondition(sql, filter.getTo() != null, "AND c.contractDate <=: toDate ");

        // Search conditions
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(c.contractNumber)").append(filter.getSearchKey())
                .append("OR LOWER(c.description)").append(filter.getSearchKey())
                .append("OR LOWER(cus.name)").append(filter.getSearchKey())
                .append("OR LOWER(cus.tin)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Required parameters
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        setParameterIfNotNull(query, "customerId", filter.getCustomerId());
        setParameterIfNotNull(query, "customerTin", filter.getCustomerTin());
        setParameterIfNotNull(query, "supplierTin", filter.getSupplierTin());
        if (StringUtils.hasText(filter.getStatus())) {
            query.setParameter("status", ContractStatus.valueOf(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom().toLocalDate());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo().toLocalDate());
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
