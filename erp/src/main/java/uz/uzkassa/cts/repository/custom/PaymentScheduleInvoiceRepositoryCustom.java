package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.domain.ActOfTransmission;

import java.util.List;

@Repository
public interface PaymentScheduleInvoiceRepositoryCustom {

    List<ActOfTransmission> changeActStatus();
}
