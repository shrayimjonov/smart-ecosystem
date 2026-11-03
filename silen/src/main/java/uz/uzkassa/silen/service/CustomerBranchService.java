package uz.uzkassa.silen.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uz.uzkassa.silen.domain.Customer;
import uz.uzkassa.silen.domain.CustomerBranch;
import uz.uzkassa.silen.dto.CustomerBranchDTO;
import uz.uzkassa.silen.dto.filter.CustomerFilter;
import uz.uzkassa.silen.dto.waybill.NicBranchDTO;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.SoliqClient;
import uz.uzkassa.silen.repository.pg.CustomerBranchRepository;
import uz.uzkassa.silen.repository.pg.CustomerRepository;
import uz.uzkassa.silen.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerBranchService extends BaseService {
    CustomerBranchRepository customerBranchRepository;
    CustomerRepository customerRepository;

    SoliqClient soliqClient;

    public Long syncBranches(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(notFound("Клиент не найден"));

        NicBranchDTO[] branchesByCompany = soliqClient.getBranchesByCompany(customer.getTin());
        if (branchesByCompany == null) {
            throw new BadRequestException("Нет данных по данному ИНН", customer.getTin());
        }
        for (NicBranchDTO nicBranchDTO : branchesByCompany) {
            if (!nicBranchDTO.getIsDeleted()) continue;
            CustomerBranch customerBranch = customerBranchRepository.findFirstByNum(nicBranchDTO.getBranchNum()).orElse(new CustomerBranch());
            if (customerBranch.getId() != null) customerBranch.setCustomerId(customerId);
            customerBranch.setName(nicBranchDTO.getName());
            customerBranch.setNum(nicBranchDTO.getBranchNum());
            customerBranch.setRegionId(Long.valueOf(nicBranchDTO.getNs10Code()));
            customerBranch.setDistrictId(nicBranchDTO.getNs11Code());
            customerBranch.setDeleted(nicBranchDTO.getIsDeleted());
            customerBranch.setAddress(nicBranchDTO.getAddress());
            customerBranch.setLon(nicBranchDTO.getLongitude());
            customerBranch.setLat(nicBranchDTO.getLatitude());
            customerBranchRepository.save(customerBranch);
        }
        return customerId;
    }

    public Page<CustomerBranchDTO> findAllByFilter(CustomerFilter filter) {
        return customerBranchRepository.findAllByCustomerId(filter.getCustomerId(), filter.getPageable())
                .map(CustomerBranch::toDTO);
    }

    public List<CustomerBranchDTO> getItems(CustomerFilter filter) {
        if (filter.getCustomerTin() != null) {
            customerRepository.findFirstByOrganizationIdAndTinAndDeletedIsFalse(SecurityUtils.getCurrentOrganizationId(), filter.getCustomerTin())
                    .ifPresent(customer -> filter.setCustomerId(customer.getId()));
        }
        return customerBranchRepository.findAllByCustomerId(filter.getCustomerId(), filter.getPageable())
                .map(CustomerBranch::toDTO)
                .getContent();
    }

    public List<CustomerBranchDTO> getCustomerBranchesByTin(String tin) {
        NicBranchDTO[] branchesByCompany = soliqClient.getBranchesByCompany(tin);
        if (branchesByCompany == null) {
            throw new BadRequestException("Нет данных по данному ИНН", tin);
        }
        List<CustomerBranchDTO> list = new ArrayList<>();
        for (NicBranchDTO nicBranchDTO : branchesByCompany) {
            if (!nicBranchDTO.getIsDeleted()) continue;
            CustomerBranchDTO customerBranchDTO = new CustomerBranchDTO();
            customerBranchDTO.setName(nicBranchDTO.getName());
            customerBranchDTO.setNum(nicBranchDTO.getBranchNum());
            customerBranchDTO.setRegionId(Long.valueOf(nicBranchDTO.getNs10Code()));
            customerBranchDTO.setRegionName(nicBranchDTO.getNs10Name());
            customerBranchDTO.setDistrictId(nicBranchDTO.getNs11Code());
            customerBranchDTO.setDistrictName(nicBranchDTO.getNs11Name());
            customerBranchDTO.setAddress(nicBranchDTO.getAddress());
            customerBranchDTO.setLon(nicBranchDTO.getLongitude());
            customerBranchDTO.setLat(nicBranchDTO.getLatitude());
            list.add(customerBranchDTO);
        }
        return list;
    }

    public CustomerBranchDTO update(Long id, CustomerBranchDTO dto) {
        CustomerBranch customerBranch = customerBranchRepository.findById(id).orElseThrow(notFound("Филиал не найден"));
        customerBranch.setLat(dto.getLat());
        customerBranch.setLon(dto.getLon());
        customerBranch.setAddress(dto.getAddress());
        customerBranch.setRegionId(dto.getRegionId());
        customerBranch.setDistrictId(dto.getDistrictId());
        customerBranch.setAddress(dto.getAddress());
        customerBranch.setLon(dto.getLon());
        customerBranch.setLat(dto.getLat());
        return customerBranchRepository.save(customerBranch).toDTO();
    }

}
