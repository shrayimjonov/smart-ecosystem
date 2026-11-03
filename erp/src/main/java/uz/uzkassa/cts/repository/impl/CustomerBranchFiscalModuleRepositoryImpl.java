package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.CustomerBranchFiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.BaseDeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CustomerBranchFiscalModuleRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CustomerBranchFiscalModuleRepositoryImpl extends BaseRepositoryImpl implements CustomerBranchFiscalModuleRepositoryCustom {

    @Override
    public ResultList<SerialNumberDTO> getLookUp(BaseFilter filter) {
        ResultList<SerialNumberDTO> resultList = new ResultList<>();
        String sql = "select fm.id, fm.serial_number as serialNumber \n" +
                getLookUpBaseSql(filter) +
                " order by fm.serial_number " +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<SerialNumberDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(SerialNumberDTO.class));

        String countSql = "select count(fm.id) \n" +
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
        sql.append(" from customer_branch_fiscal_module cbfm \n");
        sql.append(" inner join fiscal_module fm on fm.id = cbfm.fiscal_module_id \n");
        sql.append(" inner join customer_branch cb on cb.id = cbfm.customer_branch_id \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" where cbfm.deleted = false ");
        sql.append(" and fm.deleted = false ");
        sql.append(" and cbfm.customer_branch_id = ").append(filter.getCustomerBranchId());
        sql.append(" and cbfm.status = '").append(DeviceStatus.REGISTERED).append("' \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<SerialNumberDTO> getItems(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select fm.id, fm.serial_number as serialNumber ");
        sql.append(" from customer_branch_fiscal_module cbfm \n");
        sql.append(" inner join fiscal_module fm on fm.id = cbfm.fiscal_module_id \n");
        sql.append(" inner join customer_branch cb on cb.id = cbfm.customer_branch_id \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" where cbfm.deleted = false \n");
        sql.append(" and fm.deleted = false \n");
        sql.append(" and cbfm.customer_branch_id =").append(filter.getCustomerBranchId());

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" order by cbfm.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SerialNumberDTO.class));
    }

    @Override
    public ResultList<CustomerBranchFiscalModule> getList(BaseFilter filter) {
        ResultList<CustomerBranchFiscalModule> resultList = new ResultList<>();
        String sql = "select cbfm.* " +
                getBaseListSql(filter) +
                " order by cbfm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, CustomerBranchFiscalModule.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        List<CustomerBranchFiscalModule> list = listQuery.getResultList();

        String countSql = "select count(cbfm.id) \n" +
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
        sql.append(" from customer_branch_fiscal_module cbfm \n");
        sql.append(" inner join fiscal_module fm on fm.id = cbfm.fiscal_module_id \n");
        sql.append(" inner join customer_branch cb on cb.id = cbfm.customer_branch_id \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" where cbfm.deleted = false \n");
        sql.append(" and cbfm.customer_branch_id = ").append(filter.getCustomerBranchId());

        if (filter.getCompanyId() != null)
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        if (!isSuperAdmin())
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");

        return sql;
    }

    @Override
    public ResultList<BaseDeviceFiscalModuleDTO> getListByCustomerBranch(BaseFilter filter) {
        ResultList<BaseDeviceFiscalModuleDTO> resultList = new ResultList<>();
        String sql = "select cbfm.id, d.serial_number as deviceSerial, fm.serial_number as fiscalModuleSerial, \n" +
                "CASE WHEN f.id is not null THEN f.status ELSE cbfm.status END status, \n" +
                "CASE WHEN f.id is not null THEN f.created_date ELSE cbfm.created_date END registrationDate \n" +
                getBaseSql(filter) +
                " order by cbfm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<BaseDeviceFiscalModuleDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(BaseDeviceFiscalModuleDTO.class));

        String countSql = "select count(cbfm.id) \n" +
                getBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public ResultList<CustomerBranchFiscalModule> getResultList(BaseFilter filter) {
        ResultList<CustomerBranchFiscalModule> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cbfm from CustomerBranchFiscalModule cbfm");
        sql.append(" join cbfm.customerBranch cb");
        sql.append(" where cbfm.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cbfm.status = :status");
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
            sql.append(" and cbfm.customerBranchId = :customerBranchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cbd.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cbd.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cbfm.fiscalModule.serialNumber)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cbfm", " select count(cbfm.id)");
        sql.append(" order by cbfm.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CustomerBranchFiscalModule> query = entityManager
                .createQuery(sql.toString(), CustomerBranchFiscalModule.class)
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
        sql.append("select count(cbfm.id) filter ( where cbfm.status = 'REGISTERED') as REGISTERED,");
        sql.append(" count(cbfm.id) filter ( where cbfm.status = 'FISCAL') as FISCAL,");
        sql.append(" count(cbfm.id) filter ( where cbfm.status = 'FISCAL_ACTIVE') as FISCAL_ACTIVE");
        sql.append(" from customer_branch_fiscal_module cbfm");
        sql.append(" join fiscal_module fm on cbfm.fiscal_module_id = fm.id");
        sql.append(" join customer_branch cb on cbfm.customer_branch_id = cb.id");
        sql.append(" join branch b on cb.branch_id = b.id");
        sql.append(" where cbfm.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cbfm.status = :status");
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
            sql.append(" and cbfm.customer_branch_id = :customerBranchId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cbd.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cbd.created_date <= :toDate");
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
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getCustomerBranchId() != null) {
            query.setParameter("customerBranchId", filter.getCustomerBranchId());
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

    private StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer_branch_fiscal_module cbfm \n");
        sql.append(" inner join fiscal_module fm on fm.id = cbfm.fiscal_module_id \n");
        sql.append(" inner join customer_branch cb on cb.id = cbfm.customer_branch_id \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" left join fiscalization f on f.fiscal_module_id = cbfm.fiscal_module_id \n");
        sql.append(" left join device d on d.id = f.device_id \n");
        sql.append(" where cbfm.deleted = false \n");
        sql.append(" and fm.deleted = false \n");
        sql.append(" and cbfm.customer_branch_id = ").append(filter.getCustomerBranchId());
        sql.append(" and (f.id is null or f.deleted = false)");

        if (filter.getCompanyId() != null)
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        if (!isSuperAdmin())
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(" or lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
