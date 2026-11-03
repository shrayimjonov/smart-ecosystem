package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.CustomerEmployee;
import uz.uzkassa.cts.dto.customeremployee.CustomerEmployeeDTO;
import uz.uzkassa.cts.dto.customeremployee.CustomerEmployeeListDTO;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.service.CustomerEmployeeService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
public class CustomerEmployeeServiceImpl extends BaseServiceImpl<CustomerEmployee, Long> implements CustomerEmployeeService {

    private final CustomerEmployeeRepository customerEmployeeRepository;
    private final ValidationContext validationContext;

    public CustomerEmployeeServiceImpl(CustomerEmployeeRepository customerEmployeeRepository, ValidationContext validationContext) {
        super(customerEmployeeRepository);
        this.customerEmployeeRepository = customerEmployeeRepository;
        this.validationContext = validationContext;
    }

    @Transactional
    @Override
    public Long create(CustomerEmployeeDTO customerEmployeeDTO) {
        validationContext.validate(customerEmployeeDTO);
        CustomerEmployee customerEmployee = new CustomerEmployee();
        customerEmployee.setCustomerId(customerEmployeeDTO.getCustomerId());
        customerEmployee.setRole(customerEmployeeDTO.getRole());
        customerEmployee.setFirstName(customerEmployeeDTO.getFirstName());
        customerEmployee.setLastName(customerEmployeeDTO.getLastName());
        customerEmployee.setPatronymic(customerEmployeeDTO.getPatronymic());
        return customerEmployeeRepository.save(customerEmployee).getId();
    }

    @Transactional
    @Override
    public Long update(Long id, CustomerEmployeeDTO customerEmployeeDTO) {
        validationContext.validate(customerEmployeeDTO);
        return customerEmployeeRepository.findById(id).map(customerEmployee -> {
            customerEmployee.setCustomerId(customerEmployeeDTO.getCustomerId());
            customerEmployee.setRole(customerEmployeeDTO.getRole());
            customerEmployee.setFirstName(customerEmployeeDTO.getFirstName());
            customerEmployee.setLastName(customerEmployeeDTO.getLastName());
            customerEmployee.setPatronymic(customerEmployeeDTO.getPatronymic());
            return customerEmployeeRepository.save(customerEmployee).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE));

    }

    @Override
    public List<CustomerEmployeeListDTO> getList(Long customerId) {
        return customerEmployeeRepository.findAllByCustomerIdAndDeletedIsFalse(customerId).stream().map(customerEmployee -> {
            CustomerEmployeeListDTO customerEmployeeListDTO = new CustomerEmployeeListDTO();
            customerEmployeeListDTO.setId(customerEmployee.getId());
            customerEmployeeListDTO.setFirstName(customerEmployee.getFirstName());
            customerEmployeeListDTO.setLastName(customerEmployee.getLastName());
            customerEmployeeListDTO.setPatronymic(customerEmployee.getPatronymic());
            customerEmployeeListDTO.setCustomer(customerEmployee.getCustomer().toShortDTO());
            customerEmployeeListDTO.setRole(customerEmployee.getRole());
            return customerEmployeeListDTO;
        }).collect(Collectors.toList());
    }
}
