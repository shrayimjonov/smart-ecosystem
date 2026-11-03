package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.FiscalModuleReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.FiscalModuleReportFilter;
import uz.uzkassa.cts.repository.custom.FiscalModuleReportRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.DateUtil;

/**
 * Created date time: 06.03.2025 10:55
 */

@SuppressWarnings("unused")
public class FiscalModuleReportRepositoryImpl extends BaseRepositoryImpl implements FiscalModuleReportRepositoryCustom {

    @Override
    public ResultList<FiscalModuleReport> getResultList(FiscalModuleReportFilter filter) {
        ResultList<FiscalModuleReport> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select fmr from FiscalModuleReport fmr");
        sql.append(" join fmr.cases c");
        sql.append(" join fmr.customer cus");
        sql.append(" where fmr.deleted = false");

        if (!SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode()) || filter.getBranchId() != null) {
            sql.append(" and c.branchId = :branchId");
        }
        if (!CollectionUtils.isEmpty(filter.getCaseCreatorIds())) {
            sql.append(" and c.creatorId in (:caseCreatorIds)");
        }
        if (filter.getArrivalStatus() != null) {
            sql.append(" and fmr.arrivalStatus = :arrivalStatus");
        }
        if (filter.getReason() != null) {
            sql.append(" and fmr.reason = :reason");
        }
        if (filter.getCaseType() != null) {
            sql.append(" and c.caseType = :caseType");
        }
        if (filter.getFrom() != null) {
            sql.append(" and fmr.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and fmr.createdDate <= :toDate");
        }
        if (filter.getArrivalFrom() != null) {
            sql.append(" and fmr.arrivalDate >= :fromArrivalDate");
        }
        if (filter.getArrivalTo() != null) {
            sql.append(" and fmr.arrivalDate <= :toArrivalDate");
        }
        if (!CollectionUtils.isEmpty(filter.getRegionIds())) {
            sql.append(" and cus.address.regionId in (:regionIds)");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cus.tin)").append(filter.getLikeSearch());
            sql.append(" or lower(cus.name)").append(filter.getLikeSearch());
            sql.append(" or lower(cus.companyName)").append(filter.getLikeSearch());
            sql.append(" or lower(fmr.deviceSerialNumber)").append(filter.getLikeSearch());
            sql.append(" or lower(fmr.fmSerialNumber)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select fmr", " select count(fmr.id)");
        sql.append(" order by fmr.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<FiscalModuleReport> query = entityManager
                .createQuery(sql.toString(), FiscalModuleReport.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (!SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode()) || filter.getBranchId() != null) {
            query.setParameter("branchId", !SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode()) ? SecurityUtils.getBranchId() : filter.getBranchId());
            countQuery.setParameter("branchId", !SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode()) ? SecurityUtils.getBranchId() : filter.getBranchId());
        }
        if (!CollectionUtils.isEmpty(filter.getCaseCreatorIds())) {
            query.setParameter("caseCreatorIds", filter.getCaseCreatorIds());
            countQuery.setParameter("caseCreatorIds", filter.getCaseCreatorIds());
        }
        if (filter.getArrivalStatus() != null) {
            query.setParameter("arrivalStatus", filter.getArrivalStatus());
            countQuery.setParameter("arrivalStatus", filter.getArrivalStatus());
        }
        if (filter.getReason() != null) {
            query.setParameter("reason", filter.getReason());
            countQuery.setParameter("reason", filter.getReason());
        }
        if (filter.getCaseType() != null) {
            query.setParameter("caseType", filter.getCaseType());
            countQuery.setParameter("caseType", filter.getCaseType());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.getArrivalFrom() != null) {
            query.setParameter("fromArrivalDate", DateUtil.getFromStartOfDay(filter.getArrivalFrom()));
            countQuery.setParameter("fromArrivalDate", DateUtil.getFromStartOfDay(filter.getArrivalFrom()));
        }
        if (filter.getArrivalTo() != null) {
            query.setParameter("toArrivalDate", DateUtil.getToEndOfDay(filter.getArrivalTo()));
            countQuery.setParameter("toArrivalDate", DateUtil.getToEndOfDay(filter.getArrivalTo()));
        }
        if (!CollectionUtils.isEmpty(filter.getRegionIds())) {
            query.setParameter("regionIds", filter.getRegionIds());
            countQuery.setParameter("regionIds", filter.getRegionIds());
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
