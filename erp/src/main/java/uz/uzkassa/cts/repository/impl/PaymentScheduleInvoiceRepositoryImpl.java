package uz.uzkassa.cts.repository.impl;

import jakarta.persistence.Query;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.repository.custom.PaymentScheduleInvoiceRepositoryCustom;

import java.util.List;

@SuppressWarnings("unused")
public class PaymentScheduleInvoiceRepositoryImpl extends BaseRepositoryImpl implements PaymentScheduleInvoiceRepositoryCustom {

    @Override
    public List<ActOfTransmission> changeActStatus() {
        Query query = entityManager.createQuery(
            "SELECT act FROM ActOfTransmission act " +
                " WHERE (select count(aps) from ActPaymentSchedule aps " +
                " where aps.actOfTransmissionDetail.actOfTransmission.id = act.id " +
                " and aps.invoiceId is null) =0 " +
                " and act.status <> 'CLOSED'");

        return (List<ActOfTransmission>) query.getResultList();
    }
}
