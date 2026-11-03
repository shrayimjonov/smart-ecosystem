package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.cts.domain.Company;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CompanyRepositoryCustom;

import java.util.List;

@SuppressWarnings("unused")
public class CompanyRepositoryImpl extends BaseRepositoryImpl implements CompanyRepositoryCustom {

    @Override
    public List<Company> list(BaseFilter filter) {
        String sql = "select c.* \n" +
                getBaseSql(filter) +
                " order by c." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, Company.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        return (List<Company>) listQuery.getResultList();
    }

    @Override
    public ResultList<Company> getResultList(BaseFilter filter) {
        ResultList<Company> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct c.* from company c ");
        if (filter.getRegionId() != null || filter.getDistrictId() != null || filter.getNeighborhoodId() != null) {
            sql.append(" join address a on a.entity_id = c.id and a.entity_type = 'COMPANY' ");
        }
        sql.append(" where c.deleted = false ");
        if (filter.getRegionId() != null) {
            sql.append(" and a.region_id =:regionId ");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and a.district_id =:districtId ");
        }
        if (filter.getNeighborhoodId() != null) {
            sql.append(" and a.neighborhood_id =:neighborhoodId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and c.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and c.created_date>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and c.created_date<=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.brand) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.phone) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.email) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select distinct c", " select count(distinct c.id)");
        countSql = countSql.replace(".*", "");
        sql.append(" order by c.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        Query query = entityManager
                .createNativeQuery(sql.toString(), Company.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        Query countQuery = entityManager.createNativeQuery(countSql, Long.class);

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
            query.setParameter("status", filter.getStatus());
            countQuery.setParameter("status", filter.getStatus());
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
        Long count = (Long) countQuery.getSingleResult();
        if (count > 0) {
            resultList.setList(query.getResultList());
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from company c \n");
        sql.append(" where c.deleted = false ");
        if (filter.getRegionId() != null) {
            sql.append(" and c.region_id = ").append(filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and c.district_id = ").append(filter.getDistrictId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.phone)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.street)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.house)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.apartment)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
