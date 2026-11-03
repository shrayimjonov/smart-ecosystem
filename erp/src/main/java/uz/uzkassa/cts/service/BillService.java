package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Bill;
import uz.uzkassa.cts.dto.apay.APayUpdateBillStatusRequestDTO;
import uz.uzkassa.cts.dto.bill.*;
import uz.uzkassa.cts.filter.BillFilter;

public interface BillService extends BaseService<Bill, Long> {

    String create(BillDTO billDTO);

    void updateAPay(BillUpdateDTO billUpdateDTO);

    void confirmationPay(BillConfirmDTO billConfirmDTO);

    BillResponseDTO checkPay(String billId);

    void updateBillStatusByBillId(String billId, APayUpdateBillStatusRequestDTO billStatusRequestDTO);

    BillDetailDTO get(Long id);

    Page<BillDetailDTO> list(BillFilter filter);

    BillResponseDTO getByDevice(Long deviceId);

    void canceledOverTime();
}
