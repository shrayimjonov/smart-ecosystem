package uz.uzkassa.cts.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.Bank;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBank;
import uz.uzkassa.cts.dto.customerbank.CustomBankDTO;
import uz.uzkassa.cts.dto.customerbank.cabinet.CustomBankDetailCabinetDTO;
import uz.uzkassa.cts.dto.payload.CustomerPayload;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.integrations.SoliqClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqCustomerProducer;
import uz.uzkassa.cts.repository.BankRepository;
import uz.uzkassa.cts.repository.CustomerBankRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.service.CustomerBankService;
import uz.uzkassa.cts.validator.ValidationContext;

@Transactional(readOnly = true)
@Service
public class CustomerBankServiceImpl extends BaseServiceImpl<CustomerBank, Long> implements CustomerBankService {

    private final CustomerBankRepository customerBankRepository;
    private final CustomerRepository customerRepository;
    private final BankRepository bankRepository;
    private final RabbitMqCustomerProducer rabbitMqCustomerProducer;
    private final SoliqClient soliqClient;
    private final ValidationContext validationContext;

    public CustomerBankServiceImpl(CustomerBankRepository customerBankRepository, CustomerRepository customerRepository, BankRepository bankRepository, RabbitMqCustomerProducer rabbitMqCustomerProducer, SoliqClient soliqClient, ValidationContext validationContext) {
        super(customerBankRepository);
        this.customerBankRepository = customerBankRepository;
        this.customerRepository = customerRepository;
        this.bankRepository = bankRepository;
        this.rabbitMqCustomerProducer = rabbitMqCustomerProducer;
        this.soliqClient = soliqClient;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(CustomBankDTO customBankDTO) {
        validationContext.validate(customBankDTO);
        Customer customer = customerRepository.findById(customBankDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        Bank bank = bankRepository.findById(customBankDTO.getBankId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BANK));
        CustomerBank customerBank = new CustomerBank();
        customerBank.setCustomerId(customer.getId());
        customerBank.setBankId(bank.getId());
        customerBank.setBank(bank);
        customerBank.setAccountNumber(customBankDTO.getAccountNumber());
        customerBank.setDefault(true);
        customerBankRepository.save(customerBank);
        customer.setCustomerBankId(customerBank.getId());
        customer.setCustomerBank(customerBank);
        customerRepository.save(customer);
        customerBankRepository.update(false, customBankDTO.getCustomerId(), customerBank.getId());
        return customerBank.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, CustomBankDTO customBankDTO) {
        customBankDTO.setId(id);
        validationContext.validate(customBankDTO);
        return customerBankRepository.findById(id).map(customerBank -> {
            customerBank.setBankId(customBankDTO.getBankId());
            customerBank.setAccountNumber(customBankDTO.getAccountNumber());
            customerBank.setDefault(true);
            customerBankRepository.update(false, customBankDTO.getCustomerId(), id);
            return customerBankRepository.save(customerBank).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(BANK));
    }

    @Override
    public CustomBankDetailCabinetDTO get(Long id) {
        return customerBankRepository.findById(id).map(this::wrap).orElseThrow(validationService.entityNotFoundExceptionThrow(BANK));
    }

    @Override
    public CustomBankDetailCabinetDTO getByCustomerId(Long customerId) {
        return customerBankRepository.findFirstByCustomerIdAndDefaultIsTrue(customerId).map(this::wrap).orElse(null);
    }

    @Override
    public void sync() {
        if (getCompanyId() != null) {
            customerRepository.getTinsByCompanyId(getCompanyId())
                    .forEach(tin -> rabbitMqCustomerProducer.syncCustomerBank(new CustomerPayload(tin)));
        }
    }

    @Override
    public void syncQueue(CustomerPayload payload) {
        SoliqCompanyDTO soliqCompanyDTO = soliqClient.getCustomerByTin(payload.getTin());
        if (soliqCompanyDTO != null && StringUtils.isNotEmpty(soliqCompanyDTO.getMfo())) {
            bankRepository.findByCode(soliqCompanyDTO.getMfo()).ifPresent(bank -> {
                Customer customer = customerRepository.findByTin(payload.getTin());
                CustomerBank customerBank = customerBankRepository.findByCustomerIdAndBankId(customer.getId(), bank.getId());
                if (customerBank != null) {
                    if (StringUtils.isNotEmpty(soliqCompanyDTO.getAccount())) {
                        customerBank.setAccountNumber(soliqCompanyDTO.getAccount());
                    }
                    if (StringUtils.isNotEmpty(soliqCompanyDTO.getOked())) {
                        customer.setOked(soliqCompanyDTO.getOked());
                    }
                    customerBankRepository.save(customerBank);
                    customer.setCustomerBank(customerBank);
                    customer.setCustomerBankId(customerBank.getId());
                    customerRepository.save(customer);
                }
            });
        }
    }


    private CustomBankDetailCabinetDTO wrap(CustomerBank customerBank) {
        CustomBankDetailCabinetDTO customBankDetailDTO = new CustomBankDetailCabinetDTO();
        customBankDetailDTO.setId(customerBank.getId());
        customBankDetailDTO.setCustomer(customerBank.getCustomer().toShortDTO());
        customBankDetailDTO.setBank(customerBank.getBank().toCommonCodeDTO());
        customBankDetailDTO.setAccountNumber(customerBank.getAccountNumber());
        customBankDetailDTO.setDefault(customerBank.isDefault());
        return customBankDetailDTO;
    }
}
