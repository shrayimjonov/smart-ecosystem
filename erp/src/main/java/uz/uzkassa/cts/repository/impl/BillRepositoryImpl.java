package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.cts.domain.Bill;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.filter.BillFilter;
import uz.uzkassa.cts.repository.custom.BillRepositoryCustom;

@SuppressWarnings("unused")
public class BillRepositoryImpl extends BaseRepositoryImpl implements BillRepositoryCustom {

    @Override
    public ResultList<Bill> getResultList(BillFilter filter) {
        ResultList<Bill> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select b from Bill b \n");
        sql.append("inner join b.customer c \n");
        sql.append("inner join b.device d \n");
        sql.append(" where b.deleted = false ");

        if (filter.getBranchId() != null) {
            sql.append(" and b.branchId =:branchId ");
        }
        if (filter.getBillId() != null) {
            sql.append(" and b.billId =:billId ");
        }
        if (filter.getAPayStatus() != null) {
            sql.append(" and b.payStatus =:payStatus ");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and c.address.regionId =:regionId ");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and b.creatorId =:creatorId ");
        }
        if (filter.getModelId() != null) {
            sql.append(" and d.modelId =:modelId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and b.payTime >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and b.payTime <= :toDate");
        }
        if (filter.getDeviceType() != null) {
            sql.append(" and b.deviceType =:deviceType ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.companyName) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(d.serialNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select b", " select count(b.id)");
        sql.append(" order by b.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Bill> query = entityManager
                .createQuery(sql.toString(), Bill.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getBillId() != null) {
            query.setParameter("billId", filter.getBillId());
            countQuery.setParameter("billId", filter.getBillId());
        }
        if (filter.getAPayStatus() != null) {
            query.setParameter("payStatus", filter.getAPayStatus());
            countQuery.setParameter("payStatus", filter.getAPayStatus());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
        }
        if (filter.getModelId() != null) {
            query.setParameter("modelId", filter.getModelId());
            countQuery.setParameter("modelId", filter.getModelId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.getDeviceType() != null) {
            query.setParameter("deviceType", filter.getDeviceType());
            countQuery.setParameter("deviceType", filter.getDeviceType());
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
