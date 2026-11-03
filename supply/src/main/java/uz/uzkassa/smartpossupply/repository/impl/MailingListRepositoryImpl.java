package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.mailing.MailingList;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.repository.MailingListRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

@SuppressWarnings("unused")
public class MailingListRepositoryImpl extends BaseFilterableRepositoryImpl<MailingList, BaseFilter> implements MailingListRepositoryCustom {

    @Override
    protected Class<MailingList> getEntityClass() {
        return MailingList.class;
    }

    @Override
    protected String getEntityAlias() {
        return "ml";
    }

    @Override
    public void applyFilter(BaseFilter filter) {
    }

    @Override
    public String getBaseQuery(BaseFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM MailingList ml ");
        sql.append("JOIN ml.company c ");
        // Base condition
        sql.append("WHERE ml.deleted = false ");
        if (!SecurityUtils.isFromAdmin()){
            sql.append(" AND c.id = :companyId");
        }

        // Search conditions across multiple fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(ml.name)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, BaseFilter filter) {
        if (!SecurityUtils.isFromAdmin()){
            query.setParameter("companyId", getCompanyId(filter));
        }
        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
