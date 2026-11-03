package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.BankTariff;
import uz.uzkassa.cts.dto.banktariff.BankTariffDTO;
import uz.uzkassa.cts.dto.banktariff.BankTariffDetailDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.filter.BankTariffFilter;

import java.util.List;

public interface BankTariffService extends BaseService<BankTariff, Long> {

    Long create(BankTariffDTO dto);

    Long update(Long id, BankTariffDTO dto);

    Page<BankTariffDetailDTO> list(BankTariffFilter filter);

    List<CommonDTO> lookUp(BankTariffFilter filter);

    BankTariffDetailDTO get(Long id);

    void updateStatus(Long id, CommonStatus status);

    Long getId(String tin);

    Long getId(CustomerAppType appType, String tin, String serialNumber);
}
