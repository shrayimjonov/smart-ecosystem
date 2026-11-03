package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.Bank;
import uz.uzkassa.smartpossupply.dto.bank.BankLookUpDTO;
import uz.uzkassa.smartpossupply.dto.companybank.CompanyBankDetailDTO;
import uz.uzkassa.smartpossupply.dto.soliq.TaxCompanyDTO;
import uz.uzkassa.smartpossupply.filters.BankFilter;
import uz.uzkassa.smartpossupply.integration.SoliqClient;
import uz.uzkassa.smartpossupply.repository.BankRepository;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BankService extends BaseService {

    BankRepository bankRepository;
    SoliqClient soliqClient;

    public List<BankLookUpDTO> lookUp(BankFilter filter) {
        List<BankLookUpDTO> result = bankRepository.lookUp(filter)
            .stream()
            .map(Bank::toLookUpDTO)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(result) && filter.getParentId() != null) {
            bankRepository.findById(filter.getParentId()).ifPresent(bank -> result.addFirst(bank.toLookUpDTO()));
        }
        return result;
    }

    public CompanyBankDetailDTO checkByTin(String tin) {
        TaxCompanyDTO taxCompanyDTO = soliqClient.getCustomerByTin(tin);
        CompanyBankDetailDTO result = new CompanyBankDetailDTO();
        result.setAccountNumber(taxCompanyDTO.getAccount());
        result.setOked(taxCompanyDTO.getOked());
        result.setMain(true);
        if (StringUtils.isNotEmpty(taxCompanyDTO.getMfo())) {
            Bank bank = bankRepository.findByMfo(taxCompanyDTO.getMfo()).orElse(null);
            if (bank != null) {
                result.setBank(bank.toLookUpDTO());
                if (bank.getParentId() != null) {
                    bankRepository.findById(bank.getParentId()).ifPresent(parentBank -> result.setParentBank(parentBank.toLookUpDTO()));
                } else {
                    result.setParentBank(bank.toLookUpDTO());
                }
            }
        }
        return result;
    }
}
