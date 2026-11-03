package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.company.CompanyBank;
import uz.uzkassa.horeca.dto.companybank.CompanyBankDTO;
import uz.uzkassa.horeca.dto.companybank.CompanyBankDetailDTO;
import uz.uzkassa.horeca.repository.CompanyBankRepository;

@Service
@Slf4j

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CompanyBankService extends BaseService {

    CompanyBankRepository companyBankRepository;

    @Transactional
    public Long create(CompanyBankDTO companyBankDTO) {
        Long companyId = getCurrentCompanyId(companyBankDTO.getCompanyId());
        CompanyBank companyBank = companyBankRepository
                .findFirstByCompanyIdAndBankIdAndDeletedIsFalse(companyId, companyBankDTO.getBankId())
                .orElse(new CompanyBank());
        companyBank.setBankId(companyBankDTO.getBankId());
        companyBank.setCompanyId(companyId);
        companyBank.setAccountNumber(companyBankDTO.getAccountNumber());
        return companyBankRepository.save(companyBank).getId();
    }

    @Transactional
    public Long update(Long id, CompanyBankDTO companyBankDTO) {
        return companyBankRepository
                .findById(id)
                .map(companyBank -> {
                    companyBank.setBankId(companyBankDTO.getBankId());
                    companyBank.setAccountNumber(companyBankDTO.getAccountNumber());
                    companyBankRepository.save(companyBank);
                    return id;
                })
                .orElseThrow(notFoundExceptionThrow(CompanyBank.class.getSimpleName(), "id", id));
    }

    public CompanyBankDetailDTO get() {
        return companyBankRepository
                .findFirstByCompanyIdAndDeletedIsFalse(getCurrentCompanyId())
                .map(companyBank -> {
                    CompanyBankDetailDTO companyBankDetailDTO = new CompanyBankDetailDTO();
                    companyBankDetailDTO.setId(companyBank.getId());
                    companyBankDetailDTO.setBank(companyBank.getBank().toLookUpDTO());
                    companyBankDetailDTO.setAccountNumber(companyBank.getAccountNumber());
                    companyBankDetailDTO.setOked(companyBank.getCompany().getOked());
                    return companyBankDetailDTO;
                })
                .orElse(null);
    }
}
