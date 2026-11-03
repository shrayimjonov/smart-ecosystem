package uz.uzkassa.smartpossupply.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.notification.Notification;
import uz.uzkassa.smartpossupply.enums.ApplicationType;
import uz.uzkassa.smartpossupply.filters.NotificationFilter;
import uz.uzkassa.smartpossupply.repository.NotificationRepositoryCustom;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 17:31
 */
public class NotificationRepositoryImpl
        extends BaseFilterableRepositoryImpl<Notification, NotificationFilter>
        implements NotificationRepositoryCustom {

    @Override
    protected Class<Notification> getEntityClass() {
        return Notification.class;
    }

    @Override
    protected String getEntityAlias() {
        return "n";
    }

    @Override
    public void applyFilter(NotificationFilter filter) {
        filter.setCompanyId(getCompanyId(filter));
    }

    @Override
    public String getBaseQuery(NotificationFilter filter, boolean includeFetch) {
        StringBuilder sql = new StringBuilder(" FROM Notification n ");

        sql.append("WHERE n.companyId = :companyId ");

        appendCondition(sql, StringUtils.hasText(filter.getApplicationType()),
                "AND n.applicationType = :applicationType ");

        if (filter.getRead() != null) {
            if (filter.getRead()) {
                sql.append("AND EXISTS (")
                        .append("SELECT 1 FROM NotificationRead nr ")
                        .append("WHERE nr.notificationId = n.id ")
                        .append("AND nr.userId = :userId")
                        .append(") ");
            } else {
                sql.append("AND NOT EXISTS (")
                        .append("SELECT 1 FROM NotificationRead nr ")
                        .append("WHERE nr.notificationId = n.id ")
                        .append("AND nr.userId = :userId")
                        .append(") ");
            }
        }

        if (filter.isNotEmpty()) {
            sql.append("AND (")
                    .append("LOWER(n.subjectUz) LIKE :searchKey ")
                    .append("OR LOWER(n.subjectRu) LIKE :searchKey ")
                    .append("OR LOWER(n.subjectCyrillic) LIKE :searchKey")
                    .append(") ");
        }

        return sql.toString();
    }

    @Override
    public void setParameters(TypedQuery<?> query, NotificationFilter filter) {
        query.setParameter("companyId", filter.getCompanyId());

        if (StringUtils.hasText(filter.getApplicationType())) {
            query.setParameter("applicationType",
                    ApplicationType.valueOf(filter.getApplicationType()));
        }

        if (filter.getRead() != null) {
            query.setParameter("userId", SecurityUtils.getCurrentUserId());
        }

        if (filter.isNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchValue());
        }
    }

    @Override
    public Long getCount(NotificationFilter filter) {
        applyFilter(filter);
        return super.getCount(filter, entityManager);
    }
}
