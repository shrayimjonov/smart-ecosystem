package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.APayTransaction;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.apaytransaction.request.APayTransactionRequestDto;
import uz.uzkassa.cts.dto.apaytransaction.response.APayClientInfoResponseDto;
import uz.uzkassa.cts.dto.apaytransaction.response.APayTransactionResponseDto;
import uz.uzkassa.cts.dto.ledger.response.CurrentStatementResponseDto;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.mappings.APayTransactionMapper;
import uz.uzkassa.cts.repository.APayTransactionRepository;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.service.APayTransactionService;
import uz.uzkassa.cts.service.LedgerService;

@Service
public class APayTransactionServiceImpl extends BaseServiceImpl<APayTransaction, Long> implements APayTransactionService {

    private final APayTransactionRepository repository;
    private final CustomerRepository customerRepository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final LedgerService ledgerService;
    private final APayTransactionMapper aPayTransactionMapper;

    public APayTransactionServiceImpl(APayTransactionRepository repository,
                                      CustomerRepository customerRepository,
                                      CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                      LedgerService ledgerService,
                                      APayTransactionMapper aPayTransactionMapper) {
        super(repository);
        this.repository = repository;
        this.customerRepository = customerRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.ledgerService = ledgerService;
        this.aPayTransactionMapper = aPayTransactionMapper;
    }

    @Override
    public APayTransactionResponseDto create(APayTransactionRequestDto dto) {
        APayTransaction transaction = repository.findByTransactionId(dto.getTransactionId());
        if (transaction != null)
            return aPayTransactionMapper.toDto(transaction);

        transaction = aPayTransactionMapper.toEntity(dto);
        transaction = save(transaction);

        return aPayTransactionMapper.toDto(transaction);
    }

    @Override
    public APayClientInfoResponseDto getClientInfoByTin(String tin) {
        Customer customer = customerRepository.findByTin(tin);
        if (customer == null)
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();

        Long devicesCount = customerBranchDeviceRepository.countByCustomerId(customer.getId());

        CurrentStatementResponseDto statementResponseDto = ledgerService.getNeedAmountAndStatement(customer.getTin(), ServiceType.SERVICE);

        APayClientInfoResponseDto responseDto = new APayClientInfoResponseDto();
        responseDto.setCustomerName(customer.getCompanyName());
        responseDto.setDeviceCount(devicesCount);
        responseDto.setTin(customer.getTin());
        responseDto.setBalance(statementResponseDto.getBalance());
        responseDto.setNeedAmount(statementResponseDto.getNeedAmount());
        return responseDto;
    }
}
