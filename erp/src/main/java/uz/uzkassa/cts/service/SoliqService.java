package uz.uzkassa.cts.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.domain.Bank;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.soliq.*;
import uz.uzkassa.cts.dto.tin.integration.OfdAccountDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.integrations.OfdClient;
import uz.uzkassa.cts.integrations.SoliqClient;
import uz.uzkassa.cts.repository.BankRepository;
import uz.uzkassa.cts.repository.DistrictRepository;
import uz.uzkassa.cts.repository.RegionRepository;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SoliqService implements MessageConstants {

    SoliqClient soliqClient;

    RegionRepository regionRepository;

    DistrictRepository districtRepository;

    BankRepository bankRepository;

    ValidationService validationService;

    OfdClient ofdClient;

    public CompanyInfoResultDTO getCompanyByTin(String tin) {

        CompanyInfoResultDTO result = new CompanyInfoResultDTO();

        SoliqCompanyDTO soliqCompanyDTO = soliqClient.getCustomerByTin(tin);

        Region region = regionRepository.findByCode(soliqCompanyDTO.getNs10Code());
        if (region == null) {
            throw validationService.entityNotFoundExceptionThrow(REGION, CODE, soliqCompanyDTO.getNs10Code().toString()).get();
        }

        District district = districtRepository.findFirstByRegionIdAndCode(region.getId(), soliqCompanyDTO.getNs11Code()).orElse(null);
        if (district == null) {
            throw validationService.entityNotFoundExceptionThrow(DISTRICT, CODE, soliqCompanyDTO.getNs11Code().toString()).get();
        }

        BigDecimal balance = BigDecimal.ZERO;
        OfdAccountDTO ofdAccountDTO = ofdClient.checkBalance(tin);
        if (ofdAccountDTO != null) {
            balance = ofdAccountDTO.getOverpaymentSum();
        }

        CompanyInfoDTO companyInfoDTO = new CompanyInfoDTO();
        CompanyOwnerDTO companyOwnerDTO = new CompanyOwnerDTO();

        companyInfoDTO.setName(soliqCompanyDTO.getName());
        companyInfoDTO.setTin(soliqCompanyDTO.getTin());
        companyInfoDTO.setAddress(soliqCompanyDTO.getAddress());
        companyInfoDTO.setRegionId(region.getId());
        companyInfoDTO.setDistrictId(district.getId());
        companyInfoDTO.setBalance(balance);

        if (StringUtils.isNotEmpty(soliqCompanyDTO.getDirector())) {
            String[] fullNames = soliqCompanyDTO.getDirector().split(" ");
            for (String name : fullNames) {
                if (StringUtils.isEmpty(companyOwnerDTO.getLastName())) {
                    companyOwnerDTO.setLastName(name.trim());
                    continue;
                }
                if (StringUtils.isEmpty(companyOwnerDTO.getFirstName())) {
                    companyOwnerDTO.setFirstName(name.trim());
                    continue;
                }
                if (StringUtils.isEmpty(companyOwnerDTO.getPatronymic())) {
                    companyOwnerDTO.setPatronymic(name.trim());
                }
            }
        }

        result.setCompany(companyInfoDTO);
        result.setOwner(companyOwnerDTO);

        return result;
    }

    public SoliqCompanyDTO getCompanyByTinTest(String tin) {
        return soliqClient.getCustomerByTin(tin);
    }

    public CompanyBankInfoDTO getBankByTin(String tin) {
        CompanyBankInfoDTO companyBankInfoDTO = new CompanyBankInfoDTO();
        SoliqCompanyDTO soliqCompanyDTO = soliqClient.getCustomerByTin(tin);
        companyBankInfoDTO.setAccountant(soliqCompanyDTO.getAccountant());
        companyBankInfoDTO.setAccountNumber(soliqCompanyDTO.getAccount());
        companyBankInfoDTO.setOked(soliqCompanyDTO.getOked());
        companyBankInfoDTO.setCode(soliqCompanyDTO.getMfo());

        if (StringUtils.isNotEmpty(soliqCompanyDTO.getMfo())) {
            Bank bank = bankRepository.findByCodeAndStatus(soliqCompanyDTO.getMfo(), CommonStatus.ACTIVE).orElse(null);
            if (bank != null) {
                companyBankInfoDTO.setId(bank.getId());
                companyBankInfoDTO.setName(bank.getNameNotNullable());
            }
        }

        return companyBankInfoDTO;
    }

}
