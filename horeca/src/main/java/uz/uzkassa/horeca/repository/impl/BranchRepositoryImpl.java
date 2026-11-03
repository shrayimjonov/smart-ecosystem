package uz.uzkassa.horeca.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import uz.uzkassa.horeca.domain.branch.Branch;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.base.ResultList;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.BranchRepositoryCustom;

import java.util.Collections;
import java.util.List;

public class BranchRepositoryImpl extends BaseRepositoryImpl implements BranchRepositoryCustom {

    @Override
    public List<CommonDTO> getLookup(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select new CommonDTO(b.id, b.name) from Branch b ");
        sql.append(" join b.company c ");
        sql.append(" where b.deleted = false ");
        sql.append(" and b.companyId =:companyId ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and b.status =:status");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and b.address.regionId =:regionId ");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and b.address.districtId =:districtId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and b.createdDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and b.createdDate <=:toDate ");
        }
        if (filter.hasSearch()) {
            sql.append(" and ( ");
            sql.append(" lower(b.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        sql.append(" order by b.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CommonDTO> query = entityManager
            .createQuery(sql.toString(), CommonDTO.class)
            .setFirstResult(filter.getStart())
            .setMaxResults(filter.getSize());

        query.setParameter("companyId", filter.getCompanyId());

        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            query.setParameter("districtId", filter.getDistrictId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFrom());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getTo());
        }
        if (filter.hasSearch()) {
            query.setParameter("searchKey", filter.getSearchForQuery());
        }

        return query.getResultList();
    }

    @Override
    public Page<Branch> getResultList(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select b from Branch b ");
        sql.append(" join b.company c ");
        sql.append(" where b.deleted = false ");
        sql.append(" and b.companyId =:companyId ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and b.status =:status");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and b.address.regionId =:regionId ");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and b.address.districtId =:districtId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and b.createdDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and b.createdDate <=:toDate ");
        }
        if (filter.hasSearch()) {
            sql.append(" and ( ");
            sql.append(" lower(b.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        String countSql = sql.toString().replaceFirst("select b", "select count(b.id)");

        sql.append(" order by b.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Branch> query = entityManager
                .createQuery(sql.toString(), Branch.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        query.setParameter("companyId", filter.getCompanyId());
        countQuery.setParameter("companyId", filter.getCompanyId());

        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            query.setParameter("districtId", filter.getDistrictId());
            countQuery.setParameter("districtId", filter.getDistrictId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CommonStatus.valueOf(filter.getStatus()));
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
