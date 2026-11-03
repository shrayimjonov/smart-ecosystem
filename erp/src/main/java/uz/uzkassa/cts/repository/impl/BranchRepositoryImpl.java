package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.BranchRepositoryCustom;

@SuppressWarnings("unused")
public class BranchRepositoryImpl extends BaseRepositoryImpl implements BranchRepositoryCustom {

    @Override
    public ResultList<Branch> getResultList(BaseFilter filter) {
        ResultList<Branch> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select b from Branch b ");
        sql.append(" where b.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and b.status =:status ");
        }
        if (filter.getParentId() != null) {
            sql.append(" and b.parentId =:parentId ");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.companyId =:companyId ");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and b.companyId =:companyId ");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and b.baseAddress.regionId =:regionId ");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and b.baseAddress.districtId =:districtId ");
        }
        if (filter.getNeighborhoodId() != null) {
            sql.append(" and b.baseAddress.neighborhoodId =:neighborhoodId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(b.name) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select b", " select count(b.id)");
        sql.append(" order by b.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Branch> query = entityManager
                .createQuery(sql.toString(), Branch.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.getParentId() != null) {
            query.setParameter("parentId", filter.getParentId());
            countQuery.setParameter("parentId", filter.getParentId());
        }
        if (!isSuperAdmin()) {
            query.setParameter("companyId", getCompanyId());
            countQuery.setParameter("companyId", getCompanyId());
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            query.setParameter("districtId", filter.getDistrictId());
            countQuery.setParameter("districtId", filter.getDistrictId());
        }
        if (filter.getNeighborhoodId() != null) {
            query.setParameter("neighborhoodId", filter.getNeighborhoodId());
            countQuery.setParameter("neighborhoodId", filter.getNeighborhoodId());
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
