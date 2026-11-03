package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.inventory.Warehouse;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.WarehouseRepositoryCustom;

@SuppressWarnings("unused")
public class WarehouseRepositoryImpl extends BaseRepositoryImpl implements WarehouseRepositoryCustom {

    @Override
    public ResultList<Warehouse> getResultList(BaseFilter filter) {
        ResultList<Warehouse> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select w from Warehouse w ");
        sql.append(" join w.branch b ");
        sql.append(" where w.deleted = false ");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.companyId =:companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and w.branchId =:branchId ");
        }
        if (filter.getDepartmentId() != null) {
            sql.append(" and w.departmentId =:departmentId ");
        }
        if (filter.getManagerId() != null) {
            sql.append(" and w.managerId =:managerId ");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and w.address.regionId =:regionId ");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and w.address.districtId =:districtId ");
        }
        if (filter.getNeighborhoodId() != null) {
            sql.append(" and w.address.neighborhoodId =:neighborhoodId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and w.status =:status ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(w.name) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select w", " select count(w.id)");
        sql.append(" order by w.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Warehouse> query = entityManager
                .createQuery(sql.toString(), Warehouse.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getDepartmentId() != null) {
            query.setParameter("departmentId", filter.getDepartmentId());
            countQuery.setParameter("departmentId", filter.getDepartmentId());
        }
        if (filter.getManagerId() != null) {
            query.setParameter("managerId", filter.getManagerId());
            countQuery.setParameter("managerId", filter.getManagerId());
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
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
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
