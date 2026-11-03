package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.repository.custom.ServicePaymentHistoryForCronRepositoryCustom;

import java.util.List;

@SuppressWarnings("unused")
public class ServicePaymentHistoryForCronRepositoryImpl extends BaseRepositoryImpl implements ServicePaymentHistoryForCronRepositoryCustom {

    @Override
    public List<Object[]> getCustomerDevices(String tin) {
        String sql = " Select distinct cbd.id as customerDeviceBranchId, \n" +
                " cbd.deleted as deleted,\n" +
                " cbd.customer_branch_id as customerBranchId,\n" +
                " cbd.device_id as deviceId,\n" +
                " cbd.status as status,\n" +
                " cbd.due_date as dueDate,\n" +
                " cbd.is_free_trail_period as freeTrailPeriod,\n" +
                " cbd.start_date as startDate,\n" +
                " cbd.service_allowed as serviceAllowed,\n" +
                " cbd.tariff_id as tariffId, \n" +
                " t.name as tariffName,\n" +
                " t.period as tariffPeriod, \n" +
                " t.period_type as tariffPeriodType,\n" +
                " t.price as tariffPrice, t.tariff_special_type as tariffSpecialType,\n" +
                " t.free_trail_period as tariffFreeTrailPeriod, t.free_trail_period_type as tariffFreeTrailPeriodType,\n" +
                " c.id as customerId, c.tin as customerTin, \n" +
                " cbd.contract_id as contractId, \n" +
                " d.serial_number as serialNumber, \n" +
                " f.status as fiscalizationStatus, \n" +
                " cbd.initial_date as initialDate, \n" +
                " cbd.suspend_status as suspendStatus, \n" +
                " cbd.suspend_date as suspendDate, \n" +
                " f.created_date as fiscalizationCreatedDate \n" +
//        sql.append(" sph.id as servicePaymentHistoryId, \n");
//        sql.append(" sph.month as servicePaymentHistoryMonth, \n");
//        sql.append(" sph.year as servicePaymentHistoryYear \n");
                " from customer_branch_device cbd \n" +
                " inner join tariff t on t.id = cbd.tariff_id  \n" +
                " inner join customer_branch cb on cbd.customer_branch_id = cb.id  \n" +
                " inner join customer c on c.id = cb.customer_id  \n" +
                " inner join device d on cbd.device_id = d.id  \n" +
                " inner join fiscalization f on f.device_id = cbd.device_id  \n" +
                " inner join ofd_application ofd on ofd.id = f.ofd_application_id  \n" +
//        sql.append(" left join service_payment_history sph on sph.customer_branch_device_id = cbd.id  \n");
                " where cbd.deleted = false  \n" +
                " and f.deleted = false  \n" +
                " and c.tin = '" + tin + "' " +
                " and cbd.service_allowed = false " +
                " and ofd.ofd_status = '" + OfdStatus.ACCEPT.getCode() + "' \n" +
                " and ofd.application_type != '" + ApplicationType.DEREGISTER.getCode() + "' \n" +
                " and c.app_type = '" + CustomerAppType.CTS.getCode() + "' \n";

//        sql.append(" and cbd.is_free_trail_period = false ");
//        sql.append(" and sph.month = '").append(month.getValue()).append("' ");
//        sql.append(" and sph.service_order_invoice_id is null; ");


        Query query = entityManager.createNativeQuery(sql);

        return (List<Object[]>) query.getResultList();
    }
}
