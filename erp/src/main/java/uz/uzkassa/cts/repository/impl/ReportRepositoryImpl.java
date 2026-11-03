package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.report.ReportTotalDTO;
import uz.uzkassa.cts.dto.report.StatisticsByActivityTypeDTO;
import uz.uzkassa.cts.dto.report.TotalBonusDTO;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.OrderType;
import uz.uzkassa.cts.filter.ActivityTypeReportFilter;
import uz.uzkassa.cts.filter.BaseReportFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.repository.custom.ReportRepositoryCustom;
import uz.uzkassa.cts.utils.DateUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("unused")
public class ReportRepositoryImpl extends BaseRepositoryImpl implements ReportRepositoryCustom {

    @Override
    public Map<String, HashMap<Long, BigInteger>> getStatisticsByBranchReport(BaseReportFilter filter) {
        Map<String, HashMap<Long, BigInteger>> resultMap = new HashMap<>();
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());


        //Customer
        StringBuilder sql = new StringBuilder();
        sql.append("select b.id, count(c.id) total \n");
        sql.append(" from customer c ");
        sql.append(" inner join branch b on c.branch_id = b.id \n");
        sql.append(" where c.deleted = false and b.deleted=false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and c.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by b.id order by b.id");
        List<ReportTotalDTO> resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> customerMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("customerMap", customerMap);


        //Customer Branch
        sql = new StringBuilder();
        sql.append("select b.id, count(cb.id) total \n");
        sql.append(" from customer_branch cb \n");
        sql.append(" inner join branch b on cb.branch_id = b.id \n");
        sql.append(" where cb.deleted = false and b.deleted=false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and cb.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> customerBranchMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("customerBranchMap", customerBranchMap);


        //Device Total
        sql = new StringBuilder();
        sql.append("select b.id, count(bd.id) total \n");
        sql.append(" from branch_device bd \n");
        sql.append(" inner join branch b on b.id = bd.branch_id \n");
        sql.append(" inner join device d on d.id = bd.device_id \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" where bd.deleted = false and b.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and bd.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and dm.id = ").append(filter.getModelId());
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> deviceTotalMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("deviceTotalMap", deviceTotalMap);


        //Free Device Total
        sql = new StringBuilder();
        sql.append("select b.id, count(bd.id) total \n");
        sql.append(" from branch_device bd \n");
        sql.append(" inner join device d on d.id = bd.device_id \n");
        sql.append(" inner join branch b on b.id = bd.branch_id \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" left join fiscalization f on f.device_id = d.id and d.deleted = false \n");
        sql.append(" left join customer_device cd on d.id = cd.device_id and cd.deleted = false \n");
        sql.append(" where bd.deleted = false \n");
        sql.append(" and d.deleted = false \n");
        sql.append(" and dm.deleted = false \n");
        sql.append(" and b.deleted = false \n");
        sql.append(" and f.id is null \n");
        sql.append(" and cd.id is null \n");
        sql.append(" and bd.status = '").append(DeviceStatus.REGISTERED).append("' \n");

        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and dm.id = ").append(filter.getModelId());
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and bd.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> freeDeviceMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("freeDeviceMap", freeDeviceMap);


        //Transferred Device Total
        sql = new StringBuilder();
        sql.append("select b.id, count(bd.id) total \n");
        sql.append(" from branch_device bd \n");
        sql.append(" inner join branch b on b.id = bd.branch_id \n");
        sql.append(" inner join device d on d.id = bd.device_id \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" where bd.deleted = false and b.deleted=false \n");
        sql.append(" and bd.status ='").append(DeviceStatus.TRANSFERRED).append("'\n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and dm.id = ").append(filter.getModelId());
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and bd.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> transferredDeviceMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("transferredDeviceMap", transferredDeviceMap);


        //Registered Device Total
        sql = new StringBuilder();
        sql.append("select b.id, count(bd.id) total \n");
        sql.append(" from branch_device bd \n");
        sql.append(" inner join branch b on b.id = bd.branch_id \n");
        sql.append(" inner join device d on d.id = bd.device_id \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" inner join fiscalization f on f.device_id = d.id \n");
        sql.append(" where bd.deleted = false and b.deleted = false and f.deleted = false \n");
        sql.append(" and bd.status ='").append(DeviceStatus.TRANSFERRED).append("'\n");
        sql.append(" and f.status ='").append(DeviceStatus.FISCAL_ACTIVE).append("'\n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and dm.id = ").append(filter.getModelId());
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and bd.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> registeredDeviceMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("registeredDeviceMap", registeredDeviceMap);


        //FM Total
        sql = new StringBuilder();
        sql.append("select b.id, count(bfm.id) total \n");
        sql.append(" from branch_fiscal_module bfm \n");
        sql.append(" inner join branch b on b.id = bfm.branch_id \n");
        sql.append(" where bfm.deleted = false and b.deleted=false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and bfm.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> fiscalModuleTotalMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("fiscalModuleTotalMap", fiscalModuleTotalMap);


        //Free FM Total
        sql = new StringBuilder();
        sql.append("select b.id, count(bfm.id) total \n");
        sql.append(" from branch_fiscal_module bfm \n");
        sql.append(" inner join fiscal_module fm on fm.id = bfm.fiscal_module_id \n");
        sql.append(" inner join branch b on b.id = bfm.branch_id \n");
        sql.append(" left join fiscalization f on f.fiscal_module_id = fm.id and f.deleted = false \n");
        sql.append(" left join customer_fiscal_module cfm on fm.id = cfm.fiscal_module_id and cfm.deleted = false \n");
        sql.append(" where bfm.deleted = false \n");
        sql.append(" and b.deleted = false \n");
        sql.append(" and fm.deleted = false \n");
        sql.append(" and f.id is null \n");
        sql.append(" and cfm.id is null \n");
        sql.append(" and bfm.status ='").append(DeviceStatus.REGISTERED).append("'\n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and bfm.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> freeFiscalModuleMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("freeFiscalModuleMap", freeFiscalModuleMap);


        //Transferred FM Total
        sql = new StringBuilder();
        sql.append("select b.id, count(bfm.id) total \n");
        sql.append(" from branch_fiscal_module bfm \n");
        sql.append(" inner join branch b on b.id = bfm.branch_id \n");
        sql.append(" where bfm.deleted = false and b.deleted=false \n");
        sql.append(" and bfm.status ='").append(DeviceStatus.TRANSFERRED).append("'\n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and bfm.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> transferredFiscalModuleMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("transferredFiscalModuleMap", transferredFiscalModuleMap);


        //Registered FM Total
        sql = new StringBuilder();
        sql.append("select b.id, count(bfm.id) total \n");
        sql.append(" from branch_fiscal_module bfm \n");
        sql.append(" inner join branch b on b.id = bfm.branch_id \n");
        sql.append(" inner join fiscalization f on f.fiscal_module_id = bfm.fiscal_module_id \n");
        sql.append(" inner join device d on d.id = f.device_id \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" where bfm.deleted = false and b.deleted = false and f.deleted = false \n");
        sql.append(" and bfm.status ='").append(DeviceStatus.TRANSFERRED).append("'\n");
        sql.append(" and f.status ='").append(DeviceStatus.FISCAL_ACTIVE).append("'\n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and bfm.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and dm.id = ").append(filter.getModelId());
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> registeredFiscalModuleMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("registeredFiscalModuleMap", registeredFiscalModuleMap);


        //Fiscal
        sql = new StringBuilder();
        sql.append("select b.id, count(f.id) total \n");
        sql.append(" from fiscalization f \n");
        sql.append(" inner join branch b on b.id = f.branch_id \n");
        sql.append(" inner join device d on d.id = f.device_id \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" where f.deleted = false \n");
        sql.append(" and b.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and dm.id = ").append(filter.getModelId());
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and f.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> fiscalizationMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("fiscalizationMap", fiscalizationMap);


        //Applications
        sql = new StringBuilder();
        sql.append("select b.id, count(a.id) total \n");
        sql.append(" from ofd_application a \n");
        sql.append(" inner join customer c on c.id = a.customer_id \n");
        sql.append(" inner join branch b on b.id = c.branch_id \n");
        sql.append(" inner join device d on d.id = a.device_id \n");
        sql.append(" inner join device_model dm on dm.id = d.model_id \n");
        sql.append(" where c.deleted = false and b.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and a.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        if (filter.getModelId() != null) {
            sql.append(" and dm.id = ").append(filter.getModelId());
        }
        sql.append(" group by b.id order by b.id");
        resultList = jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(ReportTotalDTO.class));
        HashMap<Long, BigInteger> applicationMap = resultList.stream().collect(Collectors.toMap(ReportTotalDTO::getId, ReportTotalDTO::getTotal, (a, b) -> b, HashMap::new));
        resultMap.put("applicationMap", applicationMap);


        return resultMap;
    }

    @Override
    public List<StatisticsByActivityTypeDTO> getCustomersByActivityType(ActivityTypeReportFilter filter) {
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());

        StringBuilder sql = new StringBuilder();
        sql.append("select cat.id categoryId, cat.name_ru category, act.id activityTypeId, act.name_ru as activityType, count(c.id) customerTotal, r.id regionId, r.name_ru as region \n");
        sql.append(" from customer c ");
        sql.append(" inner join region r on r.id = c.region_id \n");
        sql.append(" inner join activity_type act on act.id = c.activity_type_id \n");
        sql.append(" inner join activity_type cat on cat.id = c.parent_activity_type_id \n");
        sql.append(" where c.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and c.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        if (filter.getActivityTypeId() != null) {
            sql.append(" and act.id=").append(filter.getActivityTypeId());
        }
        if (filter.getParentActivityTypeId() != null) {
            sql.append(" and cat.id=").append(filter.getParentActivityTypeId());
        }
        sql.append(" group by act.id, r.id, cat.id order by act.id, r.id");

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(StatisticsByActivityTypeDTO.class));
    }

    @Override
    public List<StatisticsByActivityTypeDTO> getCustomersByActivityTypeAndRegion(ActivityTypeReportFilter filter, Region region) {
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());
        StringBuilder sql = new StringBuilder();
        sql.append("select cat.id categoryId, cat.name_ru category, act.id activityTypeId, act.name_ru as activityType, count(c.id) customerTotal, d.id districtId, d.name_ru as district \n");
        sql.append(" from customer c ");
        sql.append(" inner join district d on d.id = c.district_id \n");
        sql.append(" inner join activity_type act on act.id = c.activity_type_id \n");
        sql.append(" inner join activity_type cat on cat.id = c.parent_activity_type_id \n");
        sql.append(" where c.deleted = false \n");
        sql.append(" and d.region_id=").append(region.getId());
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and c.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        if (filter.getActivityTypeId() != null) {
            sql.append(" and act.id=").append(filter.getActivityTypeId());
        }
        if (filter.getParentActivityTypeId() != null) {
            sql.append(" and cat.id=").append(filter.getParentActivityTypeId());
        }
        sql.append(" group by act.id, d.id, cat.id order by act.id, d.id");

        return jdbcSpringManager.getSimpleJdbcTemplate().query(sql.toString(), BeanPropertyRowMapper.newInstance(StatisticsByActivityTypeDTO.class));
    }

    @Override
    public Map<String, HashMap<Long, Long>> getStatisticsByRegionReport(BaseReportFilter filter) {
        Map<String, HashMap<Long, Long>> resultMap = new HashMap<>();
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());

        //Customer
        StringBuilder sql = new StringBuilder();
        sql.append("select r.id, count(c.id) total \n");
        sql.append(" from customer c ");
        sql.append(" inner join region r on c.region_id = r.id \n");
        sql.append(" where c.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and c.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by r.id order by r.id");

        Query listQuery = entityManager.createNativeQuery(sql.toString());
        List<Object[]> objects = listQuery.getResultList();
        HashMap<Long, Long> customerMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("customerMap", customerMap);

        //Customer Branch
        sql = new StringBuilder();
        sql.append("select r.id, count(cb.id) total \n");
        sql.append(" from customer_branch cb \n");
        sql.append(" inner join customer c on cb.customer_id = c.id \n");
        sql.append(" inner join region r on cb.region_id = r.id \n");
        sql.append(" where cb.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        /*if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }*/
        if (fromDate != null && toDate != null) {
            sql.append(" and cb.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by r.id order by r.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> customerBranchMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("customerBranchMap", customerBranchMap);

        //KKM
        sql = new StringBuilder();
        sql.append("select r.id, count(cd.id) total \n");
        sql.append(" from customer_device cd \n");
        sql.append(" inner join customer c on c.id = cd.customer_id \n");
        sql.append(" inner join region r on r.id = c.region_id \n");
        sql.append(" where cd.deleted = false \n");
        sql.append(" and c.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        /*if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }*/
        if (fromDate != null && toDate != null) {
            sql.append(" and cd.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by r.id order by r.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> deviceMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("deviceMap", deviceMap);

        //FM
        sql = new StringBuilder();
        sql.append("select r.id, count(cfm.id) total \n");
        sql.append(" from customer_fiscal_module cfm \n");
        sql.append(" inner join customer c on c.id = cfm.customer_id \n");
        sql.append(" inner join region r on r.id = c.region_id \n");
        sql.append(" where cfm.deleted = false \n");
        sql.append(" and c.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        /*if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }*/
        if (fromDate != null && toDate != null) {
            sql.append(" and cfm.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by r.id order by r.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> fiscalModuleMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("fiscalModuleMap", fiscalModuleMap);

        //Fiscal
        sql = new StringBuilder();
        sql.append("select r.id, count(f.id) total \n");
        sql.append(" from fiscalization f \n");
        sql.append(" inner join customer c on c.id = f.customer_id \n");
        sql.append(" inner join region r on r.id = c.region_id \n");
        sql.append(" where f.deleted = false \n");
        sql.append(" and c.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        /*if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }*/
        if (fromDate != null && toDate != null) {
            sql.append(" and f.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by r.id order by r.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> fiscalizationMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("fiscalizationMap", fiscalizationMap);

        //Applications
        sql = new StringBuilder();
        sql.append("select r.id, count(a.id) total \n");
        sql.append(" from ofd_application a \n");
        sql.append(" inner join customer c on c.id = a.customer_id \n");
        sql.append(" inner join region r on r.id = c.region_id \n");
        sql.append(" where c.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        /*if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }*/
        if (fromDate != null && toDate != null) {
            sql.append(" and a.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by r.id order by r.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> applicationMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("applicationMap", applicationMap);

        return resultMap;
    }

    @Override
    public Map<String, HashMap<Long, Long>> getStatisticsByDistrictReport(BaseReportFilter filter, Region region) {
        Map<String, HashMap<Long, Long>> resultMap = new HashMap<>();
        LocalDateTime fromDate = DateUtil.getStartDate(filter.getFromDate());
        LocalDateTime toDate = DateUtil.getEndDate(filter.getToDate());

        //Customer
        StringBuilder sql = new StringBuilder();
        sql.append("select d.id, count(c.id) total \n");
        sql.append(" from customer c \n");
        sql.append(" inner join district d on d.id = c.district_id \n");
        sql.append(" where c.deleted = false \n");
        sql.append(" and d.region_id=").append(region.getId());
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and c.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by d.id order by d.id");

        Query listQuery = entityManager.createNativeQuery(sql.toString());
        List<Object[]> objects = listQuery.getResultList();
        HashMap<Long, Long> customerMap = new HashMap<>();
        for (Object[] obj : objects) {
            customerMap.put(((Long) obj[0]), ((Long) obj[1]));
        }
        resultMap.put("customerMap", customerMap);

        //Customer Branch
        sql = new StringBuilder();
        sql.append("select d.id, count(cb.id) total \n");
        sql.append(" from customer_branch cb \n");
        sql.append(" inner join customer c on c.id = cb.customer_id \n");
        sql.append(" inner join district d on d.id = cb.district_id \n");
        sql.append(" where cb.deleted = false \n");
        sql.append(" and d.region_id=").append(region.getId());
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and cb.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by d.id order by d.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> customerBranchMap = new HashMap<>();
        for (Object[] obj : objects) {
            customerBranchMap.put(((Long) obj[0]), ((Long) obj[1]));
        }
        resultMap.put("customerBranchMap", customerBranchMap);

        //KKM
        sql = new StringBuilder();
        sql.append("select d.id, count(cd.id) total \n");
        sql.append(" from customer_device cd \n");
        sql.append(" inner join customer c on c.id = cd.customer_id \n");
        sql.append(" inner join district d on d.id = c.district_id \n");
        sql.append(" where cd.deleted = false \n");
        sql.append(" and d.region_id=").append(region.getId());
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and cd.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by d.id order by d.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> deviceMap = new HashMap<>();
        for (Object[] obj : objects) {
            deviceMap.put(((Long) obj[0]), ((Long) obj[1]));
        }
        resultMap.put("deviceMap", deviceMap);

        //FM
        sql = new StringBuilder();
        sql.append("select d.id, count(cfm.id) total \n");
        sql.append(" from customer_fiscal_module cfm \n");
        sql.append(" inner join customer c on c.id = cfm.customer_id \n");
        sql.append(" inner join district d on d.id = c.district_id \n");
        sql.append(" where cfm.deleted = false \n");
        sql.append(" and d.region_id=").append(region.getId());
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and cfm.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by d.id order by d.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> fiscalModuleMap = new HashMap<>();
        for (Object[] obj : objects) {
            fiscalModuleMap.put(((Long) obj[0]), ((Long) obj[1]));
        }
        resultMap.put("fiscalModuleMap", fiscalModuleMap);

        //Fiscalization Total
        sql = new StringBuilder();
        sql.append("select d.id, count(f.id) total \n");
        sql.append(" from fiscalization f \n");
        sql.append(" inner join customer c on c.id = f.customer_id \n");
        sql.append(" inner join district d on d.id = c.district_id \n");
        sql.append(" where f.deleted = false \n");
        sql.append(" and d.region_id=").append(region.getId());
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and f.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by d.id order by d.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> fiscalizationMap = new HashMap<>();
        for (Object[] obj : objects) {
            fiscalizationMap.put(((Long) obj[0]), ((Long) obj[1]));
        }
        resultMap.put("fiscalizationMap", fiscalizationMap);

        //Application Total
        sql = new StringBuilder();
        sql.append("select d.id, count(a.id) total \n");
        sql.append(" from ofd_application a \n");
        sql.append(" inner join customer c on c.id = a.customer_id \n");
        sql.append(" inner join district d on d.id = c.district_id \n");
        sql.append(" where c.deleted = false \n");
        sql.append(" and d.region_id=").append(region.getId());
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and a.created_date between '").append(fromDate).append("' and '").append(toDate).append("' \n");
        }
        sql.append(" group by d.id order by d.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> applicationMap = new HashMap<>();
        for (Object[] obj : objects) {
            applicationMap.put(((Long) obj[0]), ((Long) obj[1]));
        }
        resultMap.put("applicationMap", applicationMap);

        return resultMap;
    }

    @Override
    public Map<String, HashMap<Long, Long>> getStatisticsByEmployeeReport(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate) {
        Map<String, HashMap<Long, Long>> resultMap = new HashMap<>();

        //Customer
        StringBuilder sql = new StringBuilder();
        sql.append("select u.id, count(c.id) total \n");
        sql.append(" from customer c \n");
        sql.append(" inner join jhi_user u on u.id = c.creator_id \n");
        sql.append(" inner join branch b on b.id = u.branch_id \n");
        sql.append(" where c.deleted = false \n");
        sql.append(" and b.deleted = false \n");
        sql.append(" and u.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and c.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and c.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and (c.created_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(" or lower(b.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" group by u.id order by u.id");

        Query listQuery = entityManager.createNativeQuery(sql.toString());
        List<Object[]> objects = listQuery.getResultList();
        HashMap<Long, Long> customerMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("customerMap", customerMap);

        //Customer Branch
        sql = new StringBuilder();
        sql.append("select u.id, count(cb.id) total \n");
        sql.append(" from customer_branch cb \n");
        sql.append(" inner join jhi_user u on u.id = cb.creator_id \n");
        sql.append(" inner join branch b on b.id = u.branch_id \n");
        sql.append(" where cb.deleted = false \n");
        sql.append(" and b.deleted = false \n");
        sql.append(" and u.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and (cb.created_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(" or lower(b.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" group by u.id order by u.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> customerBranchMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("customerBranchMap", customerBranchMap);

        //KKM
        sql = new StringBuilder();
        sql.append("select u.id, count(cd.id) total \n");
        sql.append(" from customer_device cd \n");
        sql.append(" inner join jhi_user u on u.id = cd.creator_id \n");
        sql.append(" inner join branch b on b.id = u.branch_id \n");
        sql.append(" where cd.deleted = false \n");
        sql.append(" and b.deleted = false \n");
        sql.append(" and u.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and (cd.created_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(" or lower(b.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" group by u.id order by u.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> deviceMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("deviceMap", deviceMap);

        //FM
        sql = new StringBuilder();
        sql.append("select u.id, count(cfm.id) total \n");
        sql.append(" from customer_fiscal_module cfm \n");
        sql.append(" inner join jhi_user u on u.id = cfm.creator_id \n");
        sql.append(" inner join branch b on b.id = u.branch_id \n");
        sql.append(" where cfm.deleted = false \n");
        sql.append(" and b.deleted = false \n");
        sql.append(" and u.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and b.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and b.company_id = ").append(getCompanyId()).append("\n");
        }
        if (fromDate != null && toDate != null) {
            sql.append(" and (cfm.created_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and b.id =").append(filter.getBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(" or lower(b.name)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" group by u.id order by u.id");

        listQuery = entityManager.createNativeQuery(sql.toString());
        objects = listQuery.getResultList();
        HashMap<Long, Long> fiscalModuleMap = objects.stream().collect(Collectors.toMap(obj -> ((Long) obj[0]), obj -> ((Long) obj[1]), (a, b) -> b, HashMap::new));
        resultMap.put("fiscalModuleMap", fiscalModuleMap);

        return resultMap;
    }

    @Override
    public HashMap<Long, List<TotalBonusDTO>> getServicesMap(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate) {
        //Service
        StringBuilder sql = new StringBuilder();
        sql.append("""
                SELECT u.id, '' as type, count(oto.id) total, case
                           when s.with_quantity = false then  coalesce(oto.bonus,0) * count(oto.id)
                           else coalesce(oto.bonus * oto.total_product_count,0) * count(oto.id)
                           end as bonus\s
                """);
        sql.append(" from one_time_order oto \n");
        sql.append(" inner join jhi_user u on u.id = oto.creator_id \n");
        sql.append(" inner join cts_service s on s.id = oto.service_id \n");
        sql.append(" where oto.deleted = false \n");
        sql.append(" and u.deleted = false \n");
        sql.append(" and oto.status !='").append(ContractStatus.REJECTED.getCode()).append("' \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and u.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and u.company_id = ").append(getCompanyId()).append("\n");
        }
        if (toDate != null && fromDate != null) {
            sql.append(" and (oto.created_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and u.branch_id =").append(filter.getBranchId());
        }
        if (filter.getServiceId() != null && filter.getServiceId() > 0) {
            sql.append(" and oto.service_id =").append(filter.getServiceId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" group by u.id, s.id, oto.bonus, oto.total_product_count  order by u.id");

        Query listQuery = entityManager.createNativeQuery(sql.toString());
        List<Object[]> objects = listQuery.getResultList();

        HashMap<Long, List<TotalBonusDTO>> servicesMap = new HashMap<>();

        for (Object[] obj : objects) {
            if (servicesMap.get((Long) obj[0]) == null) {
                List<TotalBonusDTO> list = new ArrayList<>();
                servicesMap.put((Long) obj[0], list);
            }
            servicesMap.get((Long) obj[0]).add(new TotalBonusDTO((String) obj[1], (Long) obj[2], ((BigDecimal) obj[3]).longValue()));
        }
        return servicesMap;
    }

    @Override
    public HashMap<Long, List<TotalBonusDTO>> getTariffsMap(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate) {
        //Tariffs
        StringBuilder sql = new StringBuilder();
        sql.append("select u.id, t.period_type as type, count(sp.id) total, coalesce(sp.bonus,0) * count(sp.id) bonus \n");
        sql.append(" from public.cts_service_order_specification sp \n");
        sql.append(" inner join public.cts_service_order so on so.id = sp.order_id \n");
        sql.append(" inner join public.order_tariff ot on ot.service_order_id = so.id \n");
        sql.append(" inner join public.device_tariff dt on dt.order_tariff_id = ot.id \n");
        sql.append(" inner join public.tariff t on t.id = ot.tariff_id \n");
        sql.append(" inner join  public.jhi_user u on u.id = ot.creator_id \n");
        sql.append(" where sp.deleted = false \n");
        sql.append(" and u.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and u.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and u.company_id = ").append(getCompanyId()).append("\n");
        }
        if (toDate != null && fromDate != null) {
            sql.append(" and (sp.created_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and u.branch_id =").append(filter.getBranchId());
        }
        if (filter.getTariffId() != null && filter.getTariffId() > 0) {
            sql.append(" and t.id =").append(filter.getTariffId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" group by u.id, t.id, sp.bonus order by u.id ");

        Query listQuery = entityManager.createNativeQuery(sql.toString());
        List<Object[]> objects = listQuery.getResultList();

        HashMap<Long, List<TotalBonusDTO>> tariffsMap = new HashMap<>();
        for (Object[] obj : objects) {
            if (tariffsMap.get((Long) obj[0]) == null) {
                List<TotalBonusDTO> list = new ArrayList<>();
                tariffsMap.put((Long) obj[0], list);
            }
            tariffsMap.get((Long) obj[0]).add(new TotalBonusDTO(((String) obj[1]), (Long) obj[2], ((BigDecimal) obj[3]).longValue()));
        }

        return tariffsMap;
    }

    @Override
    public HashMap<Long, List<TotalBonusDTO>> getLeasingMap(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate) {
        //LEASING

        StringBuilder sql = new StringBuilder();
        sql.append("select u.id, mph.order_type as type, count(actd.id) total, coalesce(actd.bonus,0) * count(actd.id) bonus \n");
        sql.append(" from act_of_transmission_detail actd \n");
        sql.append(" inner join act_of_transmission act on act.id = actd.act_id \n");
        sql.append(" inner join contract_for_leasing cfl on cfl.id = act.contract_id \n");
        sql.append(" inner join jhi_user u on u.id = act.creator_id \n");
        sql.append(" inner join model_price_history mph on cfl.model_price_history_id = mph.id \n");
        sql.append(" where u.deleted = false and act.deleted=false and cfl.deleted = false and mph.deleted = false \n");
        sql.append(" and act.status='").append(ContractStatus.ACCEPTED.getCode()).append("'\n");
        sql.append(" and mph.order_type='").append(OrderType.LEASING.getCode()).append("'\n");
        sql.append(" and cfl.status='").append(ContractStatus.ACCEPTED.getCode()).append("'\n");
        if (filter.getCompanyId() != null) {
            sql.append(" and u.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and u.company_id = ").append(getCompanyId()).append("\n");
        }
        if (toDate != null && fromDate != null) {
            sql.append(" and (act.accept_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and u.branch_id =").append(filter.getBranchId());
        }
        if (filter.getLeasingId() != null && filter.getLeasingId() > 0) {
            sql.append(" and mph.id =").append(filter.getLeasingId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" group by u.id, mph.id, actd.bonus order by u.id ");

        Query listQuery = entityManager.createNativeQuery(sql.toString());
        List<Object[]> objects = listQuery.getResultList();

        HashMap<Long, List<TotalBonusDTO>> leasingMap = new HashMap<>();
        for (Object[] obj : objects) {
            if (leasingMap.get((Long) obj[0]) == null) {
                List<TotalBonusDTO> list = new ArrayList<>();
                leasingMap.put((Long) obj[0], list);
            }
            leasingMap.get((Long) obj[0]).add(new TotalBonusDTO(((String) obj[1]), ((Long) obj[2]), ((BigDecimal) obj[3]).longValue()));
        }
        return leasingMap;
    }

    @Override
    public HashMap<Long, List<TotalBonusDTO>> getTradeInMap(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate) {
        //TRADE IN

        StringBuilder sql = new StringBuilder();
        sql.append("select u.id, '' as type, count(tia.id) total, coalesce(tia.bonus,0) * count(tia.id) bonus \n");
        sql.append(" from trade_in_act tia \n");
        sql.append(" inner join jhi_user u on u.id = tia.creator_id \n");
        sql.append(" where u.deleted = false and tia.deleted=false \n");
        sql.append(" and tia.status='").append(ContractStatus.ACCEPTED.getCode()).append("'\n");
        if (filter.getCompanyId() != null) {
            sql.append(" and u.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and u.company_id = ").append(getCompanyId()).append("\n");
        }
        if (toDate != null && fromDate != null) {
            sql.append(" and (tia.accept_date between '").append(fromDate).append("' and '").append(toDate).append("') \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and u.branch_id =").append(filter.getBranchId());
        }

        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" group by u.id, tia.bonus order by u.id ");

        Query listQuery = entityManager.createNativeQuery(sql.toString());
        List<Object[]> objects = listQuery.getResultList();

        HashMap<Long, List<TotalBonusDTO>> tradeInMap = new HashMap<>();
        for (Object[] obj : objects) {
            if (tradeInMap.get((Long) obj[0]) == null) {
                List<TotalBonusDTO> list = new ArrayList<>();
                tradeInMap.put((Long) obj[0], list);
            }
            tradeInMap.get((Long) obj[0]).add(new TotalBonusDTO(((String) obj[1]), ((Long) obj[2]), ((BigDecimal) obj[3]).longValue()));
        }
        return tradeInMap;
    }

    @Override
    public HashMap<Long, List<TotalBonusDTO>> getRent(EmployeeReportFilter filter, LocalDateTime fromDate, LocalDateTime toDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("select u.id, 'RENT' as type, count(*) total, coalesce(rph.bonus,0) * count(*) bonus \n");
        sql.append(" from rent_payment_history rph \n");
        sql.append(" inner join jhi_user u on u.id = rph.creator_id \n");
        sql.append(" where u.deleted = false and rph.deleted = false \n");
        if (filter.getCompanyId() != null) {
            sql.append(" and u.company_id = ").append(filter.getCompanyId()).append("\n");
        }
        if (!isSuperAdmin()) {
            sql.append(" and u.company_id = ").append(getCompanyId()).append("\n");
        }
        if (toDate != null && fromDate != null) {
            sql.append(" and (rph.start_date >= '").append(fromDate.toLocalDate()).append("' and rph.due_date <='").append(toDate.toLocalDate()).append("') \n");
        }
        if (filter.getBranchId() != null) {
            sql.append(" and u.branch_id =").append(filter.getBranchId());
        }
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            sql.append(" and (");
            sql.append(" lower(u.first_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.last_name)").append(filter.getLikeSearchKey());
            sql.append(" or lower(u.patronymic)").append(filter.getLikeSearchKey());
            sql.append(") ");
        }
        sql.append(" group by u.id, rph.bonus order by u.id ");

        Query listQuery = entityManager.createNativeQuery(sql.toString());
        List<Object[]> objects = listQuery.getResultList();

        HashMap<Long, List<TotalBonusDTO>> rentMap = new HashMap<>();
        for (Object[] obj : objects) {
            if (rentMap.get((Long) obj[0]) == null) {
                List<TotalBonusDTO> list = new ArrayList<>();
                rentMap.put((Long) obj[0], list);
            }
            rentMap.get((Long) obj[0]).add(new TotalBonusDTO(((String) obj[1]), ((Long) obj[2]), ((BigDecimal) obj[3]).longValue()));
        }
        return rentMap;
    }
}
