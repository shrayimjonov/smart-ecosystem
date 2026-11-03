package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.FiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.RegistryListDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleForTransferListDTO;
import uz.uzkassa.cts.dto.fm.FiscalModuleListDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.*;
import uz.uzkassa.cts.repository.custom.FiscalModuleRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class FiscalModuleRepositoryImpl extends BaseRepositoryImpl implements FiscalModuleRepositoryCustom {


    @Override
    public Page<RegistryListDTO> listRegister(CompanyDeviceListFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct fm.id, fm.serial_number fiscalModuleSerial, d.serial_number deviceSerial, \n");
        sql.append("b.id branchId, b.name branch, \n");
        sql.append("c.id customerId, c.name customer, c.tin tin, \n");
        sql.append("cb.id customerBranchId, cb.name customerBranch, \n");

        sql.append("CASE WHEN f.id is not null then f.status ELSE \n");
        sql.append("case when cbfm.id is not null then cbfm.status else \n");
        sql.append("case when cfm.id is not null then cfm.status else \n");
        sql.append("case when bfm.id is not null then bfm.status else \n");
        sql.append("case when compfm.id is not null then compfm.status else \n");
        sql.append("f.status end end end end end status, \n");

        sql.append("CASE WHEN f.id is not null then f.created_date ELSE \n");
        sql.append("case when cbfm.id is not null then cbfm.created_date else \n");
        sql.append("case when cfm.id is not null then cfm.created_date else \n");
        sql.append("case when bfm.id is not null then bfm.created_date else \n");
        sql.append("case when compfm.id is not null then compfm.created_date else \n");
        sql.append("f.created_date end end end end end registrationDate, \n");

        sql.append("f.created_date fiscalizationDate ");
        sql.append(getListBaseSql(filter));
        sql.append(" order by \n");
        if ("date".equals(filter.getOrderBy())) {
            sql.append("CASE WHEN f.id is not null then f.created_date ELSE \n");
            sql.append("case when cbfm.id is not null then cbd.created_date else \n");
            sql.append("case when cfm.id is not null then cb.created_date else \n");
            sql.append("case when bfm.id is not null then bd.created_date else \n");
            sql.append("case when compfm.id is not null then compfm.created_date else \n");
            sql.append("fm.created_date end end end end end \n");
        } else {
            sql.append(" fm.").append(filter.getOrderBy());
        }
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        List<RegistryListDTO> result = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(RegistryListDTO.class));

        String countSql = "select count(distinct fm.id) \n" +
                getListBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        return new PageImpl<>
                (result, filter.getOrderedPageable(), (Long) countQuery.getSingleResult());
    }

    private StringBuilder getListBaseSql(CompanyDeviceListFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from fiscal_module fm \n");
        sql.append("left join company_fiscal_module compfm on compfm.fiscal_module_id = fm.id \n");
        sql.append("left join branch_fiscal_module bfm on bfm.fiscal_module_id = fm.id \n");
        sql.append("left join branch b on b.id = bfm.branch_id \n");
        sql.append("left join customer_fiscal_module cfm on cfm.fiscal_module_id = fm.id \n");
        sql.append("left join customer c on c.id = cfm.customer_id \n");
        sql.append("left join customer_branch_fiscal_module cbfm on cbfm.fiscal_module_id = fm.id \n");
        sql.append("left join customer_branch cb on cb.id = cbfm.customer_branch_id \n");
        sql.append("left join fiscalization f on f.fiscal_module_id = fm.id \n");
        sql.append("left join device d on d.id = f.device_id \n");
        sql.append("where fm.deleted = false \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and fm.company_id =").append(filter.getCompanyId());
        }
        if (!isSuperAdmin()) {
            sql.append(" and fm.company_id =").append(getCompanyId());
        }
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (filter.getRegionId() != null) {
            sql.append(" and b.region_id = ").append(filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and b.district_id = ").append(filter.getDistrictId());
        }

        if (DeviceStatus.get(filter.getStatus()) != null) {
            sql.append(" and (compfm.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or cfm.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or bfm.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or d.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or fm.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or f.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(") \n");
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
    public ResultList<SerialNumberDTO> getLookUp(FiscalModuleLookUpFilter filter) {
        ResultList<SerialNumberDTO> resultList = new ResultList<>();
        String sql = "select fm.serial_number serialNumber \n" +
                getBaseSql(filter) +
                " order by fm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<SerialNumberDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(SerialNumberDTO.class));

        String countSql = "select count(fm.id) \n" +
                getBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from fiscal_module fm \n");
        sql.append(" where fm.deleted = false ");
        sql.append(" and fm.status in ('").append(DeviceStatus.NOT_REGISTERED).append("','").append(DeviceStatus.UNREGISTERED).append("') \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and fm.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and fm.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public ResultList<FiscalModuleListDTO> getList(BaseFilter filter) {
        ResultList<FiscalModuleListDTO> resultList = new ResultList<>();
        String sql = "select fm.id, fm.serial_number serialNumber, fm.created_date registrationDate, fm.status \n" +
                getNewListBaseSql(filter) +
                " order by fm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<FiscalModuleListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(FiscalModuleListDTO.class));

        String countSql = "select count(fm.id) \n" +
                getNewListBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getNewListBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from fiscal_module fm \n");
        sql.append(" where 1=1 \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and fm.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and fm.company_id = ").append(getCompanyId()).append("\n");
        }
        if (DeviceStatus.get(filter.getStatus()) != null) {
            sql.append(" and fm.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("' \n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        return sql;
    }

    @Override
    public ResultList<FiscalModuleForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        ResultList<FiscalModuleForTransferListDTO> resultList = new ResultList<>();
        String sql = "select fm.id, fm.id fiscalModuleId, fm.serial_number serialNumber, fm.status \n" +
                getListForTransferSql(filter) +
                " order by fm.id " +
                QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize());

        List<FiscalModuleForTransferListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(FiscalModuleForTransferListDTO.class));

        String countSql = "select count(fm.id) \n" +
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
        sql.append(" from fiscal_module fm \n");
        sql.append(" where fm.deleted = false \n");
        sql.append(" and fm.status in ('").append(DeviceStatus.NOT_REGISTERED).append("','").append(DeviceStatus.UNREGISTERED).append("') \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and fm.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and fm.company_id = ").append(getCompanyId()).append("\n");
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

    @Override
    public ResultList<CompanyTerminalListDTO> getListAll(CompanyBranchDeviceFilter filter) {
        ResultList<CompanyTerminalListDTO> resultList = new ResultList<>();
        String sql = "select fm.id, fm.status, fm.serial_number serialNumber, fm.created_date as registrationDate \n" +
                getListAllBaseSql(filter) +
                " order by fm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<CompanyTerminalListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CompanyTerminalListDTO.class));

        String countSql = "select count(fm.id) \n" +
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
    public ResultList<FiscalModule> getResultList(BaseFilter filter) {
        ResultList<FiscalModule> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select fm from FiscalModule fm ");
        sql.append(" where fm.deleted = false ");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and fm.status =:status ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and fm.branchId = :branchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and fm.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and fm.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(fm.serialNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select fm", " select count(fm.id)");
        sql.append(" order by fm.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<FiscalModule> query = entityManager
                .createQuery(sql.toString(), FiscalModule.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

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
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
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
        sql.append("select count(fm.id) filter ( where fm.status = 'REGISTERED') as REGISTERED,");
        sql.append(" count(fm.id) filter ( where fm.status = 'UNREGISTERED') as UNREGISTERED,");
        sql.append(" count(fm.id) filter ( where fm.status = 'NOT_REGISTERED') as NOT_REGISTERED");
        sql.append(" from fiscal_module fm");
        sql.append(" where fm.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and fm.status = :status");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and fm.branch_id = :branchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and fm.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and fm.created_date <= :toDate");
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
        if (filter.getFrom() != null) {
            query.setParameter("fromDate", filter.getFromStartOfDay());
        }
        if (filter.getTo() != null) {
            query.setParameter("toDate", filter.getToEndOfDay());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
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
        sql.append("from fiscal_module fm \n");
        sql.append("where fm.deleted = false \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and fm.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and fm.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number) ").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        return sql;
    }
}
