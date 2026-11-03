package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.order.Reservation;
import uz.uzkassa.horeca.enums.ReservationStatus;
import uz.uzkassa.horeca.filters.ReservationFilter;
import uz.uzkassa.horeca.repository.ReservationRepositoryCustom;

import java.util.List;

public class ReservationRepositoryCustomImpl extends BaseRepositoryImpl implements ReservationRepositoryCustom {

    @Override
    public Page<Reservation> getResultList(ReservationFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select r from Reservation r ");
        sql.append(" join r.client c ");
        sql.append(" where r.deleted = false ");
        sql.append(" and r.branchId=:branchId");

        if (filter.getClientId() != null) {
            sql.append(" and r.clientId=:clientId");
        }
        if (filter.getTableId() != null) {
            sql.append(" and r.tableId=:tableId");
        }
        if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            sql.append(" and r.status in (:statuses)");
        } else if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and r.status=:status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and r.fromDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and r.fromDate<=:toDate");
        }
        if (filter.hasSearch()) {
            sql.append(" and(");
            sql.append(" lower(c.phone)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.login)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.firstName)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.lastName)").append(filter.getLikeSearchKey());
            sql.append(" or lower(r.reservationNumber)").append(filter.getLikeSearchKey());
            sql.append(" )");
        }
        String countSql = sql.toString().replaceFirst("select r", "select count(r.id)");
        sql.append(" order by r.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Reservation> query = entityManager
            .createQuery(sql.toString(), Reservation.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("branchId", filter.getBranchId());
        countQuery.setParameter("branchId", filter.getBranchId());

        if (filter.getClientId() != null) {
            query.setParameter("clientId", filter.getClientId());
            countQuery.setParameter("clientId", filter.getClientId());
        }
        if (filter.getTableId() != null) {
            query.setParameter("tableId", filter.getTableId());
            countQuery.setParameter("tableId", filter.getTableId());
        }
        if (!CollectionUtils.isEmpty(filter.getStatuses())) {
            List<ReservationStatus> statuses = filter.getStatuses().stream().map(ReservationStatus::valueOf).toList();
            query.setParameter("statuses", statuses);
            countQuery.setParameter("statuses", statuses);
        } else if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", ReservationStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", ReservationStatus.valueOf(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
            countQuery.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
            countQuery.setParameter("toDate", filter.getTo());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
            countQuery.setParameter("searchKey", filter.getSearchForQuery());
        }
        return getResultPage(countQuery, query, filter);
    }
}
