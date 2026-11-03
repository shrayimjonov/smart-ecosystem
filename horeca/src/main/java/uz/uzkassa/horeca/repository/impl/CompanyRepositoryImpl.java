package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.TypedQuery;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.CompanyFilter;
import uz.uzkassa.horeca.repository.CompanyRepositoryCustom;

public class CompanyRepositoryImpl extends BaseRepositoryImpl implements CompanyRepositoryCustom {

    @Override
    public ResultList<Company> getResultList(CompanyFilter filter) {
        ResultList<Company> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" select c from Company c ");
        sql.append(" join c.owner o");
        sql.append(" where c.deleted = false and o.deleted = false ");
        sql.append(" and c.tin is not null ");

        if (filter.getStatus() != null) {
            sql.append(" and c.status=:status ");
        }
        if (filter.getActivityTypeId() != null) {
            sql.append(" and c.activityTypeId=:activityTypeId ");
        }
        if (filter.getBusinessType() != null) {
            sql.append(" and c.businessType=:businessType ");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and c.address.regionId=:regionId ");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and c.address.districtId=:districtId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and c.registrationDate>=:fromDate ");
        }
        if (filter.getTo() != null) {
            sql.append(" and c.registrationDate<=:toDate ");
        }
        if (filter.hasSearch()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin) ").append(filter.getLikeSearchKey());
            sql.append(" or lower(o.phone) ").append(filter.getLikeSearchKey());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select c", "select count(c.id)");

        sql.append(" order by c.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Company> query = entityManager
            .createQuery(sql.toString(), Company.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getStatus() != null) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.getActivityTypeId() != null) {
            query.setParameter("activityTypeId", filter.getActivityTypeId());
            countQuery.setParameter("activityTypeId", filter.getActivityTypeId());
        }
        if (filter.getBusinessType() != null) {
            query.setParameter("businessType", filter.getBusinessType());
            countQuery.setParameter("businessType", filter.getBusinessType());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            query.setParameter("districtId", filter.getDistrictId());
            countQuery.setParameter("districtId", filter.getDistrictId());
        }
        if (filter.getDistrictId() != null) {
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

        Long listCount = countQuery.getSingleResult();
        if (listCount > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(listCount);
        }
        return resultList;
    }
}
