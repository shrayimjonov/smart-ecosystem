package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.invoice.request.LedgerInvoiceRequestDto;
import uz.uzkassa.cts.dto.ledger.request.LedgerDto;
import uz.uzkassa.cts.dto.ledger.request.LedgerRevertAmountRequestDto;
import uz.uzkassa.cts.dto.ledger.response.*;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.DeviceSuspendStatus;
import uz.uzkassa.cts.enums.InvoiceType;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.filter.BalanceFilter;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.LedgerMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqCustomerProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.LedgerService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LedgerServiceImpl extends BaseServiceImpl<Ledger, Long> implements LedgerService {

    private static final String BANK_SERVICE = "BANK_SERVICE";
    private static final String BANK_PURCHASE = "BANK_PURCHASE";
    private static final String UZKASSA_SERVICE = "UZKASSA_SERVICE";
    private static final String UZKASSA_PURCHASE = "UZKASSA_PURCHASE";
    private static final String COMPANY_TIN = "306351564";

    private final LedgerRepository repository;
    private final LedgerDetailRepository ledgerDetailRepository;
    private final ServicePaymentHistoryForCronRepository servicePaymentHistoryForCronRepository;
    private final RabbitMqDeviceSwitcherProducer rabbitMqProducer;
    private final RabbitMqCustomerProducer rabbitMqCustomerProducer;
    private final CustomerRepository customerRepository;
    private final RentPaymentHistoryRepository rentPaymentHistoryRepository;
    private final LedgerTestRepository ledgerTestRepository;
    private final LedgerMapper ledgerMapper;

    public LedgerServiceImpl(LedgerRepository repository,
                             LedgerDetailRepository ledgerDetailRepository,
                             ServicePaymentHistoryForCronRepository servicePaymentHistoryForCronRepository,
                             RabbitMqDeviceSwitcherProducer rabbitMqProducer,
                             RabbitMqCustomerProducer rabbitMqCustomerProducer, CustomerRepository customerRepository,
                             RentPaymentHistoryRepository rentPaymentHistoryRepository,
                             LedgerTestRepository ledgerTestRepository,
                             LedgerMapper ledgerMapper) {
        super(repository);
        this.repository = repository;
        this.ledgerDetailRepository = ledgerDetailRepository;
        this.servicePaymentHistoryForCronRepository = servicePaymentHistoryForCronRepository;
        this.rabbitMqProducer = rabbitMqProducer;
        this.rabbitMqCustomerProducer = rabbitMqCustomerProducer;
        this.customerRepository = customerRepository;
        this.rentPaymentHistoryRepository = rentPaymentHistoryRepository;
        this.ledgerTestRepository = ledgerTestRepository;
        this.ledgerMapper = ledgerMapper;
    }

    @Override
    public LedgerDto create(LedgerDto dto) {
        return null;
    }

    @Override
    public List<LedgerResponseDto> topUpClientBalance(LedgerDto dto) {
        return topUpCustomerBalance(dto.getDescription(), dto.getAmount(), dto.getTin())
                .getDetails()
                .stream()
                .map(ledgerMapper::toDto)
                .collect(Collectors.toList());
    }

    public void importFromJournalDetail(BankClientDetail detail) {
        importFromBankClient(detail);
        //DEVICE SWITCH STATUS
        rabbitMqProducer.paymentHistoryCreate(detail.getPayerIdentityNumber());
        rabbitMqProducer.deviceStatusSwitch(detail.getPayerIdentityNumber());
    }

    @Override
    @Transactional
    public void importFromJournalDetails(Set<BankClientDetail> details) {
        for (BankClientDetail detail : details) {
            if (!detail.isApproved())
                topUpBalance(detail);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void topUpBalance(BankClientDetail detail) {
        importFromBankClient(detail);
        //DEVICE SWITCH STATUS
        rabbitMqProducer.paymentHistoryCreate(detail.getPayerIdentityNumber());
        rabbitMqProducer.deviceStatusSwitch(detail.getPayerIdentityNumber());
    }

    @Override
    public LedgerResponseDto getByBankAccountNumber(String accountNumber) {
        LedgerDetail detail = getLastByAccountNumber(accountNumber);
        return ledgerMapper.toDto(detail);
    }

    @Override
    public List<LedgerResponseDto> getClientBalanceFull(String tin) {
        List<LedgerResponseDto> details = new ArrayList<>();
        LedgerResponseDto ledgerResponseDto = getClientBalanceFull(tin, ServiceType.SERVICE);
        if (ledgerResponseDto != null) {
            details.add(ledgerResponseDto);
        }
        return details;
    }

    public LedgerResponseDto getClientLastTopUp(Long customerId) {
        List<LedgerDetail> detail = repository.getClientLastTopUp(customerId);
        if (!detail.isEmpty())
            return ledgerMapper.toDto(detail.getFirst());
        return null;
    }

    private LedgerResponseDto getClientBalanceFull(String tin, ServiceType serviceType) {
        List<LedgerDetail> detail = repository.getClientBalanceFull(tin, serviceType);
        if (!detail.isEmpty())
            return ledgerMapper.toDto(detail.getFirst());
        return null;
    }

    @Override
    public LedgerResponseDto getClientBalance(String tin, ServiceType serviceType) {
        LedgerResponseDto result = getClientBalanceFull(tin, serviceType);
        if (result == null) {
            throw validationService.notFoundExceptionThrow("billing.not.statement", tin).get();
        }
        return result;
    }

    @Override
    public BigDecimal calculateUnpaidAndWithoutInvoice(List<ServicePaymentHistory> histories) {
        BigDecimal total = BigDecimal.ZERO;
        for (ServicePaymentHistory history : histories) {
            if (history.getServiceOrderInvoiceId() == null)
                total = total.add(history.getAmount());
        }
        return total;
    }

    @Override
    public BigDecimal getSumOfCreditPart(String tin) {
        return ledgerDetailRepository.getTotalCredit(tin);
    }

    @Override
    public Page<LedgerResponseDto> getAllTransactionsByTin(String tin, BaseFilter filter) {
        return ledgerDetailRepository.findByCustomerIdOrderByIdDesc(getCustomerId(tin), filter.getOrderedPageable())
                .map(ledgerMapper::toDto);
    }

    public List<LedgerTinPinflResponseDto> getAllTransactionsByPinflAndTin(String tin) {
        List<LedgerTinPinflResponseDto> result = new ArrayList<>();
        for (Object[] obj : repository.getAllTransactionsByPinflAndTin(tin)) {
            result.add(LedgerTinPinflResponseDto.builder()
                    .id(((Long) obj[0]))
                    .accountNumber(obj[1].toString())
                    .balance((BigDecimal) obj[2])
                    .debit((BigDecimal) obj[3])
                    .credit((BigDecimal) obj[4])
                    .customerName(obj[5].toString())
                    .paymentDate(java.sql.Date.valueOf(obj[6].toString()).toLocalDate())
                    .tin(obj[7].toString())
                    .customerId(((Long) obj[8]))
                    .referenceTo(obj[9].toString())
                    .description(obj[10].toString())
                    .build());
        }
        return result;
    }

    @Override
    public Page<LedgerResponseDto> getAllTransactionsByAccountNumber(String accountNumber, BaseFilter filter) {
        return ledgerDetailRepository.findByAccountNumberOrderByIdAsc(accountNumber, filter.getOrderedPageable())
                .map(ledgerMapper::toDto);
    }

    /**
     * use this for paying anything (purchase, pay for services). It will decrease you balance  (debit: customer; credit:uzkassa)
     *
     * @param accountNumber customer account number from bank
     * @param customerName  customer name may use for reports
     * @param description   description of payment (by document)
     * @param referenceTo   reference to service and table and row id
     * @param amount        amount of payment
     */
    private Ledger clientPayment(String accountNumber, String customerName, String description, String referenceTo, BigDecimal amount, String tin) {

        Ledger ledger = new Ledger();
        ledger.setReferenceTo(referenceTo);
        ledger.setDescription(description);

        Long customerId = getCustomerId(tin);
        ledger.getDetails().add(customerPaymentDetail(customerId, accountNumber, customerName, amount, tin));
        ledger.getDetails().add(acceptPaymentByUzkassa(amount));

        save(ledger);

        return ledger;
    }


    /**
     * you can use this only when importing bank client file (debit: Bank; credit: customer)
     */
    private Ledger topUpCustomerBalance(String description, BigDecimal amount, String tin) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(tin, tin);
        Ledger ledger = new Ledger();
        ledger.setDescription(description);
        ledger.setReferenceTo("Top up: " + tin);

        LocalDate paymentDate = LocalDate.now();

        //add Customer detail
        Long customerId = getCustomerId(tin);
        BigDecimal customerBalance = calculateBalance(customerId, BigDecimal.ZERO, amount);
        ledger.addLedgerDetail(createLedgerDetail(customerId, customer.getCustomerBank().getAccountNumber(), customer.getCompanyName(),
                BigDecimal.ZERO, amount,
                customerBalance, tin, "",
                ServiceType.SERVICE, paymentDate));

        //add Bank details
        //BANK_SRVC -customerId is = (-1) minus one
        Long bankSrvcId = getCustomerId("BANK_SRVC");
        BigDecimal bankBalance = calculateBalance(bankSrvcId, amount, BigDecimal.ZERO);
        ledger.addLedgerDetail(createLedgerDetail(bankSrvcId, "20208000905065950001", "ООО \"SPECIAL TEXNO SOFT\"",
                amount, BigDecimal.ZERO, bankBalance, "BANK_SRVC", "",
                ServiceType.SERVICE, paymentDate));


        save(ledger);
        rabbitMqCustomerProducer.updateCustomerStatusByPayment(tin);
        return ledger;
    }

    private void importFromBankClient(BankClientDetail detail) {
        Ledger ledger = new Ledger();
        ledger.setReferenceTo(detail.toReferenceToString());
        ledger.setDescription(detail.getDescription());
        ledger.setContractNumber(detail.getContractNumber());
        ledger.setSpecificationNumber(detail.getSpecificationNumber());

        //add Bank details
        Long bankCustomerId = getCustomerId(detail.getReceiverIdentityNumber());
        BigDecimal bankBalance = calculateBalance(bankCustomerId, detail.getAmount(), BigDecimal.ZERO);
        ledger.addLedgerDetail(createLedgerDetail(bankCustomerId, detail.getReceiverAccountNumber(), detail.getReceiverName(),
                detail.getAmount(), BigDecimal.ZERO, bankBalance,
                detail.getReceiverIdentityNumber(), "", detail.getServiceType(), detail.getPaymentDate()));

        //add Customer detail
        Long customerId = getCustomerId(detail.getPayerIdentityNumber());
        BigDecimal customerBalance = calculateBalance(customerId, BigDecimal.ZERO, detail.getAmount());
        ledger.addLedgerDetail(createLedgerDetail(customerId, detail.getPayerAccountNumber(), detail.getPayerName(),
                BigDecimal.ZERO, detail.getAmount(), customerBalance, detail.getPayerIdentityNumber(), "", detail.getServiceType(), detail.getPaymentDate()));

        saveAndFlash(ledger);
        rabbitMqCustomerProducer.updateCustomerStatusByPayment(detail.getPayerIdentityNumber());
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public void revertMoneyToBank(LedgerRevertAmountRequestDto dto) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(dto.getTin(), dto.getTin());
        Ledger ledger = new Ledger();
        ledger.setDescription(dto.getDescription());
        ledger.setReferenceTo("Revert from: " + dto.getTin());

        LocalDate paymentDate = LocalDate.now();
        //add Bank details
        //BANK_SRVC -customerId is = (-1) minus one
        Long bankSrvcId = getCustomerId("BANK_SRVC");
        BigDecimal bankBalance = calculateBalance(bankSrvcId, BigDecimal.ZERO, dto.getAmount());
        ledger.addLedgerDetail(createLedgerDetail(bankSrvcId, "20208000905065950001", "ООО \"SPECIAL TEXNO SOFT\"",
                BigDecimal.ZERO, dto.getAmount(), bankBalance, "BANK_SRVC", "",
                ServiceType.SERVICE, paymentDate));

        //add Customer detail
        Long customerId = getCustomerId(dto.getTin());
        BigDecimal customerBalance = calculateBalance(customerId, dto.getAmount(), BigDecimal.ZERO);
        ledger.addLedgerDetail(createLedgerDetail(customerId, customer.getCustomerBank().getAccountNumber(), customer.getCompanyName(), dto.getAmount(),
                BigDecimal.ZERO, customerBalance, dto.getTin(), "",
                ServiceType.SERVICE, paymentDate));

        save(ledger);
        rabbitMqCustomerProducer.updateCustomerStatusByPayment(dto.getTin());
    }

    @Override
    public void topUpBalance(LedgerRevertAmountRequestDto dto) {
        topUpCustomerBalance(dto.getDescription(), dto.getAmount(), dto.getTin());
    }

    private Long getCustomerId(String tin) {
        switch (tin) {
            case "BANK_SRVC" -> {
                return -1L;
            }
            case "UZKASSA_PURCHASE" -> {
                return -2L;
            }
            case "BANK_PURCHASE" -> {
                return -3L;
            }
        }
        Customer customer = customerRepository.findFirstByTinOrOldTin(tin, tin);
        if (customer != null)
            return customer.getId();
        return 0L;
    }


    private BigDecimal calculateBalance(Long customerId, BigDecimal debitAccount, BigDecimal creditAccount) {
        BigDecimal balance = getCustomerBalanceByCustomerId(customerId);
        balance = balance.subtract(debitAccount).add(creditAccount);
        return balance;
    }

    @Override
    public BigDecimal getCustomerBalanceByCustomerId(Long customerId) {
        LedgerDetail detail = getLastByCustomerId(customerId);
        if (detail == null)
            return BigDecimal.ZERO;
        return detail.getBalance();
    }

    @Override
    public BigDecimal getCustomerBalance(String tin) {
        LedgerDetail detail = getLastByTin(tin);
        if (detail == null)
            return BigDecimal.ZERO;
        return detail.getBalance();
    }

    @Override
    public Page<LedgerBalanceDto> getBalance(BalanceFilter filter) {
        ResultList<LedgerBalanceDto> resultList = repository.getBalance(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<LedgerResponseDto> getService(BaseFilter filter) {
        return getBalanceByServiceOrSell(filter, ServiceType.SERVICE);
    }

    private Page<LedgerResponseDto> getBalanceByServiceOrSell(BaseFilter filter, ServiceType serviceType) {

        ResultList<LedgerResponseDto> resultList = repository.getBalanceByServiceOrSell(filter, serviceType);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<LedgerDetail> getAllCustomersServiceBalance() {
        return repository.getAllCustomersServiceBalance();
    }

    @Override
    public Page<LedgerResponseDto> getSell(BaseFilter filter) {
        return getBalanceByServiceOrSell(filter, ServiceType.SALE);
    }

    @Override
    public void payForOrder(LedgerInvoiceRequestDto detail) {
        Ledger ledger = new Ledger();
        ledger.setReferenceTo(detail.getLink());
        ledger.setDescription(detail.getDescription());
        ledger.setContractNumber(detail.getContractNumber());
        ledger.setSpecificationNumber(detail.getSpecificationNumber());

        //add Company details
        Long bankId = getCustomerId(detail.getReceiverIdentityNumber());
        BigDecimal bankBalance = calculateBalance(bankId, BigDecimal.ZERO, detail.getAmount());
        ledger.addLedgerDetail(createLedgerDetail(bankId, detail.getReceiverAccountNumber(), detail.getReceiverName(),
                BigDecimal.ZERO, detail.getAmount(), bankBalance,
                detail.getReceiverIdentityNumber(),
                detail.getLink(), detail.getServiceType(), detail.getPaymentDate()));

        //add Customer detail
        Long customerId = getCustomerId(detail.getPayerIdentityNumber());
        BigDecimal customerBalance = calculateBalance(customerId, detail.getAmount(), BigDecimal.ZERO);
        ledger.addLedgerDetail(createLedgerDetail(customerId, detail.getPayerAccountNumber(), detail.getPayerName(),
                detail.getAmount(), BigDecimal.ZERO, customerBalance, detail.getPayerIdentityNumber(),
                detail.getLink(), detail.getServiceType(), detail.getPaymentDate()));

        save(ledger);
        rabbitMqCustomerProducer.updateCustomerStatusByPayment(detail.getPayerIdentityNumber());
    }

    private LedgerDetail getLastByTin(String tin) {
        return ledgerDetailRepository.findFirstByTinOrderByIdDesc(tin);
    }

    private LedgerDetail getLastByCustomerId(Long customerId) {
        return ledgerDetailRepository.findFirstByCustomerIdOrderByIdDesc(customerId);
    }

    private LedgerDetail getLastByAccountNumber(String accountNumber) {
        return ledgerDetailRepository.findFirstByAccountNumberOrderByIdDesc(accountNumber);
    }

    private LedgerDetail topUpBankDetails(Long customerId, BigDecimal amount, String tin) {
        return createLedgerDetail(customerId, BANK_PURCHASE, BANK_PURCHASE, amount, BigDecimal.ZERO, BigDecimal.ZERO, tin, "", ServiceType.SALE, LocalDate.now());
    }

    private LedgerDetail topUpCustomerDetails(Long customerId, String accountNumber, String customerName, BigDecimal amount, String tin) {
        return createLedgerDetail(customerId, accountNumber, customerName, BigDecimal.ZERO, BigDecimal.ZERO, amount, tin, "", ServiceType.SALE, LocalDate.now());
    }

    private LedgerDetail customerPaymentDetail(Long customerId, String accountNumber, String customerName, BigDecimal amount, String tin) {
        return createLedgerDetail(customerId, accountNumber, customerName, amount, BigDecimal.ZERO, BigDecimal.ZERO, tin, "", ServiceType.SALE, LocalDate.now());
    }


    private LedgerDetail acceptPaymentByUzkassa(BigDecimal amount) {
        return createLedgerDetail(getCustomerId("UZKASSA_PURCHASE"), UZKASSA_PURCHASE, UZKASSA_PURCHASE, BigDecimal.ZERO, amount, BigDecimal.ZERO, UZKASSA_PURCHASE, "", ServiceType.SALE, LocalDate.now());
    }


    private LedgerDetail createLedgerDetail(Long customerId,
                                            String accountNumber,
                                            String customerName,
                                            BigDecimal debit,
                                            BigDecimal credit,
                                            BigDecimal balance,
                                            String tin,
                                            String link,
                                            ServiceType serviceType,
                                            LocalDate paymentDate) {
        LedgerDetail detail = new LedgerDetail();
        detail.setCustomerId(customerId);
        detail.setAccountNumber(accountNumber);
        detail.setDebit(debit);
        detail.setCredit(credit);
        detail.setCustomerName(customerName);
        detail.setTin(tin);
        detail.setLink(link);
        detail.setBalance(balance);
        detail.setServiceType(serviceType);
        detail.setPaymentDate(paymentDate);
        return detail;
    }

    @Override
    public void revertByInvoiceId(Long invoiceId, InvoiceType type) {
        String link = switch (type) {
            case ONE_TIME_ORDER_INVOICE -> "/api/one-time-order-invoice/" + invoiceId + "/generateTemplate";
            case SERVICE_ORDER_INVOICE -> "/api/serviceOrderInvoice/" + invoiceId + "/generateTemplate";
            case TRADE_IN_INVOICE -> "/api/trade-in-invoice/" + invoiceId + "/generateTemplate";
            case RENT_INVOICE -> "/api/rent-invoice/" + invoiceId + "/generateTemplate";
            default -> "";
        };

        Ledger ledgerOld = repository.findByReferenceTo(link);

        Ledger ledger = new Ledger();
        ledger.setReferenceTo("Reverted ledger id: " + ledgerOld.getId());
        ledger.setDescription("Reverted: " + ledgerOld.getDescription());
        ledger.setContractNumber("Reverted: " + ledgerOld.getContractNumber());
        ledger.setSpecificationNumber(ledgerOld.getSpecificationNumber());

        Set<LedgerDetail> details = ledgerOld.getDetails();
        LedgerDetail bankSrvcDetail = null;
        LedgerDetail clientDetail = null;
        for (LedgerDetail detail : details) {
            if (detail.getTin().equals(COMPANY_TIN))
                bankSrvcDetail = detail;
            else
                clientDetail = detail;
        }

        //add Bank details
        Long bankId = getCustomerId(bankSrvcDetail.getTin());
        BigDecimal bankBalance = calculateBalance(bankId, bankSrvcDetail.getCredit(), bankSrvcDetail.getDebit());
        ledger.addLedgerDetail(createLedgerDetail(bankId, bankSrvcDetail.getAccountNumber(), bankSrvcDetail.getCustomerName(),
                bankSrvcDetail.getCredit(), bankSrvcDetail.getDebit(), bankBalance,
                bankSrvcDetail.getTin(), link, bankSrvcDetail.getServiceType(), bankSrvcDetail.getPaymentDate()));

        //add Customer detail
        Long customerId = getCustomerId(clientDetail.getTin());
        BigDecimal customerBalance = calculateBalance(customerId, clientDetail.getCredit(), clientDetail.getDebit());
        ledger.addLedgerDetail(createLedgerDetail(customerId, clientDetail.getAccountNumber(), clientDetail.getCustomerName(),
                clientDetail.getCredit(), clientDetail.getDebit(), customerBalance, clientDetail.getTin(), link, clientDetail.getServiceType(), clientDetail.getPaymentDate()));

        save(ledger);
        rabbitMqCustomerProducer.updateCustomerStatusByPayment(clientDetail.getTin());
    }

    public boolean isBalanceEnoughForRent(String tin, BigDecimal amount) {
        CurrentStatementResponseDto balance = getNeedAmountAndStatement(tin, ServiceType.SERVICE);
        return balance.getNeedAmount().compareTo(amount) >= 0;
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentStatementResponseDto getNeedAmountAndStatement(String tin, ServiceType service) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(tin, tin);
        if (customer == null)
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();

        return getNeedAmountAndStatement(customer.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentStatementResponseDto getNeedAmountAndStatement(Long customerId) {
        BigDecimal calculateUnpaid = servicePaymentHistoryForCronRepository.unpaidHistoriesSum(customerId);
        if (calculateUnpaid == null)
            calculateUnpaid = BigDecimal.ZERO;

        BigDecimal rentPayment = rentPaymentHistoryRepository.unpaidHistoriesSum(customerId);
        if (rentPayment == null)
            rentPayment = BigDecimal.ZERO;

        BigDecimal balance = getCustomerBalanceByCustomerId(customerId);
        BigDecimal statement = balance
                .subtract(calculateUnpaid)
                .subtract(rentPayment);

        return new CurrentStatementResponseDto(statement, balance);
    }


    public BigDecimal getClientBalanceByServiceType(String tin, ServiceType serviceType) {
        return repository.getClientBalanceByServiceType(tin, serviceType);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getClientBalanceOnThisDate(String tin, LocalDate createDate) {
        return repository.getClientBalanceOnThisDate(tin, createDate);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getDebtors() {
        return repository.getDebtors();
    }

    @Override
    @Transactional(readOnly = true)
    public LedgerDebtorsDto getDebtorsWithCount() {
        return repository.getDebtorsWithCount();
    }

    @Transactional(readOnly = true)
    public Page<BalanceListResponseDto> getBalancePrepaidList(BaseFilter filter) {
        ResultList<BalanceListResponseDto> resultList = repository.getBalancePrepaidList(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCurrentMonthDetails() {
        return repository.getCurrentMonthDetails();
    }


    @Override
    @Transactional(readOnly = true)
    public IncomeExpenseResponseDto getIncomeAndExpense(LocalDate startDate, LocalDate endDate) {
        return repository.getIncomeAndExpense(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurrentMonthDetailsSummaryDto> getSummary(LocalDate startDate, LocalDate endDate) {
        return repository.getSummary(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetailNotCreatedReportDto> getDetailNotCreatedDeviceList(LocalDate startDate, LocalDate endDate) {
        try {
            List<DetailNotCreatedDeviceDto> result = new ArrayList<>();
            for (Object[] obj : repository.getDetailNotCreatedDeviceList(startDate, endDate)) {
                DetailNotCreatedDeviceDto dto = new DetailNotCreatedDeviceDto();
                dto.setCount(((BigInteger) obj[0]).longValue());
                if (obj[1] != null)
                    dto.setStatus(DeviceStatus.valueOf(obj[1].toString()));
                if (obj[2] != null)
                    dto.setSuspendStatus(DeviceSuspendStatus.valueOf(obj[2].toString()));
                if (obj[3] != null)
                    dto.setTariffId(((BigInteger) obj[3]).longValue());
                dto.setServiceAllowed((Boolean) obj[4]);
                dto.setFreeTrailPeriod((Boolean) obj[5]);
                if (obj[6] != null)
                    dto.setFiscalStatus(DeviceStatus.valueOf(obj[6].toString()));
                result.add(dto);
            }

            Map<String, Long> report = new HashMap<>();
            for (DetailNotCreatedDeviceDto dto : result) {
                if (dto.getTariffId() == null) {
                    update(report, "сервисе не включон", dto.getCount());
                } else if ((dto.getTariffId().equals(2L) || dto.getTariffId().equals(4L))) {
                    update(report, "годовые", dto.getCount());
                } else if (dto.getFiscalStatus() != null && (DeviceStatus.DELETED == dto.getFiscalStatus() || DeviceStatus.DIS_FISCAL == dto.getFiscalStatus())) {
                    update(report, "не фискальный снять с регистрации", dto.getCount());
                } else if (dto.getStatus() == DeviceStatus.BLOCKED && DeviceSuspendStatus.SUSPENDED == dto.getSuspendStatus()) {
                    update(report, "приостановленный", dto.getCount());
                } else if (DeviceStatus.BLOCKED == dto.getStatus() && DeviceSuspendStatus.DEREGISTER == dto.getSuspendStatus()) {
                    update(report, "снять с регистрации", dto.getCount());
                } else if (dto.getFreeTrailPeriod()) {
                    update(report, "испытательный период", dto.getCount());
                } else if (dto.getFiscalStatus() == null) {
                    update(report, "не фискальный", dto.getCount());
                } else if (dto.getServiceAllowed()) {
                    update(report, "сервисе не включон", dto.getCount());
                }
            }

            return mapToList(report);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return new ArrayList<>();
        }
    }

    public void update(Map<String, Long> table, String key, Long val) {
        if (!table.containsKey(key)) {
            table.put(key, val);
            return;
        }

        for (Map.Entry<String, Long> entry : table.entrySet()) {
            if (entry.getKey().equals(key)) {
                Long newValue = val + entry.getValue();
                entry.setValue(newValue);
                break;
            }
        }
    }

    public List<DetailNotCreatedReportDto> mapToList(Map<String, Long> map) {
        List<DetailNotCreatedReportDto> thingList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            DetailNotCreatedReportDto dto = new DetailNotCreatedReportDto();
            dto.setReason(entry.getKey());
            dto.setCount(entry.getValue());
            thingList.add(dto);
        }

        return thingList;
    }

    public void clientBalanceFix(String tin) {
        List<LedgerTest> list;
        if (StringUtils.isNotEmpty(tin)) {
            list = ledgerTestRepository.findAllByTin(tin);
        } else {
            list = ledgerTestRepository.findAll();
        }
        list.forEach(ledgerTest -> {
            for (int i = 1; i <= ledgerTest.getCount() - ledgerTest.getAPayCount(); i++) {
                this.revertMoneyToBank(LedgerRevertAmountRequestDto.builder()
                        .amount(ledgerTest.getAmount())
                        .tin(ledgerTest.getTin())
                        .description("Неправильное пополнение : " + ledgerTest.getAmount())
                        .build());
            }
        });
    }
}
