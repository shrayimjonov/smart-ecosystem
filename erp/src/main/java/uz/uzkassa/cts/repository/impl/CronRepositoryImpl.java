package uz.uzkassa.cts.repository.impl;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.cron.CronDeviceSwitchDto;
import uz.uzkassa.cts.enums.PeriodType;
import uz.uzkassa.cts.repository.custom.CronRepositoryCustom;

import java.time.LocalDate;
import java.util.List;

@Component
public class CronRepositoryImpl extends BaseRepositoryImpl implements CronRepositoryCustom {

    @Override
    public List<CronDeviceSwitchDto> getCustomerDeviceList(boolean isInvoice, LocalDate localDate) {
        StringBuilder sql = new StringBuilder();
        sql.append("Select distinct cbd.id as customerDeviceBranchId, \n");
        sql.append(" cbd.deleted as deleted,\n");
        sql.append(" cbd.customer_branch_id as customerBranchId,\n");
        sql.append(" cbd.device_id as deviceId,\n");
        sql.append(" cbd.status as status,\n");
        sql.append(" cbd.due_date as dueDate,\n");
        sql.append(" cbd.is_free_trail_period as freeTrailPeriod,\n");
        sql.append(" cbd.start_date as startDate,\n");
        sql.append(" cbd.service_allowed as serviceAllowed,\n");
        sql.append(" cbd.tariff_id as tariffId, \n");
        sql.append(" t.name as tariffName,\n");
        sql.append(" t.period as tariffPeriod, \n");
        sql.append(" t.period_type as tariffPeriodType,\n");
        sql.append(" t.price as tariffPrice,\n");
        sql.append(" t.tariff_special_type as tariffSpecialType,\n");
        sql.append(" t.free_trail_period as tariffFreeTrailPeriod,\n");
        sql.append(" t.free_trail_period_type as tariffFreeTrailPeriodType,\n");
        sql.append(" c.id as customerId,\n");
        sql.append(" c.tin as customerTin, \n");
        sql.append(" cbd.contract_id as contractId, \n");
        sql.append(" d.serial_number as serialNumber, \n");
        sql.append(" f.status as fiscalizationStatus \n");
        sql.append(" from customer_branch_device cbd \n");
        sql.append(" inner join tariff t on t.id = cbd.tariff_id  \n");
        sql.append(" inner join customer_branch cb on cbd.customer_branch_id = cb.id  \n");
        sql.append(" inner join customer c on c.id = cb.customer_id  \n");
        sql.append(" inner join device d on cbd.device_id = d.id  \n");
        sql.append(" inner join fiscalization f on f.device_id = cbd.device_id  \n");
        sql.append(" where cbd.deleted = false  \n");
        if (!isInvoice) {
            if (localDate != null)
                sql.append(" and cbd.due_date < '").append(localDate.plusDays(2)).append("' ");
        }
        // INVOICE MUST WORK ONLY ON LAST DAY OF MONTH
        if (isInvoice) {
            if (localDate != null)
                sql.append(" and cbd.due_date < '").append(localDate).append("' ");
            sql.append(" and cbd.is_free_trail_period  = false");
            sql.append(" and cbd.status = 'UN_BLOCKED'");
            sql.append(" and t.period_type ='").append(PeriodType.MONTHLY).append("' ");
        }
        sql.append(" and cbd.service_allowed = false; ");

        return jdbcSpringManager.getSimpleJdbcTemplate()
                .query(sql.toString(), BeanPropertyRowMapper.newInstance(CronDeviceSwitchDto.class));
    }
}
