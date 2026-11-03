package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.DeviceSuspendHistory;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;
import uz.uzkassa.cts.repository.custom.DeviceSuspendHistoryRepositoryCustom;

@SuppressWarnings("unused")
public class DeviceSuspendHistoryRepositoryImpl extends BaseRepositoryImpl implements DeviceSuspendHistoryRepositoryCustom {

    @Override
    public ResultList<DeviceSuspendHistory> getResultList(DeviceSuspendHistoryFilter filter) {
        ResultList<DeviceSuspendHistory> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select d from DeviceSuspendHistory d");
        sql.append(" join d.customerBranch cb");
        sql.append(" join d.contract con");
        sql.append(" join con.customer cus");
        sql.append(" where d.deleted = false");

        if (filter.isBlockedBySystem() || filter.isCurrentBlocked()) {
            sql.append(" and d.dueDate is null");
        }
        if (filter.isBlockedBySystem()) {
            sql.append(" and d.creatorId is null");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and cb.id = :branchId");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cb.address.regionId = :regionId");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and cb.address.districtId = :districtId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and d.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and d.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cus.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(cus.oldTin) ").append(filter.getLikeSearch());
            sql.append(" or lower(d.deviceSerialNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select d", " select count(d.id)");
        sql.append(" order by d.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<DeviceSuspendHistory> query = entityManager
                .createQuery(sql.toString(), DeviceSuspendHistory.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            query.setParameter("districtId", filter.getDistrictId());
            countQuery.setParameter("districtId", filter.getDistrictId());
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
