package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.customer.CompanyCustomerListDTO;
import uz.uzkassa.cts.dto.customer.CustomerLookUpDTO;
import uz.uzkassa.cts.dto.customer.CustomerShortDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerListDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.enums.apay.APayRegisterStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.repository.custom.CustomerRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;

@SuppressWarnings("unused")
public class CustomerRepositoryImpl extends BaseRepositoryImpl implements CustomerRepositoryCustom {

    @Override
    public ResultList<Customer> getResultList(CustomerFilter filter) {
        ResultList<Customer> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select c from Customer c");
        sql.append(" left join c.branches cb");
        sql.append(" left join c.customerBank cusBank");
        if (StringUtils.isNotEmpty(filter.getOfdStatus()) && !(OfdStatus.NOT_SEND == OfdStatus.valueOf(filter.getOfdStatus()))) {
            sql.append(" left join OfdApplication ofd on ofd.customerId = c.id");
        }
        sql.append(" where c.deleted = false");

        if (StringUtils.isNotEmpty(filter.getOfdStatus()) && !(OfdStatus.NOT_SEND == OfdStatus.valueOf(filter.getOfdStatus()))) {
            sql.append(" and ofd.ofdStatus = :ofdStatus");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and c.status = :status");
        }
        if (StringUtils.isNotEmpty(filter.getLegalStatus())) {
            sql.append(" and c.legalStatus = :legalStatus");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and c.companyId = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and cb.id = :branchId");
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and cb.id in (:branchIds)");
        }
        if (filter.getActivityTypeId() != null) {
            sql.append(" and c.activityTypeId = :activityTypeId");
        }
        if (filter.getParentActivityTypeId() != null) {
            sql.append(" and c.parentActivityTypeId = :parentActivityTypeId");
        }
        if (StringUtils.isNotEmpty(filter.getBusinessType())) {
            sql.append(" and c.businessType = :businessType");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and c.address.regionId = :regionId");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and c.address.districtId = :districtId");
        }
        if (filter.getNeighborhoodId() != null) {
            sql.append(" and c.address.neighborhoodId = :neighborhoodId");
        }
        if (filter.getCreatorId() != null) {
            sql.append(" and c.creatorId = :creatorId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and c.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and c.createdDate <= :toDate");
        }
        if (CollectionUtils.isNotEmpty(filter.getRegionIds())) {
            sql.append(" and c.address.regionId in (:regionIds)");
        }
        if (StringUtils.isNotEmpty(filter.getAppType())) {
            sql.append(" and c.appType = :appType");
        }
        if (filter.getManagerId() != null) {
            sql.append(" and c.managerId = :managerId");
        }
        if (filter.getBankCategoryId() != null) {
            sql.append(" and cusBank.bankId = :bankId");
        }
        if (StringUtils.isNotEmpty(filter.getAPayRegisterStatus())) {
            APayRegisterStatus aPayRegisterStatus = APayRegisterStatus.valueOf(filter.getAPayRegisterStatus());
            if (APayRegisterStatus.REGISTERED == aPayRegisterStatus) {
                sql.append(" and c.apayId is not null");
            } else if (APayRegisterStatus.NOT_REGISTERED == aPayRegisterStatus) {
                sql.append(" and c.apayId is null");
            }
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name)").append(filter.getLikeSearch());
            sql.append(" or lower(c.companyName)").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearch());
            sql.append(" or lower(c.oldTin)").append(filter.getLikeSearch());
            sql.append(" or lower(c.newTin)").append(filter.getLikeSearch());
            sql.append(" or lower(c.phone)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select c", " select count(distinct c.id)");
        sql.append(" group by c.id");
        sql.append(" order by c.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Customer> query = entityManager
                .createQuery(sql.toString(), Customer.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getOfdStatus()) && !(OfdStatus.NOT_SEND == OfdStatus.valueOf(filter.getOfdStatus()))) {
            query.setParameter("ofdStatus", OfdStatus.valueOf(filter.getOfdStatus()));
            countQuery.setParameter("ofdStatus", OfdStatus.valueOf(filter.getOfdStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", CustomerStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", CustomerStatus.valueOf(filter.getStatus()));
        }
        if (StringUtils.isNotEmpty(filter.getLegalStatus())) {
            query.setParameter("legalStatus", CustomerLegalStatus.valueOf(filter.getLegalStatus()));
            countQuery.setParameter("legalStatus", CustomerLegalStatus.valueOf(filter.getLegalStatus()));
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            query.setParameter("branchIds", filter.getBranchIds());
            countQuery.setParameter("branchIds", filter.getBranchIds());
        }
        if (filter.getActivityTypeId() != null) {
            query.setParameter("activityTypeId", filter.getActivityTypeId());
            countQuery.setParameter("activityTypeId", filter.getActivityTypeId());
        }
        if (filter.getParentActivityTypeId() != null) {
            query.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
            countQuery.setParameter("parentActivityTypeId", filter.getParentActivityTypeId());
        }
        if (StringUtils.isNotEmpty(filter.getBusinessType())) {
            query.setParameter("businessType", BusinessType.valueOf(filter.getBusinessType()));
            countQuery.setParameter("businessType", BusinessType.valueOf(filter.getBusinessType()));
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
        if (filter.getCreatorId() != null) {
            query.setParameter("creatorId", filter.getCreatorId());
            countQuery.setParameter("creatorId", filter.getCreatorId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
            countQuery.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
            countQuery.setParameter("toDate", filter.getToEndOfDay());
        }
        if (CollectionUtils.isNotEmpty(filter.getRegionIds())) {
            query.setParameter("regionIds", filter.getRegionIds());
            countQuery.setParameter("regionIds", filter.getRegionIds());
        }
        if (StringUtils.isNotEmpty(filter.getAppType())) {
            query.setParameter("appType", CustomerAppType.valueOf(filter.getAppType()));
            countQuery.setParameter("appType", CustomerAppType.valueOf(filter.getAppType()));
        }
        if (filter.getManagerId() != null) {
            query.setParameter("managerId", filter.getManagerId());
            countQuery.setParameter("managerId", filter.getManagerId());
        }
        if (filter.getBankCategoryId() != null) {
            query.setParameter("bankId", filter.getBankCategoryId());
            countQuery.setParameter("bankId", filter.getBankCategoryId());
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
    public ResultList<String> getAllToSync(BaseFilter filter) {
        ResultList<String> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select c.tin from Customer c");
        sql.append(" where c.deleted is not true and c.tin is not null");
        String countSql = sql.toString().replaceFirst("select c.tin", "select count(c.tin)");
        sql.append(" order by c.id");

        TypedQuery<String> query = entityManager.createQuery(sql.toString(), String.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());
        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        Long count = countQuery.getSingleResult();
        if (count > 0) {
            resultList.setCount(count);
            resultList.setList(query.getResultList());
        }
        return resultList;
    }

    private StringBuilder customerGetBaseSql(CustomerFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer c \n");
        sql.append(" inner join jhi_user owner on owner.customer_id = c.id \n");
        sql.append(" inner join region r on r.id = c.region_id \n");
        sql.append(" inner join district d on d.id = c.district_id \n");
        sql.append(" inner join neighborhood n on n.id = c.neighborhood_id \n");
        sql.append(" inner join activity_type at on at.id = c.activity_type_id \n");
        sql.append(" where c.deleted = false \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getBusinessType())) {
            sql.append(" and c.business_type = '").append(BusinessType.get(filter.getBusinessType())).append("' \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and c.branch_id = ").append(filter.getBranchId());
        }
        if (filter.getRegionId() != null) {
            sql.append(" and c.region_id = ").append(filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and c.district_id = ").append(filter.getDistrictId());
        }
        if (filter.getEmployeeId() != null) {
            sql.append(" and u.id = ").append(filter.getEmployeeId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.company_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.phone)").append(filter.getLikeSearchKey());
            sql.append(" or lower(owner.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(owner.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(owner.patronymic)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<CustomerShortDTO> getCustomerListByEmployee(BaseFilter filter) {
        ResultList<CustomerShortDTO> resultList = new ResultList<>();
        String sql = "select c.id, c.name, c.tin \n" +
                getCustomerListBaseSql(filter) +
                " order by c." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<CustomerShortDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CustomerShortDTO.class));

        String countSql = "select count(c.id) \n" +
                getCustomerListBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getCustomerListBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from customer c \n");
        sql.append("where c.deleted=false \n");
        if (filter.getEmployeeId() != null) {
            sql.append("and c.creator_id=").append(filter.getEmployeeId());
        }
        if (filter.getBranchId() != null) {
            sql.append("and c.branch_id=").append(filter.getBranchId());
        }

        if (filter.getCompanyId() != null)
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        if (!isSuperAdmin())
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.company_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.phone)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<CompanyCustomerListDTO> getCustomerListByEmployee(CustomerFilter filter, boolean isOfdStatusSelected) {
        ResultList<CompanyCustomerListDTO> resultList = new ResultList<>();
        String sql = "select distinct c.id, c.name as name, c.company_name companyName, c.tin tin, c.old_tin oldTin, c.phone, c.send_geolocation sendGeolocation, c.white_list whiteList, \n" +
                " act.name_ru activityType, c.business_type businessType, b.id branchId, b.name branch, \n" +
                " c.created_date createdDate, (u.first_name||' '||u.last_name) creator, \n" +
                " coalesce(deviceTotal,0) deviceTotal , coalesce(fiscalModuleTotal,0) fiscalModuleTotal, c.app_type as appType \n " +
                getCustomerAllBaseSql(filter, isOfdStatusSelected) +
                " order by " + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<CompanyCustomerListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CompanyCustomerListDTO.class));

        String countSql = "select count(distinct c.id) \n" +
                getCustomerAllBaseSql(filter, isOfdStatusSelected);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());

        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getCustomerAllBaseSql(CustomerFilter filter, boolean isOfdStatusSelected) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer c \n");
        sql.append("inner join jhi_user owner on owner.customer_id = c.id \n");
        sql.append("inner join activity_type act on act.id = c.activity_type_id \n");
        sql.append("inner join branch b on b.id = c.branch_id \n");
        sql.append("inner join jhi_user u on u.id = c.creator_id \n");
        if (filter.getBankCategoryId() != null) {
            sql.append("left join customer_bank cusBank on cusBank.id = c.customer_bank_id \n");
            sql.append("left join banks bk on bk.id = cusBank.bank_id \n");
        }
        sql.append("left join (select customer_id,count(id) deviceTotal \n ");
        sql.append("from customer_device where deleted=false \n");
        sql.append("group by customer_id) cd on cd.customer_id = c.id \n");
        sql.append("left join (select customer_id,count(id) fiscalModuleTotal \n");
        sql.append("from customer_fiscal_module where deleted=false and status !='TRANSFERRED' \n");
        sql.append("group by customer_id) cfm on cfm.customer_id = c.id \n");
        if (StringUtils.isNotEmpty(filter.getOfdStatus()) && OfdStatus.NOT_SEND.equals(OfdStatus.get(filter.getOfdStatus()))) {
            sql.append(" left join ofd_application ofd on c.id = ofd.customer_id \n");
        }
        if (isOfdStatusSelected) {
            sql.append("inner join ofd_application a on a.customer_id = c.id \n");
        }
        sql.append(" where c.deleted = false \n");
        if (StringUtils.isNotEmpty(filter.getOfdStatus()) && OfdStatus.NOT_SEND.equals(OfdStatus.get(filter.getOfdStatus()))) {
            sql.append(" and ofd.customer_id is null \n");
        }
        if (filter.getBankCategoryId() != null) {
            sql.append(" and bk.id = ").append(filter.getBankCategoryId());
        }

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getManagerId() != null) {
            sql.append(" and c.manager_id =").append(filter.getManagerId());
        }
        if (filter.getAllowDeviceRegister() != null) {
            sql.append(" and c.allow_device_register = ").append(filter.getAllowDeviceRegister());
        }
//        if (filter.getSendGeolocation() != null) {
//            sql.append(" and c.send_geolocation = ").append(filter.getSendGeolocation());
//        }
//        if (filter.getApayRegistered() != null) {
//            if (filter.getApayRegistered()) {
//                sql.append(" and c.apay_id is not null");
//            } else {
//                sql.append(" and c.apay_id is null");
//            }
//        }
        if (filter.getEmployeeId() != null) {
            sql.append(" and c.creator_id = ").append(filter.getEmployeeId());
        }
        if (filter.getActivityTypeId() != null) {
            sql.append(" and c.activity_type_id = ").append(filter.getActivityTypeId());
        }
        if (filter.getParentActivityTypeId() != null) {
            sql.append(" and c.parent_activity_type_id = ").append(filter.getParentActivityTypeId());
        }
        if (StringUtils.isNotEmpty(filter.getBusinessType())) {
            sql.append(" and c.business_type = '").append(BusinessType.get(filter.getBusinessType())).append("'");
        }
        if (isOfdStatusSelected && StringUtils.isNotEmpty(filter.getOfdStatus())) {
            sql.append(" and a.ofd_status = '").append(OfdStatus.get(filter.getOfdStatus())).append("'");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and c.branch_id = ").append(filter.getBranchId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and c.branch_id in (").append(QueryUtils.getSqlLongFormat(filter.getBranchIds())).append(")");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and c.region_id = ").append(filter.getRegionId());
        }
        if (CollectionUtils.isNotEmpty(filter.getRegionIds())) {
            sql.append(" and c.region_id in (").append(QueryUtils.getSqlLongFormat(filter.getRegionIds())).append(")");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and c.district_id = ").append(filter.getDistrictId());
        }
        if (StringUtils.isNotEmpty(filter.getAppType())) {
            sql.append(" and c.app_type = '").append(CustomerAppType.valueOf(filter.getAppType())).append("' ");
        }
//        if (filter.getStatusPhone() != null) {
//            sql.append(" and c.status_phone = ").append(filter.getStatusPhone()).append("");
//        }
        if (StringUtils.isNotEmpty(filter.getLegalStatus())) {
            sql.append(" and c.legal_status = '").append(filter.getLegalStatus()).append("'");
        }
        if (filter.getBankTariffId() != null) {
            sql.append(" and c.bank_tariff_id = ").append(filter.getBankTariffId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or ");
            sql.append(" lower(c.old_tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.phone)").append(filter.getLikeSearchKey());

            if (!SecurityUtils.isCurrentUserInRole(Role.CALL_CENTER.getRoleCode())) {
                sql.append(" or lower(c.name)").append(filter.getLikeSearchKey());
                sql.append(" or lower(c.company_name)").append(filter.getLikeSearchKey());
                sql.append(" or lower(owner.first_name)").append(filter.getLikeSearchKey());
                sql.append(" or lower(owner.last_name)").append(filter.getLikeSearchKey());
                sql.append(" or lower(owner.patronymic)").append(filter.getLikeSearchKey());
            }
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<String> getTinListInterval(int start, int limit) {
        TypedQuery<String> query = entityManager
                .createQuery("select c.tin from Customer c where c.deleted=false order by c.id", String.class)
                .setFirstResult(start).setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<String> getPhoneNumberListInterval(int start, int limit) {
        TypedQuery<String> query = entityManager
                .createQuery("select c.phone from Customer c where c.deleted=false order by c.id", String.class)
                .setFirstResult(start).setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public List<SyncCustomerListDTO> getCustomerListToSync(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select c.*,");
        sql.append("v.percent as vatRate, c.activity_type_id as activityTypeId,r.code as regionCode, d.code as districtCode,");
        sql.append("b.code as bankCode, cb.account_number as accountNumber, cb.oked as oked,");
        sql.append("uc.login as creatorLogin, ul.login as modifierLogin, c.last_modified_date as modifiedDate \n");
        sql.append("from customer c \n");
        sql.append("left join vat v on v.id = c.vat_id \n");
        sql.append("inner join region r on r.id = c.region_id \n");
        sql.append("inner join district d on d.id = c.district_id \n");
        sql.append("left join customer_bank cb on cb.id = c.customer_bank_id \n");
        sql.append("inner join banks b on b.id = cb.bank_id \n");
        sql.append("left join jhi_user uc on uc.id = c.creator_id \n");
        sql.append("left join jhi_user ul on ul.id = c.last_modifier_id \n");
        sql.append("where c.deleted = false \n");

        if (filter.getCompanyId() != null)
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        if (!isSuperAdmin())
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");

        sql.append(" order by ").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SyncCustomerListDTO.class));
    }

    @Override
    public ResultList<CustomerLookUpDTO> getLookUpBranchCustomer(BaseFilter filter) {
        ResultList<CustomerLookUpDTO> resultList = new ResultList<>();
        String sql = "select distinct c.id as id, c.name as name, c.tin tin, c.app_type as appType, c.vat_id as vatId \n" +
                getItemsBaseSql(filter) +
                " order by c." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<CustomerLookUpDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CustomerLookUpDTO.class));

        String countSql = "select count(distinct c.id) \n" +
                getItemsBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getItemsBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer c \n");
        sql.append(" left join customer_branches cbs on cbs.customer_id = c.id \n");
        sql.append(" where c.deleted = false \n");
        sql.append(" and c.status in (").append(QueryUtils.getSqlFormat(List.of(CustomerStatus.ACTIVE.getCode(), CustomerStatus.DEBTOR.getCode()))).append(")\n");
        if (!SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode())) {
            sql.append(" and (c.branch_id = ").append(filter.getBranchId()).append(" or cbs.branch_id = ").append(filter.getBranchId()).append(") \n");
        }

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.phone)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        return sql;
    }
}
