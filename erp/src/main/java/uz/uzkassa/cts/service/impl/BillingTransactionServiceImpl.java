package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.BillingTransaction;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.integration.response.BillingTransactionResponseDTO;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.billing.BillingTransactionType;
import uz.uzkassa.cts.filter.billing.BillingTransactionFilter;
import uz.uzkassa.cts.integrations.BillingServiceFeignClient;
import uz.uzkassa.cts.repository.BillingTransactionRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.service.BillingTransactionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class BillingTransactionServiceImpl extends BaseServiceImpl<BillingTransaction, Long> implements BillingTransactionService {

    private final BillingTransactionRepository billingTransactionRepository;
    private final BillingServiceFeignClient billingServiceFeignClient;
    private final CustomerRepository customerRepository;

    public BillingTransactionServiceImpl(BillingTransactionRepository billingTransactionRepository,
                                         BillingServiceFeignClient billingServiceFeignClient, CustomerRepository customerRepository) {
        super(billingTransactionRepository);
        this.billingTransactionRepository = billingTransactionRepository;
        this.billingServiceFeignClient = billingServiceFeignClient;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public void create(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        BillingTransactionFilter billingFilter = new BillingTransactionFilter();
        billingFilter.setTin(customer.getTin());
        billingFilter.setCompanyType(CompanyType.PRIME.name());
        billingFilter.setDebitOrCredit(BillingTransactionType.CREDIT.name());
        billingTransactionRepository
                .findTopByCustomerIdOrderByTransactionDateDesc(customerId)
                .ifPresentOrElse(
                        billingTransaction -> {
                            billingFilter.setLastId(billingTransaction.getTransactionId());
                            billingFilter.setCreatedDate(billingTransaction.getTransactionDate());
                            List<BillingTransactionResponseDTO> transactionList = billingServiceFeignClient.getTransactionByTin(billingFilter);
                            if (CollectionUtils.isNotEmpty(transactionList)) {
                                List<BillingTransaction> listTransactionEntity = new ArrayList<>();
                                transactionList.forEach(transactionDTO -> {
                                    BillingTransaction billingTransactionEntity = new BillingTransaction();
                                    billingTransactionEntity.setCustomerId(customerId);
                                    billingTransactionEntity.setTransactionId(transactionDTO.getId());
                                    billingTransactionEntity.setLedgerId(transactionDTO.getLedgerId());
                                    billingTransactionEntity.setAmount(transactionDTO.getAmount());
                                    billingTransactionEntity.setRemainder(transactionDTO.getAmount());
                                    billingTransactionEntity.setTransactionDate(transactionDTO.getCreatedDate());
                                    listTransactionEntity.add(billingTransactionEntity);
                                });
                                ListUtils.partition(listTransactionEntity, 10)
                                        .forEach(billingTransactionRepository::saveAllAndFlush);
                            }
                        },
                        () -> {
                            List<BillingTransactionResponseDTO> transactionList = billingServiceFeignClient.getTransactionByTin(billingFilter);
                            if (CollectionUtils.isNotEmpty(transactionList)) {
                                List<BillingTransaction> listTransactionEntity = new ArrayList<>();
                                transactionList.forEach(transactionDTO -> {
                                    BillingTransaction billingTransactionEntity = new BillingTransaction();
                                    billingTransactionEntity.setCustomerId(customerId);
                                    billingTransactionEntity.setTransactionId(transactionDTO.getId());
                                    billingTransactionEntity.setLedgerId(transactionDTO.getLedgerId());
                                    billingTransactionEntity.setAmount(transactionDTO.getAmount());
                                    billingTransactionEntity.setRemainder(transactionDTO.getAmount());
                                    billingTransactionEntity.setTransactionDate(transactionDTO.getCreatedDate());
                                    listTransactionEntity.add(billingTransactionEntity);
                                });
                                ListUtils.partition(listTransactionEntity, 10)
                                        .forEach(billingTransactionRepository::saveAllAndFlush);
                            }
                        });
    }

    @Override
    @Transactional
    public boolean calculateBalance(BigDecimal amount, Long customerId) {
        List<BillingTransaction> transactionList = billingTransactionRepository.findAllByCustomerIdAndCalculatedOrderByTransactionDate(customerId, false);
        if (CollectionUtils.isNotEmpty(transactionList)) {
            for (BillingTransaction billingTransaction : transactionList) {
                if (billingTransaction.getRemainder().compareTo(amount) >= 0) {
                    billingTransaction.setRemainder(billingTransaction.getRemainder().subtract(amount));
                    if (billingTransaction.getRemainder().equals(BigDecimal.ZERO)) {
                        billingTransaction.setCalculated(true);
                    }
                    amount = BigDecimal.ZERO;
                    break;
                } else {
                    amount = amount.subtract(billingTransaction.getRemainder());
                    billingTransaction.setRemainder(BigDecimal.ZERO);
                    billingTransaction.setCalculated(true);
                }
            }
        }
        if (amount.equals(BigDecimal.ZERO)) {
            billingTransactionRepository.saveAllAndFlush(transactionList);
            return true;
        }
        return false;
    }
}
