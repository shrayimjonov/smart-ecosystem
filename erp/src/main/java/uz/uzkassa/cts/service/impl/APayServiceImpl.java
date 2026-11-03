package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.address.District;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.dto.apay.*;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.integrations.ApayClient;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.APayService;
import uz.uzkassa.cts.service.SoliqService;
import uz.uzkassa.cts.service.ValidationService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class APayServiceImpl implements APayService, MessageConstants {

    ApplicationProperties applicationProperties;
    ApayClient apayClient;
    CustomerRepository customerRepository;
    SoliqService soliqService;
    RegionRepository regionRepository;
    DistrictRepository districtRepository;
    BankRepository bankRepository;
    CustomerEmployeeRepository customerEmployeeRepository;
    ValidationService validationService;

    @Override
    public APayAddTransactionResponseDto add(APayAddTransactionRequestDto dto) {
        return apayClient.add(dto);
    }

    @Override
    public APayAddTransactionResponseDto createBillTransaction(APayBillCreateRequestDTO dto) {
        return apayClient.createBillTransaction(dto);
    }

    @Override
    public void updateBillTransaction(APayBillUpdateRequestDTO dto) {
        apayClient.updateBillTransaction(dto);
    }

    @Override
    public APayPayResponseDto pay(APayPayRequestDto dto) {
        return apayClient.pay(dto);
    }

    @Override
    public APayPayResponseDto confirm(APayPayRequestDto dto) {
        return apayClient.confirm(dto);
    }

    @Override
    public APayCheckResponseDto check(APayCheckRequestDto dto) {
        return apayClient.check(dto);
    }

    @Override
    public APayResponseDTO getByBillId(String billId) {
        return apayClient.getByBillId(billId);
    }

    @Override
    public OfdPaymentInfoResponseDTO fiscal(OfdBillFiscalRequestDTO requestDTO) {
        return apayClient.fiscal(requestDTO);
    }

    @Override
    public ApayCustomerDetailDTO getCustomerByTin(String tin) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(tin, tin);
        ApayCustomerDetailDTO apayCustomerDetailDTO = new ApayCustomerDetailDTO();
        if (customer != null) {
            apayCustomerDetailDTO.setFromCts(true);
            apayCustomerDetailDTO.setId(customer.getId());
            apayCustomerDetailDTO.setName(customer.getName());
            apayCustomerDetailDTO.setCompanyName(customer.getCompanyName());
            apayCustomerDetailDTO.setTin(customer.getTin());
            apayCustomerDetailDTO.setPhone(customer.getPhone());
            apayCustomerDetailDTO.setBusinessType(customer.getBusinessType());
            apayCustomerDetailDTO.setActivityType(new ApayActivityTypeDTO(customer.getActivityType()));
            apayCustomerDetailDTO.setVatRate(customer.getVat() != null ? customer.getVat().getPercent() : null);

            apayCustomerDetailDTO.setAddress(new ApayAddressDTO(customer.getAddress()));

            customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).ifPresent(customerEmployee -> {
                DirectorDTO directorDTO = new DirectorDTO();
                directorDTO.setFirstName(customerEmployee.getFirstName());
                directorDTO.setLastName(customerEmployee.getLastName());
                directorDTO.setMiddleName(customerEmployee.getPatronymic());
                directorDTO.setPhone(customer.getPhone());
                apayCustomerDetailDTO.setOwner(directorDTO);
            });

            if (customer.getCustomerBank() != null) {
                ApayBankDTO apayBankDTO = new ApayBankDTO();
                apayBankDTO.setId(customer.getCustomerBank().getBank().getId().toString());
                apayBankDTO.setName(customer.getCustomerBank().getBank().getNameNotNullable());
                apayBankDTO.setOked(customer.getOked());
                apayBankDTO.setAccountNumber(customer.getCustomerBank().getAccountNumber());
                apayBankDTO.setMfo(customer.getCustomerBank().getBank().getCode());
                apayCustomerDetailDTO.setBank(apayBankDTO);
            }

            customer.getCustomerBranches().forEach(customerBranch -> {
                ApayBranchDTO apayBranchDTO = new ApayBranchDTO();
                apayBranchDTO.setId(customerBranch.getId().toString());
                apayBranchDTO.setName(customerBranch.getName());
                apayBranchDTO.setPhone(customerBranch.getPhone());

                ApayAddressDTO apayAddressDTO = new ApayAddressDTO(customerBranch.getAddress());
                apayAddressDTO.setLongitude(customerBranch.getAddress().getLongitude());
                apayAddressDTO.setLatitude(customerBranch.getAddress().getLatitude());
                apayBranchDTO.setAddress(apayAddressDTO);

                apayBranchDTO.setActivityType(new ApayActivityTypeDTO(customerBranch.getActivityType()));
                apayCustomerDetailDTO.addBranch(apayBranchDTO);
            });
        } else {

            SoliqCompanyDTO soliqCompanyDTO = soliqService.getCompanyByTinTest(tin);

            String tinOrPinfl = Optional.ofNullable(soliqCompanyDTO.getTin()).orElse(soliqCompanyDTO.getPersonalNum());
            if (StringUtils.isEmpty(tinOrPinfl)) {
                return apayCustomerDetailDTO;
            }

            Region region = regionRepository.findByCode(soliqCompanyDTO.getNs10Code());
            if (region == null) {
                throw validationService.entityNotFoundExceptionThrow(REGION, CODE, soliqCompanyDTO.getNs10Code().toString()).get();
            }

            District district = districtRepository.findFirstByRegionIdAndCode(region.getId(), soliqCompanyDTO.getNs11Code()).orElse(null);
            if (district == null) {
                throw validationService.entityNotFoundExceptionThrow(DISTRICT, CODE, soliqCompanyDTO.getNs11Code().toString()).get();
            }

            apayCustomerDetailDTO.setFromNic(true);
            apayCustomerDetailDTO.setName(soliqCompanyDTO.getName());
            apayCustomerDetailDTO.setTin(tinOrPinfl);

            ApayAddressDTO apayAddressDTO = new ApayAddressDTO();
            apayAddressDTO.setStreet(soliqCompanyDTO.getAddress());

            ApayLocalizedDTO regionDTO = new ApayLocalizedDTO();
            regionDTO.setId(region.getId().toString());
            regionDTO.setCode(region.getCode());
            regionDTO.setNameUz(region.getNameUzLatin());
            regionDTO.setNameRu(region.getNameRu());
            apayAddressDTO.setRegion(regionDTO);

            ApayLocalizedDTO districtDTO = new ApayLocalizedDTO();
            districtDTO.setId(district.getId().toString());
            districtDTO.setCode(district.getCode());
            districtDTO.setNameUz(district.getNameUzLatin());
            districtDTO.setNameRu(district.getNameRu());
            apayAddressDTO.setDistrict(districtDTO);

            apayCustomerDetailDTO.setAddress(apayAddressDTO);

            if (StringUtils.isNotEmpty(soliqCompanyDTO.getDirector())) {
                DirectorDTO director = new DirectorDTO();
                String[] fullNames = soliqCompanyDTO.getDirector().split(" ");
                for (String name : fullNames) {
                    if (StringUtils.isEmpty(director.getLastName())) {
                        director.setLastName(name.trim());
                        continue;
                    }
                    if (StringUtils.isEmpty(director.getFirstName())) {
                        director.setFirstName(name.trim());
                        continue;
                    }
                    if (StringUtils.isEmpty(director.getMiddleName())) {
                        director.setMiddleName(name.trim());
                    }
                }
                apayCustomerDetailDTO.setOwner(director);
            }

            ApayBankDTO bankDTO = new ApayBankDTO();
            bankDTO.setAccountNumber(soliqCompanyDTO.getAccount());
            bankDTO.setOked(soliqCompanyDTO.getOked());
            bankDTO.setMfo(soliqCompanyDTO.getMfo());
            if (StringUtils.isNotEmpty(soliqCompanyDTO.getMfo())) {
                bankRepository.findByCodeAndStatus(soliqCompanyDTO.getMfo(), CommonStatus.ACTIVE).ifPresent(bank -> bankDTO.setName(bank.getNameNotNullable()));
            }
            apayCustomerDetailDTO.setBank(bankDTO);
        }

        return apayCustomerDetailDTO;
    }
}
