package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.Notification;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.NotificationFilter;
import uz.uzkassa.cts.repository.custom.NotificationRepositoryCustom;

@SuppressWarnings("unused")
public class NotificationRepositoryImpl extends BaseRepositoryImpl implements NotificationRepositoryCustom {

    @Override
    public ResultList<Notification> getResultList(NotificationFilter filter) {
        ResultList<Notification> resultList = new ResultList<>();
        Long companyId = getCompanyId(filter.getCompanyId());
        StringBuilder sql = new StringBuilder();
        sql.append("select n from Notification n");
        if (filter.isUnreadFirst()) {
            sql.append(" left join NotificationRead nr on nr.notificationId = n.id");
        }
        sql.append(" where n.deleted = false");
        sql.append(" and n.companyId =:companyId");
        if (filter.getEntityId() != null) {
            sql.append(" and n.entityId = :entityId");
        }
        if (filter.getEntityType() != null) {
            sql.append(" and n.entityType = :entityType");
        }
        if (filter.getReceiverId() != null) {
            sql.append(" and n.receiverId = :receiverId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and n.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and n.createdDate <= :toDate");
        }
        if (filter.getFrom() != null) {
            sql.append(" and n.createdDate >= :fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and n.createdDate <= :toDate ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(n.messageUzLatin)").append(filter.getLikeSearch());
            sql.append(" or lower(n.messageUzCyrillic)").append(filter.getLikeSearch());
            sql.append(" or lower(n.messageRu)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select n", " select count(n.id)");
        sql.append(" order by ").append(filter.isUnreadFirst() ? "nr.id desc, n." : "n.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Notification> query = entityManager
                .createQuery(sql.toString(), Notification.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", companyId);
        countQuery.setParameter("companyId", companyId);

        if (filter.getEntityId() != null) {
            query.setParameter("entityId", filter.getEntityId());
            countQuery.setParameter("entityId", filter.getEntityId());
        }
        if (filter.getEntityType() != null) {
            query.setParameter("entityType", filter.getEntityType());
            countQuery.setParameter("entityType", filter.getEntityType());
        }
        if (filter.getReceiverId() != null) {
            query.setParameter("receiverId", filter.getReceiverId());
            countQuery.setParameter("receiverId", filter.getReceiverId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
            countQuery.setParameter("searchKey", filter.getSearchFor());
        }
        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }
}
