package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.BranchDevice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.DeviceForTransferListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncBranchDeviceListDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.repository.custom.BranchDeviceRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class BranchDeviceRepositoryImpl extends BaseRepositoryImpl implements BranchDeviceRepositoryCustom {

    @Override
    public ResultList<SerialNumberDTO> getLookUp(BaseFilter filter) {
        ResultList<SerialNumberDTO> resultList = new ResultList<>();
        String sql = "select d.id, d.serial_number as serialNumber" +
                getItemsBaseSql(filter) +
                " order by bd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<SerialNumberDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(SerialNumberDTO.class));

        String countSql = "select count(bd.id) \n" +
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
        sql.append(" from branch_device bd \n");
        sql.append(" inner join device d on d.id = bd.device_id \n");
        sql.append(" inner join branch b on b.id = bd.branch_id \n");
        sql.append(" where bd.deleted = false \n");
        sql.append(" and d.deleted = false \n");
        sql.append(" and d.model_id = ").append(filter.getModelId());
        if (!isSuperAdmin()) {
            sql.append(" and bd.branch_id = ").append(SecurityUtils.getBranchId());
        }
        sql.append(" and bd.status ='").append(DeviceStatus.REGISTERED).append("'\n");

        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<BaseDeviceFiscalModuleListDTO> getList(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleListDTO> resultList = new ResultList<>();
        String sql = "select bd.id, d.serial_number deviceSerial, fm.serial_number fiscalModuleSerial, \n" +
                "c.id customerId, c.name customer, c.tin tin, \n" +
                "cb.id customerBranchId, cb.name customerBranch, cbd.status deviceStatus, \n" +
                "CASE WHEN f.id is not null then f.status ELSE case when cd.id is not null then cd.status else bd.status end end status, \n" +
                "CASE WHEN f.id is not null then f.created_date ELSE case when cd.id is not null then cd.created_date else bd.created_date end end registrationDate, \n" +
                " u.id as creatorId, (u.first_name||''||u.last_name) creatorName \n" +
                getListBaseSql(filter) +
                " order by bd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<BaseDeviceFiscalModuleListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(BaseDeviceFiscalModuleListDTO.class));

        String countSql = "select count(bd.id) \n" +
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
        sql.append("from branch_device bd \n");
        sql.append("inner join device d on d.id = bd.device_id \n");
        sql.append("inner join branch b on b.id = bd.branch_id \n");
        sql.append("left join customer_device cd on cd.device_id = bd.device_id \n");
        sql.append("left join jhi_user u on u.id = cd.creator_id \n");
        sql.append("left join customer c on c.id = cd.customer_id \n");
        sql.append("left join customer_branch_device cbd on cbd.device_id = bd.device_id \n");
        sql.append("left join customer_branch cb on cb.id = cbd.customer_branch_id \n");
        sql.append("left join fiscalization f on f.device_id = bd.device_id \n");
        sql.append("left join fiscal_module fm on fm.id = f.fiscal_module_id \n");
        sql.append("where bd.deleted = false \n");
        sql.append("and bd.branch_id =").append(SecurityUtils.getBranchId()).append(" \n");
        sql.append("and b.deleted = false \n");
        sql.append("and d.deleted = false \n");
        sql.append("and (cd.id is null or (cd.deleted=false and c.deleted=false)) \n");
        sql.append("and (cbd.id is null or (cbd.deleted=false and cb.deleted=false)) \n");
        sql.append("and (f.id is null or (f.deleted=false and fm.deleted=false)) \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cb.region_id = ").append(filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and cb.district_id = ").append(filter.getDistrictId());
        }
        if (filter.getUserId() != null) {
            sql.append(" and cbd.creator_id =").append(filter.getUserId());
        }

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(b.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(" or lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(cb.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        return sql;
    }

    @Override
    public ResultList<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        ResultList<DeviceForTransferListDTO> resultList = new ResultList<>();
        String sql = "select bd.id, d.id deviceId, d.serial_number serialNumber, dm.id modelId, dm.name as model, bd.status \n" +
                getListForTransferSql(filter) +
                " order by d.id" +
                QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize());

        List<DeviceForTransferListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(DeviceForTransferListDTO.class));

        String countSql = "select count(bd.id) \n" +
                getListForTransferSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getListForTransferSql(CompanyDeviceFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from branch_device bd \n");
        sql.append(" inner join device d on d.id = bd.device_id \n");
        sql.append(" inner join branch b on b.id = bd.branch_id \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" where bd.deleted = false \n");
        sql.append(" and d.deleted = false \n");
        sql.append(" and dm.deleted = false \n");
        sql.append(" and b.deleted = false \n");
        sql.append(" and bd.status = '").append(DeviceStatus.REGISTERED).append("' \n");
        sql.append(" and bd.branch_id =").append(filter.getBranchId());
        sql.append(" and d.id not in (select f.device_id from fiscalization f where f.deleted=false) \n");
        sql.append(" and d.id not in (select cd.device_id from customer_device cd where cd.deleted=false) \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and dm.id =").append(filter.getModelId());
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and d.type = '").append(filter.getType()).append("'\n");
        }
        if (filter.getDeviceId() != null && filter.getQuantity() != null && filter.getQuantity() > 0) {
            sql.append(" and d.id>=").append(filter.getDeviceId());
        } else if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        return sql;
    }

    @Override
    public List<SerialNumberDTO> getListForTransfer2(CompanyDeviceFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select d.id, d.serial_number serialNumber \n");
        sql.append(" from branch_device bd \n");
        sql.append(" inner join device d on d.id = bd.device_id \n");
        sql.append(" inner join branch b on b.id = bd.branch_id \n");
        sql.append(" where bd.deleted = false \n");
        sql.append(" and d.deleted = false \n");
        sql.append(" and bd.status = '").append(DeviceStatus.REGISTERED).append("' \n");
        sql.append(" and bd.branch_id =").append(filter.getBranchId());
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and d.type = '").append(filter.getType()).append("'\n");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }

        if (filter.getModelId() != null) {
            sql.append(" and d.model_id =").append(filter.getModelId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        sql.append(" order by bd.id");
        sql.append(QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize()));
        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SerialNumberDTO.class));
    }

    @Override
    public List<BranchDevice> freeDevices(Long id, List<String> serialNumbers) {
        String sql = "select bd.* from branch_device bd \n" +
                " inner join device d on d.id = bd.device_id \n" +
                " inner join branch b on b.id = bd.branch_id \n" +
                " where bd.deleted = false \n" +
                " and d.deleted = false \n" +
                " and bd.branch_id = " + id +
                " and bd.status not in ('" + DeviceStatus.TRANSFERRED + "', " +
                " '" + DeviceStatus.SOLD + "', " +
                " '" + DeviceStatus.BLOCKED + "', " +
                " '" + DeviceStatus.UN_BLOCKED + "') " +
                " and d.serial_number in (" + QueryUtils.getSqlFormat(serialNumbers) + ") \n" +
                " and b.company_id = " + getCompanyId() + "\n";
        Query listQuery = entityManager.createNativeQuery(sql, BranchDevice.class);

        return (List<BranchDevice>) listQuery.getResultList();
    }

    @Override
    public ResultList<CompanyTerminalListDTO> getListAll(CompanyBranchDeviceFilter filter) {
        ResultList<CompanyTerminalListDTO> resultList = new ResultList<>();
        String sql = "select bd.id, bd.status, d.serial_number, dm.name model, b.id branchId, b.name as branch, \n" +
                "cb.id customerBranchId, cb.name as customerBranch, \n" +
                "(CASE WHEN cbd.id is not null THEN cbd.created_date ELSE bd.created_date END) as registrationDate \n" +
                getListAllBaseSql(filter) +
                " order by bd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<CompanyTerminalListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CompanyTerminalListDTO.class));

        String countSql = "select count(bd.id) \n" +
                getListAllBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getListAllBaseSql(CompanyBranchDeviceFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from branch_device bd \n");
        sql.append("inner join device d on d.id = bd.device_id \n");
        sql.append("inner join device_model dm on dm.id = d.model_id \n");
        sql.append("inner join branch b on b.id = bd.branch_id \n");
        sql.append("left join customer_branch_device cbd on cbd.device_id = bd.device_id \n");
        sql.append("left join customer_branch cb on cb.id = cbd.customer_branch_id \n");
        sql.append("where bd.deleted = false and d.deleted = false and dm.deleted = false and b.deleted = false \n");
        sql.append(" and (cbd.id is null or (cbd.deleted=false and cb.deleted=false))");

        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getDeviceStatus())) {
            sql.append(" and bd.status ='").append(filter.getDeviceStatus()).append("' \n");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and d.type = '").append(filter.getType()).append("'\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<BaseDeviceFiscalModuleListDTO> getListByEmployee(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleListDTO> resultList = new ResultList<>();
        String sql = "select bd.id, d.serial_number deviceSerial, fm.serial_number fiscalModuleSerial, \n" +
                "cb.id customerBranchId, cb.name as customerBranch, \n" +
                "c.id customerId, c.name as customer, c.tin \n" +
                getListByEmployeeBaseSql(filter) +
                " order by bd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<BaseDeviceFiscalModuleListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(BaseDeviceFiscalModuleListDTO.class));

        String countSql = "select count(bd.id) \n" +
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
        sql.append("from branch_device bd \n");
        sql.append("inner join device d on d.id = bd.device_id \n");
        sql.append("left join customer_device cd on cd.device_id = bd.device_id \n");
        sql.append("left join customer_branch_device cbd on cbd.device_id = bd.device_id \n");
        sql.append("left join fiscalization f on f.device_id = bd.device_id \n");
        sql.append("left join customer_branch cb on cb.id = f.customer_branch_id \n");
        sql.append("left join customer c on c.id = f.customer_id \n");
        sql.append("left join fiscal_module fm on fm.id = f.fiscal_module_id \n");
        sql.append("where bd.deleted = false and d.deleted = false \n");
        sql.append("and (cd.id is null or (c.deleted = false and cd.deleted = false)) \n");
        sql.append("and (cbd.id is null or (cb.deleted = false and cbd.deleted = false)) \n");
        sql.append("and (f.id is null or (f.deleted = false and fm.deleted = false)) \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and d.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and d.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getEmployeeId() != null) {
            sql.append("and bd.creator_id =").append(filter.getEmployeeId());
        }
        if (filter.getBranchId() != null) {
            sql.append("and bd.branch_id =").append(filter.getBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(" or lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.company_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearchKey());
            sql.append(" or lower(cb.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<SyncBranchDeviceListDTO> getListToSync(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select bd.*, bd.last_modified_date as modifiedDate, \n");
        sql.append("d.serial_number as serialNumber, c.login as creatorLogin, l.login as modifierLogin \n");
        sql.append("from branch_device bd \n");
        sql.append("inner join branch b on b.id = bd.branch_id \n");
        sql.append("inner join device d on d.id = bd.device_id \n");
        sql.append("left join jhi_user c on c.id = bd.creator_id \n");
        sql.append("left join jhi_user l on l.id = bd.last_modifier_id \n");
        sql.append("where bd.deleted = false \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        sql.append(" order by bd.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SyncBranchDeviceListDTO.class));
    }

    @Override
    public ResultList<BranchDevice> getResultList(BaseFilter filter) {
        ResultList<BranchDevice> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select bd from BranchDevice bd");
        sql.append(" where bd.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and bd.status = :status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and bd.branch.companyId = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and bd.branchId = :branchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and bd.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and bd.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(bd.device.serialNumber)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select bd", " select count(bd.id)");
        sql.append(" order by bd.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<BranchDevice> query = entityManager
                .createQuery(sql.toString(), BranchDevice.class)
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
        sql.append("select count(bd.id) filter ( where bd.status = 'REGISTERED') as REGISTERED,");
        sql.append(" count(bd.id) filter ( where bd.status = 'TRANSFERRED') as TRANSFERRED");
        sql.append(" from branch_device bd");
        sql.append(" inner join branch b on bd.branch_id = b.id");
        sql.append(" inner join device d on bd.device_id = d.id");
        sql.append(" where bd.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and bd.status = :status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and bd.branch_id = :branchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and bd.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and bd.created_date <= :toDate");
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
