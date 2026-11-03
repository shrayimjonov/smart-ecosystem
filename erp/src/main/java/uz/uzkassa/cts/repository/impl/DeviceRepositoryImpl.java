package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.Device;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.sync.SyncDeviceListDTO;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.SortType;
import uz.uzkassa.cts.filter.*;
import uz.uzkassa.cts.mappings.DeviceMapper;
import uz.uzkassa.cts.repository.custom.DeviceRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class DeviceRepositoryImpl extends BaseRepositoryImpl implements DeviceRepositoryCustom {

    private final DeviceMapper deviceMapper;

    public DeviceRepositoryImpl(DeviceMapper deviceMapper) {
        this.deviceMapper = deviceMapper;
    }

    @Override
    public ResultList<Device> getResultList(BaseFilter filter) {
        ResultList<Device> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select d from Device d ");
        sql.append(" where d.deleted = false ");
        if (filter.getModelId() != null) {
            sql.append(" and d.modelId =:modelId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and d.status =:status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and d.createdDate>=:fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and d.createdDate<=:toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(d.serialNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select d", " select count(d.id)");
        sql.append(" order by d.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Device> query = entityManager
                .createQuery(sql.toString(), Device.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (filter.getModelId() != null) {
            query.setParameter("modelId", filter.getModelId());
            countQuery.setParameter("modelId", filter.getModelId());
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
        sql.append("select count(d.id) filter ( where d.status = 'REGISTERED') as REGISTERED,");
        sql.append(" count(d.id) filter ( where d.status = 'UNREGISTERED') as UNREGISTERED,");
        sql.append(" count(d.id) filter ( where d.status = 'NOT_REGISTERED') as NOT_REGISTERED");
        sql.append(" from device d");
        sql.append(" where d.deleted = false ");

        if (filter.getModelId() != null) {
            sql.append(" and d.model_id = :modelId ");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and d.status = :status ");
        }
        if (filter.getFrom() != null) {
            sql.append(" and d.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and d.created_date <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), Map.class);

        if (filter.getModelId() != null) {
            query.setParameter("modelId", filter.getModelId());
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

    @Override
    public Page<RegistryListDTO> listRegister(CompanyDeviceListFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct d.id, d.serial_number deviceSerial, fm.serial_number fiscalModuleSerial, \n");
        sql.append("b.id branchId, b.name branch, \n");
        sql.append("c.id customerId, c.name customer, c.tin tin, \n");
        sql.append("cb.id customerBranchId, cb.name customerBranch, cbd.status deviceStatus, \n");
        sql.append("COALESCE(f.status, \n" +
                "             cbd.status, \n" +
                "             cb.status, \n" +
                "             bd.status, \n" +
                "             compdev.status, \n" +
                "             d.status) AS status,");

        sql.append("COALESCE(f.created_date, \n" +
                "             cbd.created_date, \n" +
                "             cb.created_date, \n" +
                "             bd.created_date, \n" +
                "             compdev.created_date, \n" +
                "             d.created_date) AS registrationDate,");

        sql.append("f.created_date fiscalizationDate ");
        sql.append(getListBaseSql(filter));
        sql.append(" order by \n");
        if ("date".equals(filter.getOrderBy())) {
            sql.append("CASE WHEN f.id is not null then f.created_date ELSE \n");
            sql.append("case when cbd.id is not null then cbd.created_date else \n");
            sql.append("case when cb.id is not null then cb.created_date else \n");
            sql.append("case when bd.id is not null then bd.created_date else \n");
            sql.append("case when compdev.id is not null then compdev.created_date else \n");
            sql.append("d.created_date end end end end end \n");
        } else {
            filter.setSortOrder(SortType.asc);
            sql.append(" d.").append(filter.getOrderBy());
        }
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        List<RegistryListDTO> result = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(RegistryListDTO.class));

        String countSql = "select count(distinct d.id) \n" +
                getListBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        return new PageImpl<>
                (result, filter.getOrderedPageable(),
                        ((Long) (countQuery.getSingleResult())));

    }

    private StringBuilder getListBaseSql(CompanyDeviceListFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from device d \n");
        sql.append("left join company_device compdev on compdev.device_id = d.id \n");
        sql.append("left join branch_device bd on bd.device_id = d.id \n");
        sql.append("left join branch b on b.id = bd.branch_id \n");
        sql.append("left join customer_device cd on cd.device_id = d.id \n");
        sql.append("left join customer c on c.id = cd.customer_id \n");
        sql.append("left join customer_branch_device cbd on cbd.device_id = d.id \n");
        sql.append("left join customer_branch cb on cb.id = cbd.customer_branch_id \n");
        sql.append("left join fiscalization f on f.device_id = d.id \n");
        sql.append("left join fiscal_module fm on fm.id = f.fiscal_module_id \n");
        sql.append("where d.deleted = false \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and d.company_id =").append(filter.getCompanyId());
        }
        if (!isSuperAdmin()) {
            sql.append(" and d.company_id =").append(getCompanyId());
        }
        if (filter.getBranchId() != null) {
            sql.append(" and bd.branch_id =").append(filter.getBranchId());
        }
        if (filter.getRegionId() != null) {
            sql.append(" and cb.region_id = ").append(filter.getRegionId());
        }
        if (filter.getDistrictId() != null) {
            sql.append(" and cb.district_id = ").append(filter.getDistrictId());
        }
        if (DeviceStatus.get(filter.getDeviceStatus()) != null) {
            sql.append(" and cbd.status ='").append(DeviceStatus.get(filter.getDeviceStatus()).getCode()).append("'\n");
            sql.append(" and cbd.deleted = false \n");
        }
        if (DeviceStatus.get(filter.getStatus()) != null) {
            sql.append(" and (compdev.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or cd.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or bd.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
            sql.append(" or d.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n");
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
    public Page<DeviceForTransferListDTO> getListForTransfer(CompanyDeviceFilter filter) {
        String sql = "select d.id, d.id deviceId, d.serial_number serialNumber, dm.id modelId, dm.name as model, d.status \n" +
                getListForTransferSql(filter) +
                " order by d.id " +
                QueryUtils.getOffsetLimit(filter.getPage(), filter.getSize());
        List<DeviceForTransferListDTO> result = jdbcSpringManager.getSimpleJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(DeviceForTransferListDTO.class));

        String countSql = "select count(d.id) \n" +
                getListForTransferSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        return new PageImpl<>(result, filter.getOrderedPageable(), ((Long) (countQuery.getSingleResult())));
    }

    private StringBuilder getListForTransferSql(CompanyDeviceFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from device d \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" where d.deleted = false \n");
        sql.append(" and dm.deleted = false \n");
        sql.append(" and d.status in ('").append(DeviceStatus.NOT_REGISTERED).append("','").append(DeviceStatus.UNREGISTERED).append("') \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and d.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdminAndAdmin()) {
            sql.append(" and d.company_id = ").append(getCompanyId()).append("\n");
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
    public Page<CompanyTerminalListDTO> listAll(CompanyBranchDeviceFilter filter) {
        String sql = "select d.id, d.status, d.serial_number serialNumber, dm.name as model, d.created_date as registrationDate \n" +
                getListAllBaseSql(filter) +
                " order by d." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<CompanyTerminalListDTO> result = jdbcSpringManager.getSimpleJdbcTemplate().query(sql, BeanPropertyRowMapper.newInstance(CompanyTerminalListDTO.class));

        String countSql = "select count(d.id) \n" +
                getListAllBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        return new PageImpl<>(result, filter.getOrderedPageable(), ((Long) (countQuery.getSingleResult())));
    }

    private StringBuilder getListAllBaseSql(CompanyBranchDeviceFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from device d \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" where d.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and d.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and d.company_id = ").append(getCompanyId()).append("\n");
        }
        if (StringUtils.isNotEmpty(filter.getDeviceStatus())) {
            sql.append(" and d.status ='").append(filter.getDeviceStatus()).append("' \n");
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

    @Override
    public Page<DeviceDetailDTO> list(BaseFilter filter) {
        String sql = "select d.* \n" +
                getBaseSql(filter) +
                " order by d." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, Device.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        List<Device> resultList = listQuery.getResultList();

        String countSql = "select count(d.id) \n" +
                getBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        List<DeviceDetailDTO> result = new ArrayList<>();
        resultList.forEach(device -> result.add(deviceMapper.toDetailDto(device)));

        return new PageImpl<>(result, filter.getOrderedPageable(), ((Long) (countQuery.getSingleResult())));
    }

    private StringBuilder getBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from device d \n");
        sql.append(" where d.deleted = false ");
        if (filter.getCompanyId() != null) {
            sql.append(" and d.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and d.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and d.model_id = ").append(filter.getModelId());
        }
        if (DeviceStatus.get(filter.getStatus()) != null) {
            sql.append(" and d.status ='").append(DeviceStatus.get(filter.getStatus()).getCode()).append("'\n ");
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        return sql;
    }

    @Override
    public List<Device> getSyncItems(SyncDeviceFilter syncDeviceFilter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select d.* from device d where d.deleted = false \n");
        if (syncDeviceFilter.getCompanyId() != null) {
            sql.append(" and d.company_id = ").append(syncDeviceFilter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and d.company_id = ").append(getCompanyId()).append("\n");
        }
        sql.append(" and d.model_id = ").append(syncDeviceFilter.getModelId());
        sql.append(" and d.status in ('").append(DeviceStatus.NOT_REGISTERED).append("','").append(DeviceStatus.UNREGISTERED).append("') \n");
        sql.append(" order by id asc ");
        Query listQuery = entityManager.createNativeQuery(sql.toString(), Device.class).setMaxResults(syncDeviceFilter.getTotal().intValue());
        return listQuery.getResultList();
    }

    @Override
    public Long getDeviceTotalByModel(Long modelId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(d.id) \n");
        sql.append(" from device d \n");
        sql.append(" where d.deleted = false ");
        if (!isSuperAdmin()) {
            sql.append(" and d.company_id = ").append(getCompanyId()).append("\n");
        }
        sql.append(" and d.model_id =").append(modelId);
        sql.append(" and d.status in ('").append(DeviceStatus.NOT_REGISTERED).append("','").append(DeviceStatus.UNREGISTERED).append("') \n");
        Query countQuery = entityManager.createNativeQuery(sql.toString());

        return ((Long) (countQuery.getSingleResult()));
    }

    @Override
    public Page<SerialNumberDTO> resultLookUp(DeviceLookUpFilter filter) {
        String sql = "select d.* \n" +
                getBaseLookUpSql(filter) +
                " order by d." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName();

        Query listQuery = entityManager.createNativeQuery(sql, Device.class);
        listQuery.setFirstResult(filter.getStart()).setMaxResults(filter.getSize());

        List<Device> resultList = listQuery.getResultList();

        List<SerialNumberDTO> result = new ArrayList<>();
        resultList.forEach(device -> result.add(new SerialNumberDTO(device.getId(), device.getSerialNumber())));
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.size());
    }

    private StringBuilder getBaseLookUpSql(DeviceLookUpFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(" from device d \n");
        sql.append(" where d.deleted = false ");
        sql.append(" and d.model_id = ").append(filter.getModelId());
        sql.append(" and d.status in ('").append(DeviceStatus.NOT_REGISTERED).append("','").append(DeviceStatus.UNREGISTERED).append("') \n");

        if (StringUtils.isNotEmpty(filter.getType())) {
            sql.append(" and d.type = '").append(filter.getType()).append("'\n");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and d.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and d.company_id = ").append(getCompanyId()).append("\n");
        }

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        return sql;
    }

    @Override
    public List<SyncDeviceListDTO> getListToSync(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select d.*, d.last_modified_date as modifiedDate,\n");
        sql.append("uc.login as creatorLogin, ul.login as modifierLogin \n");
        sql.append("from device d \n");
        sql.append("left join jhi_user uc on uc.id = d.creator_id \n");
        sql.append("left join jhi_user ul on ul.id = d.last_modifier_id \n");
        sql.append("where d.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and d.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and d.company_id = ").append(getCompanyId()).append("\n");
        }
        sql.append(" order by d.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());
        sql.append(QueryUtils.getOffsetLimit(filter));

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(SyncDeviceListDTO.class));
    }
}
