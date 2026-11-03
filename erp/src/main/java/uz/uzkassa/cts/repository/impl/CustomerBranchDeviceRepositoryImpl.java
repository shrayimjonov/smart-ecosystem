package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.CustomerDeviceListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.enums.DevicePaymentType;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CustomerBranchDeviceRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CustomerBranchDeviceRepositoryImpl extends BaseRepositoryImpl implements CustomerBranchDeviceRepositoryCustom {

    @Override
    public ResultList<CustomerBranchDevice> getList(BaseFilter filter) {
        ResultList<CustomerBranchDevice> resultList = new ResultList<>();
        String sql = "select cbd.* " +
                getListBaseSql(filter) +
                " order by cbd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, CustomerBranchDevice.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        List<CustomerBranchDevice> list = listQuery.getResultList();

        String countSql = "select count(cbd.id) \n" +
                getListBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getListBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer_branch_device cbd \n");
        sql.append(" inner join customer_branch cb on cb.id = cbd.customer_branch_id\n");
        sql.append(" inner join customer c on c.id = cb.customer_id\n");
        sql.append(" where cbd.deleted = false \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        sql.append(" and cbd.customer_branch_id =").append(filter.getCustomerBranchId());

        return sql;
    }

    @Override
    public ResultList<SerialNumberDTO> getLookUp(BaseFilter filter) {
        ResultList<SerialNumberDTO> resultList = new ResultList<>();
        String sql = "select d.id, d.serial_number as serialNumber " +
                getLookUpBaseSql(filter) +
                " order by cbd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<SerialNumberDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(SerialNumberDTO.class));

        String countSql = "select count(cbd.id) \n" +
                getLookUpBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getLookUpBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer_branch_device cbd \n");
        sql.append(" inner join device d on d.id = cbd.device_id \n");
        sql.append(" inner join customer_branch cb on cb.id = cbd.customer_branch_id\n");
        sql.append(" inner join customer c on c.id = cb.customer_id\n");
        sql.append(" where cbd.deleted = false \n");
        sql.append(" and d.deleted = false \n");
        // @todo bu joydi logic yana qarab ko`rish kk
//        sql.append(" and (case when cbd.payment_type='RENT' or c.app_type='SMARTPOS' then cbd.service_allowed = true else cbd.service_allowed = false end) ");
        sql.append(" and d.model_id = ").append(filter.getModelId());
        sql.append(" and cbd.customer_branch_id =").append(filter.getCustomerBranchId());

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        //TODO For february invoices, it's temporary disabled
        //sql.append(" and cbd.status !='").append(DeviceStatusEnum.BLOCKED.getCode()).append("'\n");
        sql.append(" and d.id not in (select f.device_id from fiscalization f where f.deleted=false) \n");
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<SerialNumberDTO> getItems(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select d.id, d.serial_number as serialNumber ");
        sql.append(" from customer_branch_device cbd \n");
        sql.append(" inner join device d on d.id = cbd.device_id \n");
        sql.append(" inner join customer_branch cb on cb.id = cbd.customer_branch_id\n");
        sql.append(" inner join customer c on c.id = cb.customer_id\n");
        sql.append(" where cbd.deleted = false \n");
        sql.append(" and d.deleted = false \n");
        sql.append(" and cbd.customer_branch_id =").append(filter.getCustomerBranchId());
        sql.append(" and d.model_id = ").append(filter.getModelId());

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" order by cbd.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SerialNumberDTO.class));
    }

    @Override
    public List<CustomerDeviceListDTO> getFreeDevices(Long customerId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select cbd.* from customer_branch_device cbd \n");
        sql.append(" inner join customer_branch cb on cb.id = cbd.customer_branch_id \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" where cbd.deleted = false \n");
        sql.append(" and c.id =").append(customerId).append("\n");
        sql.append(" and cbd.service_allowed = true ");
        sql.append(" and cbd.payment_type!='").append(DevicePaymentType.RENT.getCode()).append("' \n");

        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        Query query = entityManager.createNativeQuery(sql.toString(), CustomerBranchDevice.class);
        List<CustomerBranchDevice> resultList = query.getResultList();

        List<CustomerDeviceListDTO> result = new ArrayList<>();
        resultList.forEach(customerBranchDevice ->
                result.add(new CustomerDeviceListDTO(customerBranchDevice.getId(),
                        customerBranchDevice.getDevice().getSerialNumber(),
                        customerBranchDevice.getDevice().getModel().toCommonCodeDTO())));

        return result;
    }

    @Override
    public ResultList<BaseDeviceFiscalModuleDTO> getListByCustomerBranch(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleDTO> resultList = new ResultList<>();
        String sql = "select cbd.id, d.serial_number as deviceSerial, fm.serial_number as fiscalModuleSerial, \n" +
                "cbd.status deviceStatus, " +
                "CASE WHEN f.id is not null then f.status ELSE cbd.status end status, \n" +
                "CASE WHEN f.id is not null then f.created_date ELSE cbd.created_date end registrationDate \n" +
                getBaseListSql(filter) +
                " order by cbd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<BaseDeviceFiscalModuleDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(BaseDeviceFiscalModuleDTO.class));

        String countSql = "select count(cbd.id) \n" +
                getBaseListSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseListSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer_branch_device cbd \n");
        sql.append("inner join device d on d.id = cbd.device_id \n");
        sql.append("inner join customer_branch cb on cb.id = cbd.customer_branch_id \n");
        sql.append("inner join customer c on c.id = cb.customer_id \n");
        sql.append("left join fiscalization f on f.device_id = cbd.device_id \n");
        sql.append("left join fiscal_module fm on fm.id = f.fiscal_module_id \n");
        sql.append("where cbd.deleted = false \n");
        sql.append("and d.deleted = false \n");
        sql.append("and cbd.customer_branch_id=").append(filter.getCustomerBranchId()).append(" \n");
        sql.append("and (f.id is null or f.deleted = false)").append("\n");

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(" or lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<SyncCustomerDeviceListDTO> getListToSync(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select cbd.*, cbd.last_modified_date as modifiedDate, \n");
        sql.append("d.serial_number as serialNumber, uc.login as creatorLogin, ul.login as modifierLogin \n");
        sql.append("from customer_branch_device cbd \n");
        sql.append("inner join customer_branch cb on cb.id = cbd.customer_branch_id \n");
        sql.append("inner join customer c on c.id = cb.customer_id \n");
        sql.append("inner join device d on d.id = cbd.device_id \n");
        sql.append("left join jhi_user uc on uc.id = cbd.creator_id \n");
        sql.append("left join jhi_user ul on ul.id = cbd.last_modifier_id \n");
        sql.append("where cbd.deleted = false\n");

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        sql.append(" order by cbd.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SyncCustomerDeviceListDTO.class));
    }

    @Override
    public ResultList<CustomerBranchDevice> getResultList(BaseFilter filter) {
        ResultList<CustomerBranchDevice> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cbd from CustomerBranchDevice cbd");
        sql.append(" join cbd.customerBranch cb");
        sql.append(" where cbd.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cbd.status = :status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and cb.branch.companyId = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and cb.branchId = :branchId");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and cb.customerId = :customerId");
        }
        if (filter.getCustomerBranchId() != null) {
            sql.append(" and cbd.customerBranchId = :customerBranchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cbd.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cbd.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cbd.device.serialNumber)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cbd", " select count(cbd.id)");
        sql.append(" order by cbd.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CustomerBranchDevice> query = entityManager
                .createQuery(sql.toString(), CustomerBranchDevice.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", DeviceStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", DeviceStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
            countQuery.setParameter("customerBranchId", filter.getCustomerBranchId());
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
    public List<DeviceStatisticsAdminDTO> getStatistics(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(cbd.id) filter ( where cbd.status = 'REGISTERED') as REGISTERED,");
        sql.append(" count(cbd.id) filter ( where cbd.status = 'BLOCKED') as BLOCKED,");
        sql.append(" count(cbd.id) filter ( where cbd.status = 'UN_BLOCKED') as UN_BLOCKED");
        sql.append(" from customer_branch_device cbd");
        sql.append(" inner join customer_branch cb on cbd.customer_branch_id = cb.id");
        sql.append(" inner join branch b on cb.branch_id = b.id");
        sql.append(" inner join device d on cbd.device_id = d.id");
        sql.append(" where cbd.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cbd.status = :status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and cb.branch_id = :branchId");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and cb.customer_id = :customerId");
        }
        if (filter.getCustomerBranchId() != null) {
            sql.append(" and cbd.customer_branch_id = :customerBranchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cbd.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cbd.created_date <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), Map.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", filter.getStatus());
        }
        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCompanyId());
        }
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.isSearchNotEmpty()) {
            query.setParameter("searchKey", filter.getSearchFor());
        }

        Map<String, Long> resultMap = (Map<String, Long>) query.getSingleResult();
        return resultMap
                .entrySet()
                .stream()
                .map(result -> new DeviceStatisticsAdminDTO(DeviceStatus.get(result.getKey()), result.getValue()))
                .toList();
    }
}
