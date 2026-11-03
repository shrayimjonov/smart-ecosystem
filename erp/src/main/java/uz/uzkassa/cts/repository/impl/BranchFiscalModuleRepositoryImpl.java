package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.BranchFiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.repository.custom.BranchFiscalModuleRepositoryCustom;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class BranchFiscalModuleRepositoryImpl extends BaseRepositoryImpl implements BranchFiscalModuleRepositoryCustom {

    @Override
    public ResultList<BaseDeviceFiscalModuleListDTO> getList(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleListDTO> resultList = new ResultList<>();
        String sql = "select bfm.id, d.serial_number deviceSerial, fm.serial_number fiscalModuleSerial, \n" +
                "c.id customerId, c.name customer, c.tin tin, \n" +
                "cb.id customerBranchId, cb.name customerBranch, \n" +
                "CASE WHEN f.id is not null then f.status ELSE case when cfm.id is not null then cfm.status else bfm.status end end status, \n" +
                "CASE WHEN f.id is not null then f.created_date ELSE case when cfm.id is not null then cfm.created_date else bfm.created_date end end registrationDate, \n" +
                " (case when ifm.id is null then 'ACTIVE' else 'IN_ACTIVE' end) as ofdStatus " +
                getListBaseSql(filter) +
                " order by bfm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<BaseDeviceFiscalModuleListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(BaseDeviceFiscalModuleListDTO.class));

        String countSql = "select count(bfm.id) \n" +
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
        sql.append("from branch_fiscal_module bfm \n");
        sql.append("inner join fiscal_module fm on fm.id = bfm.fiscal_module_id \n");
        sql.append("inner join branch b on b.id = bfm.branch_id \n");
        sql.append("left join customer_fiscal_module cfm on cfm.fiscal_module_id = bfm.fiscal_module_id \n");
        sql.append("left join customer c on c.id = cfm.customer_id \n");
        sql.append("left join customer_branch_fiscal_module cbfm on cbfm.fiscal_module_id = bfm.fiscal_module_id \n");
        sql.append("left join customer_branch cb on cb.id = cbfm.customer_branch_id \n");
        sql.append("left join fiscalization f on f.fiscal_module_id = bfm.fiscal_module_id \n");
        sql.append("left join device d on d.id = f.device_id \n");
        sql.append("left join inactive_fiscal_module ifm on ifm.fiscal_module_id = fm.id \n");
        sql.append("where bfm.deleted = false \n");
        sql.append("and bfm.branch_id =").append(SecurityUtils.getBranchId()).append(" \n");
        sql.append("and b.deleted = false \n");
        sql.append("and fm.deleted = false \n");
        sql.append("and (cfm.id is null or (cfm.deleted=false and c.deleted=false)) \n");
        sql.append("and (cbfm.id is null or (cbfm.deleted=false and cb.deleted=false)) \n");
        sql.append("and (f.id is null or (f.deleted=false and fm.deleted=false)) \n");

        if (filter.getCompanyId() != null)
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        if (!isSuperAdmin())
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");

        if (filter.getRegionId() != null) {
            sql.append(" and cb.region_id = ").append(filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and cb.district_id = ").append(filter.getDistrictId());
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
    public ResultList<BaseDeviceFiscalModuleListDTO> getListByEmployee(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleListDTO> resultList = new ResultList<>();
        String sql = "select bfm.id, d.serial_number deviceSerial, fm.serial_number fiscalModuleSerial, \n" +
                "cb.id customerBranchId, cb.name as customerBranch, \n" +
                "c.id customerId, c.name as customer, c.tin \n" +
                getListByEmployeeBaseSql(filter) +
                " order by bfm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<BaseDeviceFiscalModuleListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(BaseDeviceFiscalModuleListDTO.class));

        String countSql = "select count(bfm.id) \n" +
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
        sql.append("from branch_fiscal_module bfm \n");
        sql.append("inner join fiscal_module fm on fm.id = bfm.fiscal_module_id \n");
        sql.append("inner join branch b on b.id = bfm.branch_id \n");
        sql.append("left join customer_fiscal_module cfm on cfm.fiscal_module_id = bfm.fiscal_module_id \n");
        sql.append("left join customer_branch_fiscal_module cbfm on cbfm.fiscal_module_id = bfm.fiscal_module_id \n");
        sql.append("left join fiscalization f on f.fiscal_module_id = bfm.fiscal_module_id \n");
        sql.append("left join customer_branch cb on cb.id = f.customer_branch_id \n");
        sql.append("left join customer c on c.id = f.customer_id \n");
        sql.append("left join device d on d.id = f.device_id \n");
        sql.append("where bfm.deleted = false and f.deleted = false \n");
        sql.append("and (cfm.id is null or (c.deleted = false and cfm.deleted = false)) \n");
        sql.append("and (cbfm.id is null or (cb.deleted = false and cbfm.deleted = false)) \n");
        sql.append("and (f.id is null or (f.deleted = false and fm.deleted = false)) \n");

        if (filter.getCompanyId() != null)
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        if (!isSuperAdmin())
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");

        if (filter.getEmployeeId() != null) {
            sql.append("and bfm.creator_id =").append(filter.getEmployeeId());
        }
        if (filter.getBranchId() != null) {
            sql.append("and bfm.branch_id =").append(filter.getBranchId());
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
    public ResultList<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        ResultList<FiscalModuleForTransferListDTO> resultList = new ResultList<>();
        String sql = "select bfm.id, fm.id fiscalModuleId, fm.serial_number serialNumber, bfm.status \n" +
                getListForTransferSql(filter) +
                " order by fm.id" +
                QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize());

        List<FiscalModuleForTransferListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(FiscalModuleForTransferListDTO.class));

        String countSql = "select count(bfm.id) \n" +
                getListForTransferSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public ResultList<SerialNumberDTO> getListForTransfer2(CompanyDeviceFilter filter) {
        ResultList<SerialNumberDTO> resultList = new ResultList<>();
        String sql = "select fm.serial_number serialNumber \n" +
                getListForTransferSql(filter) +
                " order by bfm.id" +
                QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize());

        List<SerialNumberDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(SerialNumberDTO.class));

        String countSql = "select count(bfm.id) \n" +
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
        sql.append(" from branch_fiscal_module bfm \n");
        sql.append(" inner join fiscal_module fm on fm.id = bfm.fiscal_module_id \n");
        sql.append(" inner join branch b on b.id = bfm.branch_id \n");
        sql.append(" where bfm.deleted = false \n");
        sql.append(" and fm.deleted = false \n");
        sql.append(" and b.deleted = false \n");
        sql.append(" and bfm.status = '").append(DeviceStatus.REGISTERED).append("' \n");
        sql.append(" and bfm.branch_id =").append(filter.getBranchId());
        sql.append(" and fm.id not in (select f.fiscal_module_id from fiscalization f where f.deleted=false) \n");
        sql.append(" and fm.id not in (select cfm.fiscal_module_id from customer_fiscal_module cfm where cfm.deleted=false) \n");

        if (filter.getCompanyId() != null)
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        if (!isSuperAdmin())
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");

        if (filter.getFiscalModuleId() != null && filter.getQuantity() != null && filter.getQuantity() > 0) {
            sql.append(" and fm.id>=").append(filter.getFiscalModuleId());
        } else if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<CompanyTerminalListDTO> getListAll(CompanyBranchDeviceFilter filter) {
        ResultList<CompanyTerminalListDTO> resultList = new ResultList<>();
        String sql = "select bfm.id, bfm.status, fm.serial_number, b.id branchId, b.name as branch, \n" +
                "cb.id customerBranchId, cb.name as customerBranch, \n" +
                "(CASE WHEN cbfm.id is not null THEN cbfm.created_date ELSE bfm.created_date END) as registrationDate \n" +
                getListAllBaseSql(filter) +
                " order by bfm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<CompanyTerminalListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CompanyTerminalListDTO.class));

        String countSql = "select count(bfm.id) \n" +
                getListAllBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public ResultList<BranchFiscalModule> getResultList(BaseFilter filter) {
        ResultList<BranchFiscalModule> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select bfm from BranchFiscalModule bfm");
        sql.append(" join bfm.fiscalModule fm");
        sql.append(" join bfm.branch b");
        sql.append(" where bfm.deleted = false");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and bfm.status = :status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and b.companyId = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and b.id = :branchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and bfm.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and bfm.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(fm.serialNumber)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select bfm", " select count(bfm.id)");
        sql.append(" order by bfm.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<BranchFiscalModule> query = entityManager
                .createQuery(sql.toString(), BranchFiscalModule.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", DeviceStatus.get(filter.getStatus()));
            countQuery.setParameter("status", DeviceStatus.get(filter.getStatus()));
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
        sql.append("select count(bfm.id) filter ( where bfm.status = 'REGISTERED') as REGISTERED,");
        sql.append(" count(bfm.id) filter ( where bfm.status = 'TRANSFERRED') as TRANSFERRED,");
        sql.append(" count(bfm.id) filter ( where bfm.status = 'SOLD') as SOLD");
        sql.append(" from branch_fiscal_module bfm");
        sql.append(" join fiscal_module fm on bfm.fiscal_module_id = fm.id");
        sql.append(" join branch b on bfm.branch_id = b.id");
        sql.append(" where bfm.deleted = false");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and bfm.status = :status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and b.id = :branchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and bfm.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and bfm.created_date <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearch());
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

    private StringBuilder getListAllBaseSql(CompanyBranchDeviceFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from branch_fiscal_module bfm \n");
        sql.append("inner join fiscal_module fm on fm.id = bfm.fiscal_module_id \n");
        sql.append("inner join branch b on b.id = bfm.branch_id \n");
        sql.append("left join customer_branch_fiscal_module cbfm on cbfm.fiscal_module_id = bfm.fiscal_module_id \n");
        sql.append("left join customer_branch cb on cb.id = cbfm.customer_branch_id \n");
        sql.append("where bfm.deleted = false and fm.deleted = false and b.deleted = false \n");
        sql.append(" and (cbfm.id is null or (cbfm.deleted=false and cb.deleted=false))");

        if (filter.getCompanyId() != null)
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        if (!isSuperAdmin())
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");

        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
