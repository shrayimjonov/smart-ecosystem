package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.device.DeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.smartpos.SmartposFiscalizationListDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.repository.custom.FiscalizationRepositoryCustom;
import uz.uzkassa.cts.utils.QueryUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class FiscalizationRepositoryImpl extends BaseRepositoryImpl implements FiscalizationRepositoryCustom {

    @Override
    public ResultList<SmartposFiscalizationListDTO> getResultListDTO(BaseFilter filter) {
        ResultList<SmartposFiscalizationListDTO> resultList = new ResultList<>();
        String sql = "select f.id,\n" +
                "d.serial_number as deviceSerial, fm.serial_number as fiscalModuleSerial,\n" +
                "c.id as cusId, c.name cusName, c.tin as cusTin, c.business_type as cusBusinessType, \n" +
                "c.house, c.street, c.apartment, cd.code as districtCode, r.code as regionCode, v.percent as cusVat, \n" +
                "cb.id as brId, cb.name as brName, cb.longitude as brLongitude, cb.latitude as brLatitude, c.business_type as brBusinessType, \n" +
                "cb.house as brHouse, cb.street as brStreet, cb.apartment as brApartment, cbr.code as brRegionCode, cbd.code as brDistrictCode \n" +
                getListBaseSql(filter) +
                " order by f." + filter.getOrderBy() +
                " " + filter.getSortOrder().getName() +
                QueryUtils.getOffsetLimit(filter);

        List<SmartposFiscalizationListDTO> list = jdbcSpringManager.getSimpleJdbcTemplate().query(sql,
                BeanPropertyRowMapper.newInstance(SmartposFiscalizationListDTO.class));

        String countSql = "select count(f.id) \n" +
                getListBaseSql(filter);
        Query countQuery = entityManager.createNativeQuery(countSql);

        long count = ((Long) countQuery.getSingleResult());
        if (count > 0) {
            resultList.setList(list);
            resultList.setCount(count);
        }
        return resultList;
    }

    @Override
    public ResultList<Fiscalization> getResultList(BaseFilter filter) {
        ResultList<Fiscalization> resultList = new ResultList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("select f from Fiscalization f ");
        sql.append(" join f.device d ");
        sql.append(" join f.fiscalModule fm ");
        sql.append(" join f.customer c ");
        sql.append(" join f.branch b ");
        sql.append(" where f.deleted = false ");

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and f.status =:status ");
        }
        if (filter.getCustomerId() != null) {
            sql.append(" and c.id =:customerId ");
        }
        if (filter.getDeviceId() != null) {
            sql.append(" and d.id =:deviceId ");
        }
        if (filter.getFiscalModuleId() != null) {
            sql.append(" and fm.id =:fiscalModuleId ");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =:branchId ");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and b.companyId =:companyId ");
        }

        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(c.name) ").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin) ").append(filter.getLikeSearch());
            sql.append(" or lower(d.serialNumber) ").append(filter.getLikeSearch());
            sql.append(" or lower(fm.serialNumber) ").append(filter.getLikeSearch());
            sql.append(" )");
        }

        String countSql = sql.toString().replaceFirst("select f", " select count(f.id)");
        sql.append(" order by f.").append(filter.getOrderBy());
        sql.append(" ").append(filter.getSortOrder().getName());

        TypedQuery<Fiscalization> query = entityManager
                .createQuery(sql.toString(), Fiscalization.class)
                .setFirstResult(filter.getStart())
                .setMaxResults(filter.getSize());

        TypedQuery<Long> countQuery = entityManager.createQuery(countSql, Long.class);

        if (StringUtils.isNotEmpty(filter.getStatus())) {
            query.setParameter("status", DeviceStatus.valueOf(filter.getStatus()));
            countQuery.setParameter("status", DeviceStatus.valueOf(filter.getStatus()));
        }
        if (filter.getCustomerId() != null) {
            query.setParameter("customerId", filter.getCustomerId());
            countQuery.setParameter("customerId", filter.getCustomerId());
        }
        if (filter.getDeviceId() != null) {
            query.setParameter("deviceId", filter.getDeviceId());
            countQuery.setParameter("deviceId", filter.getDeviceId());
        }
        if (filter.getFiscalModuleId() != null) {
            query.setParameter("fiscalModuleId", filter.getFiscalModuleId());
            countQuery.setParameter("fiscalModuleId", filter.getFiscalModuleId());
        }
        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
            countQuery.setParameter("branchId", filter.getBranchId());
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
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select count(f.id) filter ( where f.status = 'FISCAL') as FISCAL,");
        sql.append(" count(f.id) filter ( where f.status = 'FISCAL_ACTIVE') as FISCAL_ACTIVE ");
        sql.append(" from fiscalization f ");
        sql.append(" join fiscal_module fm on fm.id = f.fiscal_module_id ");
        sql.append(" join device d on d.id = f.device_id ");
        sql.append(" join customer c on c.id = f.customer_id ");
        sql.append(" join branch b on b.id = f.branch_id ");
        sql.append(" where f.deleted = false");

        if (filter.getBranchId() != null) {
            sql.append(" and b.id = :branchId");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = :companyId");
        }
        if (StringUtils.isNotEmpty(filter.getStatus())) {
            sql.append(" and f.status = :status");
        }
        if (filter.getFrom() != null) {
            sql.append(" and f.created_date >= :fromDate");
        }
        if (filter.getTo() != null) {
            sql.append(" and f.created_date <= :toDate");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(fm.serial_number)").append(filter.getLikeSearch());
            sql.append(" or lower(d.serial_number)").append(filter.getLikeSearch());
            sql.append(" or lower(c.tin)").append(filter.getLikeSearch());
            sql.append(" or lower(c.name)").append(filter.getLikeSearch());
            sql.append(" )");
        }

        Query query = entityManager.createNativeQuery(sql.toString(), Map.class);

        if (filter.getBranchId() != null) {
            query.setParameter("branchId", filter.getBranchId());
        }
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

    private StringBuilder getListBaseSql(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("from fiscalization f \n");
        sql.append("left join customer c on c.id = f.customer_id \n");
        sql.append("left join customer_branch cb on cb.id = f.customer_branch_id \n");
        sql.append("left join device d on d.id = f.device_id  \n");
        sql.append("left join fiscal_module fm on fm.id = f.fiscal_module_id \n");
        sql.append("left join vat v on v.id = c.vat_id \n");
        sql.append("left join region r on r.id = c.region_id \n");
        sql.append("left join district cd on cd.id = c.district_id \n");
        sql.append("left join region cbr on cbr.id = cb.region_id \n");
        sql.append("left join district cbd on cbd.id = cb.district_id \n");
        sql.append("where cb.deleted = false and c.deleted = false and d.deleted = false and fm.deleted = false and f.deleted = false\n");

        if (filter.getCustomerId() != null) {
            sql.append(" and c.id=").append(filter.getCustomerId()).append("\n");
        }
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.isSearchNotEmpty()) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number) like (:searchKey) \n");
            sql.append(" or lower(fm.serial_number) like (:searchKey) \n");
            sql.append(" or lower(c.tin) like (:searchKey) \n");
            sql.append(" or lower(c.name) like (:searchKey) \n");
            sql.append(") ");
        }

        return sql;
    }

    @Override
    public List<DeviceFiscalModuleDTO> getLookupForOfd(BaseFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct f.id, d.serial_number deviceSerial, fm.serial_number fiscalModuleSerial from fiscalization f \n");
        sql.append(" inner join device d on d.id = f.device_id \n");
        sql.append(" inner join fiscal_module fm on fm.id = f.fiscal_module_id \n");
        sql.append(" inner join file_upload fu on fu.entity_id = f.customer_branch_id \n");
        sql.append(" left join ofd_application a on a.id = f.ofd_application_id \n");
        sql.append(" where f.deleted = false ");
        sql.append(" and fu.deleted = false ");
        sql.append(" and f.customer_branch_id = ").append(filter.getCustomerBranchId());
        sql.append(" and (f.ofd_application_id is null or a.ofd_status ='").append(OfdStatus.REJECT.getCode()).append("') \n");
        sql.append(" and fu.entity_type = '").append(EntityTypeEnum.CUSTOMER_BRANCH.name()).append("' \n");
        sql.append(" and fu.content_type = '").append(MediaType.APPLICATION_PDF_VALUE).append("' ");

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(d.serial_number)").append(filter.getLikeSearchKey());
            sql.append(" or lower(fm.serial_number)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }

        sql.append(" order by d.serial_number asc ");
        sql.append(" limit 20");
        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(DeviceFiscalModuleDTO.class));
    }

    @Override
    public ArrayList<BigInteger> getListToSendOfd() {
        String sql = "select distinct f.id from fiscalization f \n" +
                " inner join customer_branch cb on cb.id = f.customer_branch_id \n" +
                " inner join file_upload fu on fu.entity_id = cb.id \n" +
                " inner join file_upload fu1 on fu1.entity_id = f.id \n" +
                " where f.deleted = false " +
                " and fu.deleted = false " +
                " and fu1.deleted = false " +
                " and cb.deleted = false " +
                " and cb.ofd_id is not null \n" +
                " and f.ofd_application_id is null \n" +
                " and fu.entity_type = '" + EntityTypeEnum.CUSTOMER_BRANCH + "' \n" +
                " and fu.content_type = '" + MediaType.APPLICATION_PDF_VALUE + "' " +
                " and fu1.entity_type = '" + EntityTypeEnum.FISCALIZATION + "' " +
                " and fu1.content_type = '" + MediaType.APPLICATION_PDF_VALUE + "' " +
                " and fu1.document_type = '" + DocumentType.APPLICATION + "' ";

        Query listQuery = entityManager.createNativeQuery(sql);
        return (ArrayList<BigInteger>) listQuery.getResultList();
    }

    @Override
    public Long getActiveFiscalizationCount(Long customerId) {
        Query query = entityManager.createQuery(
                "select count(f.deviceId) from(select f.deviceId as deviceId from Fiscalization f " +
                        "join f.ofdApplication oa " +
                        "where f.deleted is false and oa.deleted is false " +
                        "and ((oa.applicationType = :type1 and oa.ofdStatus in (:status1)) " +
                        "or (oa.applicationType in (:type2)) " +
                        "or (oa.applicationType = :type3 and oa.ofdStatus = :status3)) " +
                        "and (f.customerId = :customerId) group by f.deviceId) f\n");
        query.setParameter("type1", ApplicationType.REGISTER);
        query.setParameter("status1", List.of(OfdStatus.ACCEPT, OfdStatus.PENDING));
        query.setParameter("type2", List.of(ApplicationType.DEREGISTER_DEVICE, ApplicationType.DEREGISTER_FISCAL_MODULE, ApplicationType.CHANGE_ADDRESS));
        query.setParameter("type3", ApplicationType.DEREGISTER);
        query.setParameter("status3", OfdStatus.REJECT);
        query.setParameter("customerId", customerId);
        return ((Long) query.getSingleResult());
    }
}
