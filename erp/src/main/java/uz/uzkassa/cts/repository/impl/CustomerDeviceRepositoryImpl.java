package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.CustomerDevice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.SerialNumberDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.CustomerDeviceRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CustomerDeviceRepositoryImpl extends BaseRepositoryImpl implements CustomerDeviceRepositoryCustom {

    @Override
    public ResultList<SerialNumberDTO> getLookUp(BaseFilter filter) {
        ResultList<SerialNumberDTO> resultList = new ResultList<>();
        String sql = "select d.id, d.serial_number as serialNumber \n" +
                getItemsBaseSql(filter) +
                " order by cd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<SerialNumberDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(SerialNumberDTO.class));

        String countSql = "select count(cd.id) \n" +
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
        sql.append(" from customer_device cd \n");
        sql.append(" inner join device d on d.id = cd.device_id \n");
        sql.append(" inner join customer c on c.id = cd.customer_id \n");
        sql.append(" where cd.deleted = false \n");
        sql.append(" and d.deleted = false \n");
        sql.append(" and d.model_id =").append(filter.getModelId());
        sql.append(" and cd.customer_id =").append(filter.getCustomerId());
        sql.append(" and cd.status in ('").append(DeviceStatus.REGISTERED).append("','").append(DeviceStatus.UN_BLOCKED).append("','").append(DeviceStatus.SOLD).append("','").append(DeviceStatus.SOLD).append("')");
        sql.append(" and d.id not in (select f.device_id from fiscalization f where f.deleted=false) \n");

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
        return sql;
    }

    @Override
    public ResultList<CustomerDevice> getList(BaseFilter filter) {
        ResultList<CustomerDevice> resultList = new ResultList<>();
        String sql = "select cd.* \n" +
                getBaseSql(filter) +
                " order by cd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, CustomerDevice.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());
        List<CustomerDevice> list = listQuery.getResultList();

        String countSql = "select count(cd.id) \n" +
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
        sql.append(" from customer_device cd \n");
        sql.append(" inner join device d on d.id = cd.device_id \n");
        sql.append(" inner join customer c on c.id = cd.customer_id \n");
        sql.append(" where cd.deleted = false \n");
        sql.append(" and d.deleted = false \n");
        sql.append(" and cd.customer_id =").append(filter.getCustomerId());
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and d.type = '").append(filter.getType()).append("'\n");
        }
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
        return sql;
    }

    @Override
    public List<SyncCustomerDeviceListDTO> getListToSync(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select cd.*, cd.last_modified_date as modifiedDate,\n");
        sql.append("d.serial_number as serialNumber, c.tin as tin,");
        sql.append("uc.login as creatorLogin, ul.login as modifierLogin \n");
        sql.append("from customer_device cd\n");
        sql.append("inner join customer c on c.id = cd.customer_id \n");
        sql.append("left join device d on d.id = cd.device_id \n");
        sql.append("left join jhi_user uc on uc.id = cd.creator_id \n");
        sql.append("left join jhi_user ul on ul.id = cd.last_modifier_id \n");
        sql.append("where cd.deleted = false\n");

        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        sql.append("order by cd.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SyncCustomerDeviceListDTO.class));
    }

    @Override
    public ResultList<CustomerDevice> getResultList(BaseFilter filter) {
        ResultList<CustomerDevice> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cd from CustomerDevice cd");
        sql.append(" join cd.customer c");
        sql.append(" where cd.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cd.status = :status");
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
            sql.append(" and cd.createdDate >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cd.createdDate <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cd.device.serialNumber)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cd", " select count(cd.id)");
        sql.append(" order by cd.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CustomerDevice> query = entityManager
                .createQuery(sql.toString(), CustomerDevice.class)
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
        sql.append("select count(cd.id) filter ( where cd.status = 'REGISTERED') as REGISTERED,");
        sql.append(" count(cd.id) filter ( where cd.status = 'TRANSFERRED') as TRANSFERRED");
        sql.append(" from customer_device cd ");
        sql.append(" inner join customer c on cd.customer_id = c.id");
        sql.append(" inner join device d on cd.device_id = d.id");
        sql.append(" where cd.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cd.status = :status");
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
            sql.append(" and cd.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and cd.created_date <= :toDate");
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
