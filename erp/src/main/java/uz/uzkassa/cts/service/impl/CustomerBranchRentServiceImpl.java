package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.CustomerBranchRent;
import uz.uzkassa.cts.dto.customerbranch.rent.CustomerBranchRentDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.repository.CustomerBranchRentRepository;
import uz.uzkassa.cts.service.CustomerBranchRentService;
import uz.uzkassa.cts.validator.ValidationContext;

@Transactional(readOnly = true)
@Service
public class CustomerBranchRentServiceImpl extends BaseServiceImpl<CustomerBranchRent, Long> implements CustomerBranchRentService {

    private final CustomerBranchRentRepository customerBranchRentRepository;
    private final ValidationContext validationContext;

    public CustomerBranchRentServiceImpl(CustomerBranchRentRepository customerBranchRentRepository, ValidationContext validationContext) {
        super(customerBranchRentRepository);
        this.customerBranchRentRepository = customerBranchRentRepository;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(CustomerBranchRentDTO customerBranchRentDTO) {
        validationContext.validate(customerBranchRentDTO);
        CustomerBranchRent customerBranchRent = new CustomerBranchRent();
        customerBranchRent.setCustomerBranchId(customerBranchRentDTO.getCustomerBranchId());
        customerBranchRent.setRentNumber(customerBranchRentDTO.getRentNumber());
        customerBranchRent.setStartDate(customerBranchRentDTO.getStartDate());
        customerBranchRent.setEndDate(customerBranchRentDTO.getEndDate());
        customerBranchRent.setStatus(CommonStatus.ACTIVE);
        return customerBranchRentRepository.save(customerBranchRent).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CustomerBranchRentDTO customerBranchRentDTO) {
        customerBranchRentDTO.setId(id);
        validationContext.validate(customerBranchRentDTO);
        return customerBranchRentRepository.findById(id).map(customerBranchRent -> {
            customerBranchRent.setCustomerBranchId(customerBranchRentDTO.getCustomerBranchId());
            customerBranchRent.setRentNumber(customerBranchRentDTO.getRentNumber());
            customerBranchRent.setStartDate(customerBranchRentDTO.getStartDate());
            customerBranchRent.setEndDate(customerBranchRentDTO.getEndDate());
            customerBranchRent.setStatus(CommonStatus.ACTIVE);
            return customerBranchRentRepository.save(customerBranchRent).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(RENT));
    }

    @Override
    @Transactional
    public Long createOrUpdate(CustomerBranchRentDTO customerBranchRentDTO) {
        customerBranchRentRepository.updateStatus(CommonStatus.IN_ACTIVE, customerBranchRentDTO.getCustomerBranchId());
        CustomerBranchRent customerBranchRent = customerBranchRentRepository.findFirstByCustomerBranchIdAndRentNumber(customerBranchRentDTO.getCustomerBranchId(), customerBranchRentDTO.getRentNumber()).orElse(null);
        if (customerBranchRent == null) {
            return this.create(customerBranchRentDTO);
        }
        return this.update(customerBranchRent.getId(), customerBranchRentDTO);
    }

    @Override
    public CustomerBranchRentDTO getByCustomerBranchId(Long customerBranchId) {
        return customerBranchRentRepository.findFirstByCustomerBranchIdAndStatus(customerBranchId, CommonStatus.ACTIVE).map(customerBranchRent -> {
            CustomerBranchRentDTO customerBranchRentDTO = new CustomerBranchRentDTO();
            customerBranchRentDTO.setId(customerBranchRent.getId());
            customerBranchRentDTO.setCustomerBranchId(customerBranchId);
            customerBranchRentDTO.setRentNumber(customerBranchRent.getRentNumber());
            customerBranchRentDTO.setStartDate(customerBranchRent.getStartDate());
            customerBranchRentDTO.setEndDate(customerBranchRent.getEndDate());
            return customerBranchRentDTO;
        }).orElse(null);
    }

    @Override
    @Transactional
    public void updateStatus(CommonStatus status, Long customerBranchId) {
        customerBranchRentRepository.updateStatus(status, customerBranchId);
    }
}
