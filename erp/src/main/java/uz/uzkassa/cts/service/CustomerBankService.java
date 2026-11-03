package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.CustomerBank;
import uz.uzkassa.cts.dto.customerbank.CustomBankDTO;
import uz.uzkassa.cts.dto.customerbank.cabinet.CustomBankDetailCabinetDTO;
import uz.uzkassa.cts.dto.payload.CustomerPayload;

public interface CustomerBankService extends BaseService<CustomerBank, Long> {

    Long create(CustomBankDTO customBankDTO);

    Long update(Long id, CustomBankDTO customBankDTO);

    CustomBankDetailCabinetDTO get(Long id);

    CustomBankDetailCabinetDTO getByCustomerId(Long customerId);

    void sync();

    void syncQueue(CustomerPayload payload);
}
