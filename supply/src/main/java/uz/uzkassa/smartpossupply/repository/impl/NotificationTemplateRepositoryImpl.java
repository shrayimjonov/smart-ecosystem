package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.smartpossupply.domain.notification.NotificationTemplate;
import uz.uzkassa.smartpossupply.filters.TemplateFilter;
import uz.uzkassa.smartpossupply.repository.NotificationTemplateRepositoryCustom;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 14:51
 */
public class NotificationTemplateRepositoryImpl
    extends BaseFilterableRepositoryImpl<NotificationTemplate, TemplateFilter>
    implements NotificationTemplateRepositoryCustom {

    @Override
    protected Class<NotificationTemplate> getEntityClass() {
        return NotificationTemplate.class;
    }

    @Override
    protected String getEntityAlias() {
        return "t";
    }

    @Override
    public void applyFilter(TemplateFilter filter) {
        // No custom filter logic needed for this repository
    }

    @Override
    public String getBaseQuery(TemplateFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM NotificationTemplate t ");

        // Base condition
        sql.append("WHERE 1=1 ");

        // Dynamic conditions
        appendCondition(sql, filter.getTemplateType() != null, "AND t.templateType = :templateType ");
        appendCondition(sql, filter.getSendType() != null, "AND t.sendType = :sendType ");
        appendCondition(sql, filter.getApplicationType() != null, "AND t.applicationType = :applicationType ");

        // Search conditions across multiple language fields
        if (filter.isNotEmpty()) {
            sql.append("AND (")
                .append("LOWER(t.subjectUz)").append(filter.getSearchKey())
                .append(" OR LOWER(t.subjectRu)").append(filter.getSearchKey())
                .append(" OR LOWER(t.subjectCyrillic)").append(filter.getSearchKey())
                .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, TemplateFilter filter) {
        // Optional parameters
        setParameterIfNotNull(query, "templateType", filter.getTemplateType());
        setParameterIfNotNull(query, "sendType", filter.getSendType());
        setParameterIfNotNull(query, "applicationType", filter.getApplicationType());

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }
}
