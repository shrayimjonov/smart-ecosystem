package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.CustomerFiscalModule;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CustomerFiscalModuleRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CustomerFiscalModuleRepositoryImpl extends BaseRepositoryImpl implements CustomerFiscalModuleRepositoryCustom {

    @Override
    public ResultList<CustomerFiscalModule> getList(BaseFilter filter) {
        ResultList<CustomerFiscalModule> resultList = new ResultList<>();
        String sql = "select cfm.* \n" +
                getBaseSql(filter) +
                " order by cfm." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, CustomerFiscalModule.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        List<CustomerFiscalModule> list = listQuery.getResultList();

        String countSql = "select count(cfm.id) \n" +
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
        sql.append(" from customer_fiscal_module cfm \n");
        sql.append(" inner join fiscal_module fm on fm.id = cfm.fiscal_module_Id \n");
        sql.append(" where cfm.deleted = false \n");
        sql.append(" and fm.deleted = false \n");
        sql.append(" and cfm.customer_id = ").append(filter.getCustomerId());

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

    @Override
    public ResultList<CustomerFiscalModule> getResultList(BaseFilter filter) {
        ResultList<CustomerFiscalModule> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cfm from CustomerFiscalModule cfm");
        sql.append(" join cfm.customer c");
        sql.append(" where cfm.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cfm.status = :status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and c.companyId = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and c.branchId = :branchId");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and c.id = :customerId");
        }
        if (filter.getFrom() != null) {
            sql.append(" and cfm.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cfm.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cfm.fiscalModule.serialNumber)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cfm", " select count(cfm.id)");
        sql.append(" order by cfm.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CustomerFiscalModule> query = entityManager
                .createQuery(sql.toString(), CustomerFiscalModule.class)
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
        sql.append("select count(cfm.id) filter ( where cfm.status = 'FISCAL') as FISCAL,");
        sql.append(" count(cfm.id) filter ( where cfm.status = 'TRANSFERRED') as TRANSFERRED");
        sql.append(" from customer_fiscal_module cfm");
        sql.append(" inner join customer c on cfm.customer_id = c.id");
        sql.append(" inner join fiscal_module fm on cfm.fiscal_module_id = fm.id");
        sql.append(" where cfm.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cfm.status = :status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = :companyId");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and c.branch_id = :branchId");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and c.id = :customerId");
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

    private StringBuilder getLookUpBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from customer_fiscal_module cfm \n");
        sql.append(" inner join fiscal_module fm on fm.id = cfm.fiscal_module_Id \n");
        sql.append(" where cfm.deleted = false \n");
        sql.append(" and fm.deleted = false \n");
        sql.append(" and cfm.customer_id =").append(filter.getCustomerId());
        sql.append(" and cfm.status ='").append(DeviceStatus.REGISTERED).append("'\n");
        sql.append(" and fm.id not in (select f.fiscal_module_id from fiscalization f where f.deleted=false) \n");

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
}
