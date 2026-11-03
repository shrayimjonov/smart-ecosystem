package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.container.Container;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.ContainerRepositoryCustom;

@SuppressWarnings("unused")
public class ContainerRepositoryImpl
    extends BaseFilterableRepositoryImpl<Container, BaseFilter>
    implements ContainerRepositoryCustom {

    @Override
    protected Class<Container> getEntityClass() {
        return Container.class;
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
        StringBuilder sql = new StringBuilder(" FROM Container c ");

        // Base conditions
        sql.append("WHERE c.deleted = false ")
            .append("AND c.companyId = :companyId ");

        // Search conditions across multiple fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(c.identificationNumber) ").append(filter.getSearchKey())
                .append(" OR LOWER(c.type) ").append(filter.getSearchKey())
                .append(" OR LOWER(c.material) ").append(filter.getSearchKey())
                .append(" OR LOWER(c.specialMark) ").append(filter.getSearchKey())
                .append(" OR LOWER(c.comment) ").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        // Required parameter
        query.setParameter("companyId", filter.getCompanyId());

        // Optional parameters
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
