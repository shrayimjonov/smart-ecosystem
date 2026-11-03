package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.domain.Branch;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customer.*;
import uz.uzkassa.cts.dto.payload.FilterPayload;
import uz.uzkassa.cts.dto.sync.SyncCustomerBranchListDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyCustomerBranchFilter;
import uz.uzkassa.cts.filter.CustomerBranchFilter;
import uz.uzkassa.cts.repository.custom.CustomerBranchRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.QueryUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@SuppressWarnings("unused")
public class CustomerBranchRepositoryImpl extends BaseRepositoryImpl implements CustomerBranchRepositoryCustom {

    @Override
    public ResultList<CustomerBranch> getResultList(CustomerBranchFilter filter) {
        ResultList<CustomerBranch> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cb from CustomerBranch cb ");
        sql.append(" join cb.customer c ");
        if (filter.getHasFiscal() != null && filter.getHasFiscal()) {
            sql.append(" join Fiscalization f on cb.id = f.customerBranchId");
        }
        sql.append(" where cb.deleted = false ");
        if (filter.getHasFiscal() != null && filter.getHasFiscal()) {
            sql.append(" and f.status in ('FISCAL', 'FISCAL_ACTIVE')");
        }
        if (filter.getConfirmed() != null && filter.getConfirmed()) {
            sql.append(" and cb.ofdId is not null ");
        }
        if (StringUtils.isNotEmpty(filter.getTin())) {
            sql.append(" and c.tin =:tin ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cb.status =:status ");
        }
        if (StringUtils.isNotEmpty(filter.getOfdStatus())) {
            sql.append(" and cb.ofdStatus =:ofdStatus ");
        }
        if (filter.getParentActivityTypeId() != null) {
            sql.append(" and cb.activityType.parent.id =:parentActivityTypeId ");
        }
        if (filter.getActivityTypeId() != null) {
            sql.append(" and cb.activityTypeId =:activityTypeId ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and cb.branchId =:branchId ");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and cb.customerId =:customerId ");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cb.address.regionId =:regionId ");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and cb.address.districtId =:districtId ");
        }
        if (!CollectionUtils.isEmpty(filter.getDistrictIds())) {
            sql.append(" and cb.address.districtId in (:districtIds)");
        }
        if (filter.getNeighborhoodId() != null) {
            sql.append(" and cb.address.neighborhoodId =:neighborhoodId ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cb.createdDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cb.createdDate<=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cb.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(cb.brand) ").append(filter.getLikeSearch());
            sql.append(" or lower(cb.phone) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cb", " select count(cb.id)");
        sql.append(" order by cb.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CustomerBranch> query = entityManager
                .createQuery(sql.toString(), CustomerBranch.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getTin())) {
            query.setParameter("tin", filter.getTin());
            countQuery.setParameter("tin", filter.getTin());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CustomerBranchStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CustomerBranchStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getOfdStatus())) {
            query.setParameter("ofdStatus", OfdStatus.valueOf(filter.getOfdStatus()));
            countQuery.setParameter("ofdStatus", OfdStatus.valueOf(filter.getOfdStatus()));
        }
        if (filter.getParentActivityTypeId() != null) {
            query.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
            countQuery.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
        }
        if (filter.getParentActivityTypeId() != null) {
            query.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
            countQuery.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
        }
        if (filter.getActivityTypeId() != null) {
            query.setParameter("activityTypeId", filter.getActivityTypeId());
            countQuery.setParameter("activityTypeId", filter.getActivityTypeId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getRegionId() != null) {
            query.setParameter("regionId", filter.getRegionId());
            countQuery.setParameter("regionId", filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            query.setParameter("districtId", filter.getDistrictId());
            countQuery.setParameter("districtId", filter.getDistrictId());
        }
        if (!CollectionUtils.isEmpty(filter.getDistrictIds())) {
            query.setParameter("districtIds", filter.getDistrictIds());
            countQuery.setParameter("districtIds", filter.getDistrictIds());
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

    @Override
    public ResultList<CustomerBranch> getList(CustomerBranchFilter filter, Branch branch) {
        ResultList<CustomerBranch> resultList = new ResultList<>();
        String sql = "select distinct cb.* \n" +
                getBaseSql(filter, branch) +
                " order by cb." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, CustomerBranch.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        List<CustomerBranch> list = listQuery.getResultList();

        String countSql = "select count(distinct cb.id) \n" +
                getBaseSql(filter, branch);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(CustomerBranchFilter filter, Branch branch) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer_branch cb \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" inner join customer_branches cbs on cbs.customer_id = c.id");
        sql.append(" where cb.deleted = false \n");
        sql.append(" and cb.customer_id = ").append(filter.getCustomerId());

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (branch != null && !(SecurityUtils.isCurrentUserInRoles(Role.getAdminRoles().toArray(new String[]{})))) {
            sql.append(" and (");
            sql.append(" cbs.branch_id in(").append(branch.getId()).append(")");
            sql.append(" or cb.region_id =").append(branch.getAddress().getRegionId());
            sql.append(")");
        }
        if (filter.getHasAct() != null) {
            sql.append(" and cb.has_act = ").append(filter.getHasAct().booleanValue());
        }
        if (filter.getHasInvoicePayment() != null) {
            sql.append(" and cb.has_invoice_payment = ").append(filter.getHasInvoicePayment().booleanValue());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(cb.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.company_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<CustomerBranchListMapDTO> listForMap(CompanyCustomerBranchFilter filter) {
        String sql = "select cb.id as id," +
                " cb.name as name," +
                " cb.brand as brand," +
                " cb.phone as phone," +
                " cb.longitude as longitude," +
                " cb.latitude as latitude," +
                " c.name as customer," +
                " c.tin as tin," +
                " act.name_ru as activityType \n" +
                getListBaseSql(filter);
        Query query = entityManager.createNativeQuery(sql, "CustomerBranchListMapMapper");

        return (List<CustomerBranchListMapDTO>) query.getResultList();
    }

    @Override
    public ResultList<CustomerBranch> getAllList(CompanyCustomerBranchFilter filter) {
        ResultList<CustomerBranch> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cb.* \n");
        sql.append(getListBaseSql(filter));
        sql.append(" order by ");
        if ("registrationDate".equals(filter.getOrderBy())) {
            sql.append(" cb.created_date ");
        } else {
            sql.append(" cb.").append(filter.getOrderBy());
        }
        sql.append(" ").append(filter.getSortOrder().getName());
        Query listQuery = entityManager.createNativeQuery(sql.toString(), CustomerBranch.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        List<CustomerBranch> list = listQuery.getResultList();

        String countSql = "select count(cb.id) \n" +
                getListBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getListBaseSql(CompanyCustomerBranchFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer_branch cb \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" inner join activity_type act on act.id = cb.activity_type_id \n");
        sql.append(" where cb.deleted = false \n");
        sql.append(" and c.deleted = false \n");
        if (filter.getCustomerId() != null) {
            sql.append(" and c.id = ").append(filter.getCustomerId());
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append(" and cb.created_date between '").append(filter.getFromStartOfDay()).append("' and '").append(filter.getToEndOfDay()).append("' \n");
        }
        if (filter.getStatus() != null) {
            sql.append(" and cb.status = '").append(CustomerBranchStatus.get(filter.getStatus())).append("' \n");
        }
        if (filter.getOfdStatus() != null) {
            sql.append(" and cb.ofd_status = '").append(OfdStatus.get(filter.getOfdStatus())).append("' \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and c.branch_id = ").append(filter.getBranchId());
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cb.region_id = ").append(filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and cb.district_id = ").append(filter.getDistrictId());
        }
        if (filter.getHasAct() != null) {
            sql.append(" and cb.has_act = ").append(filter.getHasAct().booleanValue());
        }
        if (filter.getTin() != null) {
            sql.append(" and c.tin = '").append(filter.getTin()).append("' \n");
        }
        if (filter.getHasInvoicePayment() != null) {
            sql.append(" and cb.has_invoice_payment = ").append(filter.getHasInvoicePayment().booleanValue());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(cb.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<AdminCustomerBranchListByFiscalDevicesDTO> getAllByFiscalDevices(CompanyCustomerBranchFilter filter) {
        ResultList<AdminCustomerBranchListByFiscalDevicesDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT cb.id, cb.name, cb.phone, cb.status, cb.ofd_status, cb.ofd_id ofdId, cb.latitude, cb.longitude, cb.street, cb.house, cb.apartment, \n");
        sql.append("cb.cadastre_number cadastreNumber, cb.has_act hasAct, cb.has_invoice_payment hasInvoicePayment, cb.created_date registrationDate, \n");
        sql.append("c.id AS cusId, c.name AS cusName, c.tin As tin, \n");
        sql.append("b.id AS brId, b.name AS brName, \n");
        sql.append("r.id regId, r.name_ru regNameRu, r.name_uz_cyrillic regNameUzCyrillic, r.name_uz_latin regNameUzLatin, r.code regCode, \n");
        sql.append("d.id disId, d.name_ru disNameRu, d.name_uz_cyrillic disNameUzCyrillic, d.name_uz_latin disNameUzLatin, d.code disCode \n");
        sql.append(getListByFiscalDevicesBaseSql(filter));
        sql.append(" order by ");

        if ("registrationDate".equals(filter.getOrderBy())) {
            sql.append(" cb.created_date ");
        } else {
            sql.append(" cb.").append(filter.getOrderBy());
        }
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));
        List<AdminCustomerBranchListByFiscalDevicesDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(),
                BeanPropertyRowMapper.newInstance(AdminCustomerBranchListByFiscalDevicesDTO.class));

        String countSql = "select count(cb.id) \n" +
                getListByFiscalDevicesBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);
        long count = ((Long) countQuery.getSingleResult());

        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getListByFiscalDevicesBaseSql(CompanyCustomerBranchFilter filter) {
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());

        StringBuilder sql = new StringBuilder();
        sql.append(" from customer_branch cb \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" inner join customer_branch_device cbd on cbd.customer_branch_id = cb.id \n");
        sql.append(" inner join customer_branch_fiscal_module cbfm on cbfm.customer_branch_id = cb.id \n");
        sql.append(" inner join fiscalization f on f.device_id = cbd.device_id and f.customer_id = c.id and cbfm.fiscal_module_id  = f.fiscal_module_id \n");
        sql.append(" inner join ofd_application a on a.id = f.ofd_application_id \n");
        sql.append(" left join branch b ON b.id = cb.branch_id \n");
        sql.append(" left join region r ON r.id = cb.region_id \n");
        sql.append(" left join district d ON d.id = cb.district_id \n");
        sql.append(" where cb.deleted = false and c.deleted = false and f.deleted = false and cbd.deleted=false and cbfm.deleted=false \n");
        sql.append(" and a.application_type in ('REGISTER','CHANGE_ADDRESS','DEREGISTER_DEVICE','DEREGISTER_FISCAL_MODULE')");
        sql.append(" and a.ofd_status = '").append(OfdStatus.ACCEPT.getCode()).append("' \n");

        if (filter.getCustomerId() != null) {
            sql.append(" and c.id = ").append(filter.getCustomerId());
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and cb.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        if (filter.getStatus() != null) {
            sql.append(" and cb.status = '").append(CustomerBranchStatus.get(filter.getStatus())).append("' \n");
        }
        if (filter.getOfdStatus() != null) {
            sql.append(" and cb.ofd_status = '").append(OfdStatus.get(filter.getOfdStatus())).append("' \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and c.branch_id = ").append(filter.getBranchId());
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cb.region_id = ").append(filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and cb.district_id = ").append(filter.getDistrictId());
        }
        if (filter.getHasAct() != null) {
            sql.append(" and cb.has_act = ").append(filter.getHasAct().booleanValue());
        }
        if (filter.getTin() != null) {
            sql.append(" and (");
            sql.append(" c.tin = '").append(filter.getTin()).append("'");
            sql.append(" or c.old_tin = '").append(filter.getTin()).append("'");
            sql.append(" )");
        }
        if (filter.getHasInvoicePayment() != null) {
            sql.append(" and cb.has_invoice_payment = ").append(filter.getHasInvoicePayment().booleanValue());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(cb.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<CommonDTO> getListForOfd(CustomerBranchFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct cb.id as id, cb.name as name ");
        sql.append(" from customer_branch cb ");
        sql.append(" inner join customer c on c.id = cb.customer_id ");
        sql.append(" inner join file_upload fu on fu.entity_id = cb.id and fu.entity_type='").append(EntityTypeEnum.CUSTOMER_BRANCH.name()).append("' and fu.content_type='").append(MediaType.APPLICATION_PDF_VALUE).append("'");
        sql.append(" where cb.deleted = false ");
        sql.append(" and fu.deleted = false ");
        sql.append(" and cb.customer_id =").append(filter.getCustomerId());
        sql.append(" and cb.longitude is not null ");
        sql.append(" and cb.latitude is not null ");
        sql.append(" and cb.status ='").append(CustomerBranchStatus.ACTIVE.getCode()).append("'");

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id =").append(filter.getCompanyId());
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id =").append(getCompanyId());
        }
        if (filter.getConfirmed() != null && filter.getConfirmed()) {
            sql.append(" and cb.ofd_id is not null");
        } else {
            sql.append(" and (cb.ofd_id is null or cb.resync = true) ");
        }

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(cb.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        sql.append(" order by cb.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(" limit 20 ");

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(CommonDTO.class));
    }

    @Override
    public ResultList<CommonDTO> getLookUp(BaseFilter filter) {
        ResultList<CommonDTO> resultList = new ResultList<>();
        String sql = "select distinct cb.id as id, cb.name as name \n" +
                getBaseLookUpSql(filter) +
                " order by cb." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);
        List<CommonDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CommonDTO.class));

        String countSql = "select count(distinct cb.id) \n" +
                getBaseLookUpSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);
        long count = ((Long) countQuery.getSingleResult());

        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseLookUpSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer_branch cb \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" inner join customer_branches cbs on cbs.customer_id = c.id");
        sql.append(" where cb.deleted = false \n");
        sql.append(" and c.deleted = false \n");
        sql.append(" and cb.status ='").append(CustomerBranchStatus.ACTIVE.getCode()).append("'\n");
        sql.append(" and c.status ='").append(UserStatus.ACTIVE.getCode()).append("'\n");
        sql.append(" and cb.customer_id = ").append(filter.getCustomerId());

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
            sql.append(" and cbs.branch_id in (").append(SecurityUtils.getBranchId()).append(") ");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and lower(cb.device_type) ilike lower('%").append(filter.getType()).append("%')\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(cb.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<CustomerBranchShortListDTO> getListByEmployee(BaseFilter filter) {
        ResultList<CustomerBranchShortListDTO> resultList = new ResultList<>();
        String sql = "select cb.id, cb.name, c.id customerId, c.name as customer, c.tin \n" +
                getListByEmployeeBaseSql(filter) +
                " order by cb." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);
        List<CustomerBranchShortListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CustomerBranchShortListDTO.class));

        String countSql = "select count(cb.id) \n" +
                getListByEmployeeBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);
        long count = ((Long) countQuery.getSingleResult());

        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getListByEmployeeBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from customer_branch cb \n");
        sql.append("inner join customer c on cb.customer_id = c.id \n");
        sql.append("where cb.deleted = false and c.deleted = false \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getEmployeeId() != null) {
            sql.append("and cb.creator_id =").append(filter.getEmployeeId());
        }
        if (filter.getBranchId() != null) {
            sql.append("and cb.branch_id =").append(filter.getBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(cb.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.company_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<SyncCustomerBranchListDTO> getListToSync(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select cb.*, c.tin, at.id as activityTypeId,\n");
        sql.append("r.code as regionCode, d.code as districtCode, ");
        sql.append("uc.login as creatorLogin, cb.created_date as createdDate,\n");
        sql.append("ul.login as modifierLogin, cb.last_modified_date as modifiedDate \n");
        sql.append("from customer_branch cb \n");
        sql.append("inner join customer c on cb.customer_id = c.id \n");
        sql.append("inner join activity_type at on at.id = cb.activity_type_id \n");
        sql.append("inner join region r on r.id = cb.region_id \n");
        sql.append("inner join district d on d.id = cb.district_id \n");
        sql.append("inner join jhi_user uc on uc.id = cb.creator_id \n");
        sql.append("inner join jhi_user ul on ul.id = cb.last_modifier_id \n");
        sql.append("where cb.deleted = false \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        sql.append("order by cb.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SyncCustomerBranchListDTO.class));
    }

    @Override
    public List<CustomerBranchItemsDTO> getCustomerBranchByTinOrSerialNumber(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select cb.id, cb.name, d.serial_number as serialNumber \n");
        sql.append("from customer c \n");
        sql.append("inner join customer_branch cb on c.id = cb.customer_id \n");
        sql.append("inner join customer_branch_device cbd on cb.id = cbd.customer_branch_id \n");
        sql.append("inner join device d on cbd.device_id = d.id \n");
        sql.append("where cb.status = 'ACTIVE'  and cb.deleted is false and cbd.status <> 'DELETED' \n");
        sql.append("and c.tin = '").append(filter.getTin()).append("'\n");
        if (StringUtils.isNotEmpty(filter.getSearch()))
            sql.append(" and lower(d.serial_number) ").append(filter.getLikeSearchKey());
        sql.append("order by cb.id\n");

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(CustomerBranchItemsDTO.class));
    }

    @Override
    public List<CustomerBranchResponseAPayDTO> getCustomerBranches(FilterPayload filter) {
        String sql = "select cb.id          as branchId,\n" +
                "cb.deleted            as deleted,\n" +
                "cb.name               as branchName,\n" +
                "cb.created_date       as createdDate,\n" +
                "cb.last_modified_date as updatedDate,\n" +
                "r.code                as regionCode,\n" +
                "d.code                as districtCode,\n" +
                "cb.longitude          as longitude,\n" +
                "cb.latitude           as latitude,\n" +
                "a.code                as categoryCode,\n" +
                "ap.code               as parentCategoryCode,\n" +
                "c.company_name        as companyName \n" +
                "from customer_branch cb\n" +
                "left join customer c on cb.customer_id = c.id\n" +
                "left join branch b on cb.branch_id = b.id\n" +
                "left join region r on cb.region_id = r.id\n" +
                "left join district d on cb.district_id = d.id\n" +
                "left join activity_type a on cb.activity_type_id = a.id\n" +
                "left join activity_type ap on ap.id = a.parent_id\n" +
                "where cb.deleted is false\n" +
                "order by cb.id\n" +
                " offset " + filter.getPage() * filter.getSize() + " limit " + filter.getSize();

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(CustomerBranchResponseAPayDTO.class));
    }

    @Override
    public BigInteger countCustomerBranches() {
        String sql = """
                select count(cb.id)\s
                from customer_branch cb
                left join customer c on cb.customer_id = c.id
                left join branch b on cb.branch_id = b.id
                left join region r on cb.region_id = r.id
                left join district d on cb.district_id = d.id
                left join activity_type a on cb.activity_type_id = a.id
                left join activity_type ap on ap.id = a.parent_id
                where cb.deleted is false
                """;
        Query countQuery = entityManager.createNativeQuery(sql);

        return BigInteger.valueOf((Long) countQuery.getSingleResult());
    }
}
