package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.OfdApplication;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.tin.integration.OfdApplicationMakeObjectListDTO;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.OfdApplicationFilter;
import uz.uzkassa.cts.repository.custom.OfdApplicationRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.QueryUtils;

import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
public class OfdApplicationRepositoryImpl extends BaseRepositoryImpl implements OfdApplicationRepositoryCustom {


    @Override
    public ResultList<OfdApplicationMakeObjectListDTO> getApplications(OfdApplicationFilter filter) {
        ResultList<OfdApplicationMakeObjectListDTO> resultList = new ResultList<>();
        String sql = "select DISTINCT a.*, a.fiscal_module_id as fiscalizationId, fm.serial_number as fiscalModuleSerial, c.tin as tin,d.serial_number as deviceSerial,\n" +
                "c.id as cusId, c.name as cusName,\n" +
                "cb.id as cbrId, cb.name as cbrName, \n" +
                "b.id as brId, b.name as brName \n" +
                getApplicationBaseSql(filter) +
                " order by a." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<OfdApplicationMakeObjectListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(OfdApplicationMakeObjectListDTO.class));

        String countSql = "select count(DISTINCT a.id) \n" +
                getApplicationBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getApplicationBaseSql(OfdApplicationFilter filter) {
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());
        StringBuilder sql = new StringBuilder();
        sql.append(" from ofd_application a \n");
        sql.append(" inner join customer c on c.id = a.customer_id \n");
        sql.append(" inner join branch b on b.id = c.branch_id \n");
        sql.append(" inner join customer_branches cbs on cbs.customer_id = c.id \n");
        sql.append(" inner join device d on d.id = a.device_id \n");
        sql.append(" inner join fiscal_module fm on fm.id = a.fiscal_module_id \n");
        sql.append(" inner join customer_branch cb on cb.id = a.customer_branch_id \n");
        sql.append(" where a.deleted=false and c.deleted=false and d.deleted=false and fm.deleted=false \n");
        if (!SecurityUtils.isCurrentUserInRoles(Role.CALL_CENTER.getRoleCode())) {
            if (!(SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode()))) {
                sql.append(" and (c.creator_id =").append(SecurityUtils.getCurrentUserId());
                sql.append(" or c.last_modifier_id =").append(SecurityUtils.getCurrentUserId());
                sql.append(" or cbs.branch_id =").append(SecurityUtils.getBranchId());
                sql.append(") \n");
            }
        }

        if (StringUtils.isNotEmpty(filter.getOfdStatus())) {
            sql.append(" and a.ofd_status ='").append(OfdStatus.valueOf(filter.getOfdStatus())).append("'\n");
        }
        if (StringUtils.isNotEmpty(filter.getApplicatonType())) {
            sql.append(" and a.application_type ='").append(ApplicationType.valueOf(filter.getApplicatonType())).append("'\n");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and c.region_id =").append(filter.getRegionId()).append("\n");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and c.district_id =").append(filter.getDistrictId()).append("\n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and c.branch_id =").append(filter.getBranchId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and (a.created_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.phone)").append(filter.getLikeSearchKey());
            if (!SecurityUtils.isCurrentUserInRole(Role.CALL_CENTER.getRoleCode())) {
                sql.append(" or lower(c.name)").append(filter.getLikeSearchKey());
                sql.append(" or lower(a.ofd_status)").append(filter.getLikeSearchKey());
                sql.append(" or lower(c.company_name)").append(filter.getLikeSearchKey());
                sql.append(" or lower(d.serial_number)").append(filter.getLikeSearchKey());
                sql.append(" or lower(fm.serial_number)").append(filter.getLikeSearchKey());
            }
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<OfdApplication> getResultList(OfdApplicationFilter filter) {
        ResultList<OfdApplication> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select a from OfdApplication a ");
        sql.append(" join a.customer c ");
        sql.append(" join a.device d ");
        sql.append(" join a.fiscalModule fm ");
        sql.append(" where a.deleted = false ");

//        if (!SecurityUtils.isCurrentUserInRoles(Role.CALL_CENTER.getRoleCode())) {
//            if (!(SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode()))) {
//                sql.append(" and (c.creatorId =").append(SecurityUtils.getCurrentUserId());
//                sql.append(" or c.lastModifierId =").append(SecurityUtils.getCurrentUserId());
//                sql.append(") ");
//            }
//        }

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and a.ofdStatus =:status ");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and a.applicationType =:applicationType ");
        }
        if (!CollectionUtils.isEmpty(filter.getCustomerIds())) {
            sql.append(" and c.id in (:customerIds) ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and c.branchId =:branchId ");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and c.address.regionId =:regionId ");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and c.address.districtId =:districtId ");
        }
        if (filter.getNeighborhoodId() != null) {
            sql.append(" and c.address.neighborhoodId =:neighborhoodId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and a.createdDate >=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and a.createdDate <=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.phone) ").append(filter.getLikeSearch());
            if (!SecurityUtils.isCurrentUserInRole(Role.CALL_CENTER.getRoleCode())) {
                sql.append(" or lower(c.name) ").append(filter.getLikeSearch());
                sql.append(" or lower(d.serialNumber) ").append(filter.getLikeSearch());
                sql.append(" or lower(fm.serialNumber) ").append(filter.getLikeSearch());
            }
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select a", " select count(a.id)");
        sql.append(" order by a.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<OfdApplication> query = entityManager
                .createQuery(sql.toString(), OfdApplication.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", OfdStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", OfdStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            query.setParameter("applicationType", ApplicationType.valueOf(filter.getType()));
            countQuery.setParameter("applicationType", ApplicationType.valueOf(filter.getType()));
        }
        if (!CollectionUtils.isEmpty(filter.getCustomerIds())) {
            query.setParameter("customerIds", filter.getCustomerIds());
            countQuery.setParameter("customerIds", filter.getCustomerIds());
        }
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
        if (filter.getNeighborhoodId() != null) {
            query.setParameter("neighborhoodId", filter.getNeighborhoodId());
            countQuery.setParameter("neighborhoodId", filter.getNeighborhoodId());
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
