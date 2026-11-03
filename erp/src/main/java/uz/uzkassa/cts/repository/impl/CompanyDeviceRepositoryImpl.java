package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.CompanyDevice;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.CompanyDeviceListNewDTO;
import uz.uzkassa.cts.dto.device.CompanyTerminalListDTO;
import uz.uzkassa.cts.dto.device.DeviceForTransferListDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CompanyBranchDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceFilter;
import uz.uzkassa.cts.filter.CompanyDeviceListFilter;
import uz.uzkassa.cts.repository.custom.CompanyDeviceRepositoryCustom;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class CompanyDeviceRepositoryImpl extends BaseRepositoryImpl implements CompanyDeviceRepositoryCustom {

    @Override
    public ResultList<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        ResultList<DeviceForTransferListDTO> resultList = new ResultList<>();
        String sql = "select cd.id, d.id deviceId, d.serial_number serialNumber, dm.id modelId, dm.name as model, cd.status \n" +
                getListForTransferSql(filter) +
                " order by d.id " +
                QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize());

        List<DeviceForTransferListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(DeviceForTransferListDTO.class));

        String countSql = "select count(cd.id) \n" +
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
        sql.append(" from company_device cd \n");
        sql.append(" inner join device d on d.id = cd.device_id \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" where cd.deleted = false \n");
        sql.append(" and d.deleted = false \n");
        sql.append(" and dm.deleted = false \n");
        sql.append(" and cd.status = '").append(DeviceStatus.REGISTERED).append("' \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and cd.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and cd.company_id = ").append(getCompanyId()).append("\n");
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
    public ResultList<CompanyDeviceListNewDTO> getList(CompanyDeviceListFilter filter) {
        ResultList<CompanyDeviceListNewDTO> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct compdev.id, d.serial_number deviceSerial, fm.serial_number fiscalModuleSerial, \n");
        sql.append("c.id customerId, c.name customer, c.tin tin, c.phone phone, \n");
        sql.append("cb.id customerBranchId, cb.name customerBranch, cbd.status deviceStatus, \n");
        sql.append("CASE WHEN f.id is not null then f.status ELSE case when cd.id is not null then cd.status else compdev.status end end status, \n");
        sql.append("CASE WHEN f.id is not null then f.created_date ELSE case when cd.id is not null then cd.created_date else compdev.created_date end end registrationDate, \n");
        sql.append("r.name_ru regionName, ds.name_ru districtName, cbd.payment_type as paymentType \n");
        sql.append(getListBaseSql(filter));
        sql.append(" order by \n");
        if ("date".equals(filter.getOrderBy())) {
            sql.append(" CASE WHEN f.id is not null then f.created_date ELSE case when cd.id is not null then cd.created_date else compdev.created_date end end \n");
        } else {
            sql.append(" compdev.").append(filter.getOrderBy());
        }
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        List<CompanyDeviceListNewDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(),
                BeanPropertyRowMapper.newInstance(CompanyDeviceListNewDTO.class));

        String countSql = "select count(distinct compdev.id) \n" +
                getListBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    private StringBuilder getListBaseSql(CompanyDeviceListFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from company_device compdev \n");
        sql.append("inner join device d on d.id = compdev.device_id \n");
        sql.append("left join branch_device bd on bd.device_id = compdev.device_id \n");
        sql.append("left join branch b on b.id = bd.branch_id \n");
        sql.append("left join customer_device cd on cd.device_id = compdev.device_id \n");
        sql.append("left join customer c on c.id = cd.customer_id \n");
        sql.append("left join customer_branch_device cbd on cbd.device_id = compdev.device_id \n");
        sql.append("left join customer_branch cb on cb.id = cbd.customer_branch_id \n");
        sql.append("left join fiscalization f on f.device_id = compdev.device_id \n");
        sql.append("left join fiscal_module fm on fm.id = f.fiscal_module_id \n");
        sql.append("left join region r on r.id = cb.region_id \n");
        sql.append("left join district ds on ds.id = cb.district_id \n");
        if (CollectionUtils.isNotEmpty(filter.getPaymentStatuses())) {
            sql.append("left join customer_branch_device_payment_type ps on cbd.id = ps.customer_branch_device_id \n");
        }
        sql.append("where compdev.deleted = false \n");
        sql.append("and b.deleted = false \n");
        sql.append("and d.deleted = false \n");
        sql.append("and (cd.id is null or (cd.deleted=false and c.deleted=false)) \n");
        sql.append("and (cbd.id is null or (cbd.deleted=false and cb.deleted=false)) \n");
        sql.append("and (f.id is null or (f.deleted=false and fm.deleted=false)) \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and compdev.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and compdev.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and bd.branch_id =").append(filter.getBranchId());
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cb.region_id = ").append(filter.getRegionId());
        }
        if (CollectionUtils.isNotEmpty(filter.getBranchIds())) {
            sql.append(" and bd.branch_id in (").append(filter.getBranchIds()).append(")\n");
        }
        if (CollectionUtils.isNotEmpty(filter.getRegionIds())) {
            sql.append(" and cb.region_id in (").append(filter.getRegionIds()).append(")\n");
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and cb.district_id = ").append(filter.getDistrictId());
        }
        if (filter.getUserId() != null) {
            sql.append(" and cbd.creator_id =").append(filter.getUserId());
        }
        if (DeviceStatus.get(filter.getDeviceStatus()) != null) {
            sql.append(" and cbd.status ='").append(DeviceStatus.get(filter.getDeviceStatus()).getCode()).append("'\n");
            sql.append(" and cbd.deleted = false \n");
        }
        if (DeviceStatus.get(filter.getStatus()) != null) {
            sql.append(" and (compdev.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or cd.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or f.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(") \n");
        }
        if (CollectionUtils.isNotEmpty(filter.getPaymentStatuses())) {
            sql.append(" and ps.payment_status in (").append(QueryUtils.getSqlFormat(filter.getPaymentStatuses())).append(")\n");
        }
        if (CollectionUtils.isNotEmpty(filter.getPaymentTypes())) {
            sql.append(" and cbd.payment_type in (").append(QueryUtils.getSqlFormat(filter.getPaymentTypes())).append(")\n");
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            sql.append("and f.created_date between '").append(DateUtil.getFromStartOfDay(filter.getFrom())).append("' and '").append(DateUtil.getToEndOfDay(filter.getTo())).append("' \n");
        }
        if (filter.getCbdFrom() != null && filter.getCbdTo() != null) {
            sql.append("and cbd.created_date between '").append(DateUtil.getFromStartOfDay(filter.getCbdFrom())).append("' and '").append(DateUtil.getToEndOfDay(filter.getCbdTo())).append("' \n");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and c.app_type = '").append(filter.getType()).append("'\n");
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
    public ResultList<CompanyTerminalListDTO> getListAll(CompanyBranchDeviceFilter filter) {
        ResultList<CompanyTerminalListDTO> resultList = new ResultList<>();
        String sql = "select cd.id, cd.status, d.serial_number, dm.name as model, b.id branchId, b.name as branch, \n" +
                "(CASE WHEN bd.id is not null THEN bd.created_date ELSE cd.created_date END) as registrationDate \n" +
                getListAllBaseSql(filter) +
                " order by cd." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<CompanyTerminalListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(CompanyTerminalListDTO.class));

        String countSql = "select count(cd.id) \n" +
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
    public ResultList<CompanyDevice> getResultList(BaseFilter filter) {
        ResultList<CompanyDevice> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select cd from CompanyDevice cd ");
        sql.append(" where cd.deleted = false ");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cd.status =:status ");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and cd.companyId =:companyId ");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(cd.device.serialNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select cd", " select count(cd.id)");
        sql.append(" order by cd.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<CompanyDevice> query = entityManager
                .createQuery(sql.toString(), CompanyDevice.class)
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
        sql.append(" from company_device cd");
        sql.append(" inner join device d on cd.device_id = d.id");
        sql.append(" where cd.deleted = false");
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and cd.status = :status");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and cd.company_id = :companyId");
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
        sql.append("from company_device cd \n");
        sql.append("inner join device d on d.id = cd.device_id \n");
        sql.append("inner join device_model dm on dm.id = d.model_id \n");
        sql.append("left join branch_device bd on cd.device_id = bd.device_id \n");
        sql.append("left join branch b on b.id = bd.branch_id \n");
        sql.append("where cd.deleted = false and d.deleted = false \n");
        sql.append(" and (bd.id is null or (bd.deleted=false and b.deleted=false)) \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and cd.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and cd.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getDeviceStatus() != null) {
            sql.append(" and cd.status ='").append(filter.getDeviceStatus()).append("' \n");
        }
        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and d.type = '").append(filter.getType()).append("'\n");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number) ").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }
}
