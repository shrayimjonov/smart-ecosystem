package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Bank;
import uz.uzkassa.cts.dto.bank.BankDTO;
import uz.uzkassa.cts.dto.bank.admin.BankDetailAdminDTO;
import uz.uzkassa.cts.dto.bank.admin.BankListAdminDTO;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface BankService extends BaseService<Bank, Long> {

    Long create(BankDTO bankDTO);

    Long update(Long id, BankDTO bankDTO);

    Page<BankListAdminDTO> getListForAdmin(BaseFilter filter);

    List<CommonCodeDTO> lookUp(BaseFilter filter);

    BankDetailAdminDTO get(Long id);

    void updateStatus(Long id, CommonStatus status);

    void sync();
}
