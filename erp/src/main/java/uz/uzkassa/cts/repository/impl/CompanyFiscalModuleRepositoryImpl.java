package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.CompanyFiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleListDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.repository.custom.CompanyFiscalModuleRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CompanyFiscalModuleRepositoryImpl extends BaseRepositoryImpl implements CompanyFiscalModuleRepositoryCustom {

    @Override
    public ResultList<BaseDeviceFiscalModuleListDTO> getList(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleListDTO> resultList = new ResultList<>();
        String sql = "select distinct comfm.id, d.serial_number deviceSerial, fm.serial_number fiscalModuleSerial, \n" +
                "cb.id customerBranchId, cb.name as customerBranch, \n" +
                "c.id customerId, c.name as customer, c.tin, \n" +
                " (case when ifm.id is null then 'ACTIVE' else 'IN_ACTIVE' end) as ofdStatus, " +
                " (case " +
                " when f.id is not null then f.created_date " +
                " when cbfm.id is not null then cbfm.created_date " +
                " when cfm.id is not null then cbfm.created_date " +
                " when bfm.id is not null then cbfm.created_date " +
                " when fm.id is not null then cbfm.created_date " +
                " end ) as registrationDate " +
                getListBaseSql(filter) +
                " order by comfm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<BaseDeviceFiscalModuleListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(BaseDeviceFiscalModuleListDTO.class));

        String countSql = "select count(distinct comfm.id) \n" +
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
        sql.append("from company_fiscal_module comfm \n");
        sql.append("inner join fiscal_module fm on fm.id = comfm.fiscal_module_id \n");
        sql.append("inner join branch_fiscal_module bfm on bfm.fiscal_module_id = comfm.fiscal_module_id \n");
        sql.append("left join customer_fiscal_module cfm on cfm.fiscal_module_id = comfm.fiscal_module_id \n");
        sql.append("left join customer_branch_fiscal_module cbfm on cbfm.fiscal_module_id = comfm.fiscal_module_id \n");
        sql.append("left join fiscalization f on f.fiscal_module_id = comfm.fiscal_module_id \n");
        sql.append("left join customer_branch cb on cb.id = f.customer_branch_id \n");
        sql.append("left join customer c on c.id = f.customer_id \n");
        sql.append("left join device d on d.id = f.device_id \n");
        sql.append("left join inactive_fiscal_module ifm on ifm.fiscal_module_id = fm.id \n");
        sql.append("where comfm.deleted = false and f.deleted = false \n");
        sql.append("and (cfm.id is null or (c.deleted = false and cfm.deleted = false)) \n");
        sql.append("and (cbfm.id is null or (cb.deleted = false and cbfm.deleted = false)) \n");
        sql.append("and (f.id is null or (f.deleted = false and fm.deleted = false)) \n");

        if (filter.getEmployeeId() != null) {
            sql.append("and comfm.creator_id =").append(filter.getEmployeeId());
        }
        if (filter.getBranchId() != null) {
            sql.append("and bfm.branch_id =").append(filter.getBranchId());
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cb.region_id = ").append(filter.getRegionId());
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and comfm.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and comfm.company_id = ").append(getCompanyId()).append("\n");
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
    public ResultList<CompanyTerminalListDTO> getListAll(CompanyBranchDeviceFilter filter) {
        ResultList<CompanyTerminalListDTO> resultList = new ResultList<>();
        String sql = "select cfm.id, cfm.status, fm.serial_number, b.id branchId, b.name as branch, \n" +
                "(CASE WHEN bfm.id is not null THEN bfm.created_date ELSE cfm.created_date END) as registrationDate \n" +
                getListAllBaseSql(filter) +
                " order by cfm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<CompanyTerminalListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CompanyTerminalListDTO.class));

        String countSql = "select count(cfm.id) \n" +
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
        sql.append("from company_fiscal_module cfm \n");
        sql.append("inner join fiscal_module fm on fm.id = cfm.fiscal_module_id \n");
        sql.append("left join branch_fiscal_module bfm on bfm.fiscal_module_id = cfm.fiscal_module_id \n");
        sql.append("left join branch b on b.id = bfm.branch_id \n");
        sql.append("where cfm.deleted = false and fm.deleted = false \n");
        sql.append(" and (bfm.id is null or (bfm.deleted=false and b.deleted=false)) \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and cfm.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and cfm.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number) ").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        return sql;
    }

    @Override
    public ResultList<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        ResultList<FiscalModuleForTransferListDTO> resultList = new ResultList<>();
        String sql = "select cfm.id, fm.id fiscalModuleId, fm.serial_number serialNumber, cfm.status \n" +
                getListForTransferSql(filter) +
                " order by cfm.id " +
                QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize());

        List<FiscalModuleForTransferListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(FiscalModuleForTransferListDTO.class));

        String countSql = "select count(cfm.id) \n" +
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
    public ResultList<CompanyFiscalModule> getResultList(BaseFilter filter) {
        ResultList<CompanyFiscalModule> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cfm from CompanyFiscalModule cfm ");
        sql.append(" where cfm.deleted = false ");
        if (filter.getCompanyId() != null) {
            sql.append(" and cfm.companyId =:companyId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cfm.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cfm.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cfm.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cfm.fiscalModule.serialNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cfm", " select count(cfm.id)");
        sql.append(" order by cfm.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CompanyFiscalModule> query = entityManager
                .createQuery(sql.toString(), CompanyFiscalModule.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
            countQuery.setParameter("companyId", filter.getCompanyId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", DeviceStatus.get(filter.getStatus()));
            countQuery.setParameter("status", DeviceStatus.get(filter.getStatus()));
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
        sql.append("select count(cfm.id) filter ( where cfm.status = 'REGISTERED') as REGISTERED,");
        sql.append(" count(cfm.id) filter ( where cfm.status = 'TRANSFERRED') as TRANSFERRED");
        sql.append(" from company_fiscal_module cfm");
        sql.append(" inner join fiscal_module fm on cfm.fiscal_module_id = fm.id");
        sql.append(" where cfm.deleted = false");
        if (filter.getCompanyId() != null) {
            sql.append(" and cfm.company_id = :companyId");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cfm.status = :status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cfm.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cfm.created_date <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), Map.class);

        if (filter.getCompanyId() != null) {
            query.setParameter("companyId", filter.getCompanyId());
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", filter.getStatus());
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

    private StringBuilder getListForTransferSql(CompanyDeviceFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from company_fiscal_module cfm \n");
        sql.append(" inner join fiscal_module fm on fm.id = cfm.fiscal_module_id \n");
        sql.append(" where cfm.deleted = false \n");
        sql.append(" and fm.deleted = false \n");
        sql.append(" and cfm.status = '").append(DeviceStatus.REGISTERED).append("' \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and cfm.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and cfm.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getFiscalModuleId() != null && filter.getQuantity() != null && filter.getQuantity() > 0) {
            sql.append(" and fm.id>=").append(filter.getFiscalModuleId());
        } else if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
