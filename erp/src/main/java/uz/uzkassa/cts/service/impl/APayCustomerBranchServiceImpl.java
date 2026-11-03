package uz.uzkassa.cts.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBranch;
import uz.uzkassa.cts.dto.apay.*;
import uz.uzkassa.cts.dto.customer.CustomerBranchResponseAPayDTO;
import uz.uzkassa.cts.dto.payload.FilterPayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.integrations.ApayClient;
import uz.uzkassa.cts.integrations.ApayGrpcClient;
import uz.uzkassa.cts.repository.CustomerBranchRepository;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.service.APayCustomerBranchService;
import uz.uzkassa.cts.service.ValidationService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class APayCustomerBranchServiceImpl implements APayCustomerBranchService {

    ApplicationProperties applicationProperties;
    ApayGrpcClient apayGrpcClient;
    ApayClient apayClient;
    CustomerBranchRepository customerBranchRepository;
    ObjectMapper objectMapper;
    CustomerRepository customerRepository;
    CustomerEmployeeRepository customerEmployeeRepository;
    ValidationService validationService;

    @Override
    public void sendCustomerBranches(FilterPayload filter) {
        List<CustomerBranchResponseAPayDTO> responseAPayDTOList = customerBranchRepository.getCustomerBranches(filter);
        if (!CollectionUtils.isEmpty(responseAPayDTOList)) {
            apayGrpcClient.sendCustomerBranches(responseAPayDTOList);
        }
    }

    @Override
    public void createCustomerBranches(Payload payload) {
        Optional<CustomerBranch> optionalCustomerBranch = customerBranchRepository.findById(payload.getId());
        if (optionalCustomerBranch.isPresent()) {
            CustomerBranch customerBranch = optionalCustomerBranch.get();
            CustomerBranchResponseAPayDTO branchResponseAPayDTO = new CustomerBranchResponseAPayDTO();
            branchResponseAPayDTO.setBranchId(customerBranch.getId());
            branchResponseAPayDTO.setBranchName(customerBranch.getName());
            branchResponseAPayDTO.setDeleted(false);
            branchResponseAPayDTO.setCreatedDate(customerBranch.getCreatedDate());
            branchResponseAPayDTO.setUpdatedDate(customerBranch.getLastModifiedDate());
            if (customerBranch.getAddress() != null) {
                if (customerBranch.getAddress().getRegion() != null)
                    branchResponseAPayDTO.setRegionCode(customerBranch.getAddress().getRegion().getCode().toString());
                if (customerBranch.getAddress().getRegion() != null)
                    branchResponseAPayDTO.setDistrictCode(customerBranch.getAddress().getDistrict().getCode().toString());
                branchResponseAPayDTO.setLongitude(customerBranch.getAddress().getLongitude());
                branchResponseAPayDTO.setLatitude(customerBranch.getAddress().getLatitude());
            }
            if (customerBranch.getActivityType() != null) {
                branchResponseAPayDTO.setCategoryCode(customerBranch.getActivityType().getCode().toString());
                if (customerBranch.getActivityType().getParent() != null)
                    branchResponseAPayDTO.setParentCategoryCode(customerBranch.getActivityType().getParent().getCode().toString());
            }
            if (customerBranch.getCustomer() != null) {
                branchResponseAPayDTO.setCompanyName(customerBranch.getCustomer().getCompanyName());
            }
            apayGrpcClient.createCustomerBranches(branchResponseAPayDTO);
        }
    }

    @Override
    public void updateCustomerBranches(Payload payload) {
        Optional<CustomerBranch> optionalCustomerBranch = customerBranchRepository.findById(payload.getId());
        if (optionalCustomerBranch.isPresent()) {
            CustomerBranch customerBranch = optionalCustomerBranch.get();
            CustomerBranchResponseAPayDTO branchResponseAPayDTO = new CustomerBranchResponseAPayDTO();
            branchResponseAPayDTO.setBranchId(customerBranch.getId());
            branchResponseAPayDTO.setBranchName(customerBranch.getName());
            branchResponseAPayDTO.setDeleted(false);
            branchResponseAPayDTO.setCreatedDate(customerBranch.getCreatedDate());
            branchResponseAPayDTO.setUpdatedDate(customerBranch.getLastModifiedDate());
            if (customerBranch.getAddress() != null) {
                if (customerBranch.getAddress().getRegion() != null)
                    branchResponseAPayDTO.setRegionCode(customerBranch.getAddress().getRegion().getCode().toString());
                if (customerBranch.getAddress().getRegion() != null)
                    branchResponseAPayDTO.setDistrictCode(customerBranch.getAddress().getDistrict().getCode().toString());
                branchResponseAPayDTO.setLongitude(customerBranch.getAddress().getLongitude());
                branchResponseAPayDTO.setLatitude(customerBranch.getAddress().getLatitude());
            }
            if (customerBranch.getActivityType() != null) {
                branchResponseAPayDTO.setCategoryCode(customerBranch.getActivityType().getCode().toString());
                if (customerBranch.getActivityType().getParent() != null)
                    branchResponseAPayDTO.setParentCategoryCode(customerBranch.getActivityType().getParent().getCode().toString());
            }
            if (customerBranch.getCustomer() != null) {
                branchResponseAPayDTO.setCompanyName(customerBranch.getCustomer().getCompanyName());
            }
            apayGrpcClient.updateCustomerBranches(branchResponseAPayDTO);
        }
    }

    @Override
    public void deleteCustomerBranches(Payload payload) {
        APayBranchRequestDTO aPayBranchRequestDTO = new APayBranchRequestDTO();
        aPayBranchRequestDTO.setBranchId(payload.getId());
        apayGrpcClient.deleteCustomerBranches(aPayBranchRequestDTO);
    }

    @Override
    public void quickRegister(ApayRegisterCustomerBranchesDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        if (StringUtils.isBlank(customer.getTin())) {
            throw validationService.requiredExceptionThrow(TIN).get();
        }
        if (customer.getBusinessType() == null) {
            throw validationService.requiredExceptionThrow(BUSINESS_TYPE).get();
        }
        if (customer.getActivityTypeId() == null) {
            throw validationService.requiredExceptionThrow(ACTIVITY_TYPE).get();
        }
        if (customer.getAddress() == null) {
            throw validationService.requiredExceptionThrow(ADDRESS).get();
        }
        if (customer.getAddress().getRegionId() == null) {
            throw validationService.requiredExceptionThrow(REGION).get();
        }
        if (customer.getAddress().getDistrictId() == null) {
            throw validationService.requiredExceptionThrow(DISTRICT).get();
        }
        if (customer.getCustomerBankId() == null) {
            throw validationService.requiredExceptionThrow(BANK).get();
        }
        List<CustomerBranch> branches;
        if (!CollectionUtils.isEmpty(dto.getCustomerBranchIds())) {
            branches = customerBranchRepository.findAllById(dto.getCustomerBranchIds());
        } else {
            branches = customer.getCustomerBranches();
        }
        if (CollectionUtils.isEmpty(branches)) {
            throw validationService.requiredExceptionThrow(BRANCH).get();
        }
        branches.forEach(branch -> {
            if (branch.getActivityType() == null || branch.getActivityType().getParent() == null) {
                throw validationService.requiredExceptionThrow(ACTIVITY_TYPE).get();
            }
            if (branch.getAddress() == null) {
                throw validationService.requiredExceptionThrow(ADDRESS).get();
            }
            if (branch.getAddress().getRegionId() == null) {
                throw validationService.requiredExceptionThrow(REGION).get();
            }
            if (branch.getAddress().getDistrictId() == null) {
                throw validationService.requiredExceptionThrow(DISTRICT).get();
            }
        });
        boolean exists = false;
        try {
            ResponseEntity<APayCheckCompanyDTO> checkCompanyResponse = apayClient.checkCompanyExist(customer.getTin());
            if (checkCompanyResponse.getStatusCode().is2xxSuccessful() && checkCompanyResponse.getBody() != null) {
                APayCheckCompanyDTO companyDTO = checkCompanyResponse.getBody();
                customer.setApayId(companyDTO.getApayCompanyId());
                customer.setApayTariffId(companyDTO.getTariff() != null ? companyDTO.getTariff().getId() : applicationProperties.getApayProperty().getSilverTariffId());
                exists = true;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        APayCompanyRegisterDTO request = new APayCompanyRegisterDTO(customer);
        request.setBranchList(branches.stream().map(APayRegisterMerchantDTO::new).toList());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).ifPresent(customerEmployee -> {
            DirectorDTO ownerDTO = new DirectorDTO();
            ownerDTO.setLastName(customerEmployee.getLastName());
            ownerDTO.setFirstName(customerEmployee.getFirstName());
            ownerDTO.setMiddleName(customerEmployee.getPatronymic());
            ownerDTO.setPhone(customer.getPhone());
            request.setOwner(ownerDTO);
        });
        try {
            log.info("Apay quick register request: {}", objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException ignored) {
        }
        if (!exists) {
            ResponseEntity<ApayQuickRegisterResponseDTO> response = apayClient.quickRegister(request);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Apay quick register response: {}", response.getBody());
                customer.setApayId(response.getBody().getId());
            }
        } else {
            apayClient.quickRegisterBranches(request);
        }
        customerRepository.save(customer);
    }
}
