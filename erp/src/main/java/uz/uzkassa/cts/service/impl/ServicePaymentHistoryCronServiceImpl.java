package uz.uzkassa.cts.service.impl;

import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.cron.CronInvoiceDto;
import uz.uzkassa.cts.dto.customer.CustomerTinAndIdDto;
import uz.uzkassa.cts.dto.invoice.request.LedgerInvoiceRequestDto;
import uz.uzkassa.cts.dto.launcher.LauncherStatus;
import uz.uzkassa.cts.dto.ledger.response.CurrentStatementResponseDto;
import uz.uzkassa.cts.dto.ledger.response.LedgerResponseDto;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.servicepaymenthistory.ServicePaymentHistoryCronDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.ServicePaymentHistoryForCronRepository;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.utils.NumberToWordUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static uz.uzkassa.cts.enums.DeviceStatus.BLOCKED;
import static uz.uzkassa.cts.enums.DeviceStatus.UN_BLOCKED;
import static uz.uzkassa.cts.enums.DeviceSuspendStatus.DEREGISTER;
import static uz.uzkassa.cts.enums.DeviceSuspendStatus.SUSPENDED;

@Transactional
@Slf4j
@Service
public class ServicePaymentHistoryCronServiceImpl extends BaseServiceImpl<ServicePaymentHistory, Long> implements ServicePaymentHistoryCronService {

    private final ServicePaymentHistoryForCronRepository repository;
    private final LedgerService ledgerService;
    private final LauncherService launcherService;
    private final ServiceOrderInvoiceOldService serviceOrderInvoiceOldService;
    private final ContractService contractService;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerBranchDeviceService customerBranchDeviceService;
    private final VatService vatService;
    private final CustomerRepository customerRepository;
    private final DeviceSuspendHistoryOldService deviceSuspendHistoryOldService;
    private final TariffService tariffService;

    public ServicePaymentHistoryCronServiceImpl(ServicePaymentHistoryForCronRepository repository,
                                                LedgerService ledgerService, LauncherService launcherService,
                                                ServiceOrderInvoiceOldService serviceOrderInvoiceOldService,
                                                ContractService contractService,
                                                CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                                CustomerBranchDeviceService customerBranchDeviceService,
                                                VatService vatService,
                                                CustomerRepository customerRepository,
                                                DeviceSuspendHistoryOldService deviceSuspendHistoryOldService,
                                                TariffService tariffService) {
        super(repository);
        this.repository = repository;
        this.ledgerService = ledgerService;
        this.launcherService = launcherService;
        this.serviceOrderInvoiceOldService = serviceOrderInvoiceOldService;
        this.contractService = contractService;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.customerBranchDeviceService = customerBranchDeviceService;
        this.vatService = vatService;
        this.customerRepository = customerRepository;
        this.deviceSuspendHistoryOldService = deviceSuspendHistoryOldService;
        this.tariffService = tariffService;
    }

    @Override
    public List<ServicePaymentHistoryCronDto> getCustomerDevices(String tin) {
        List<ServicePaymentHistoryCronDto> paymentHistoryCronDtos = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        repository.getCustomerDevices(tin).forEach(obj -> {
            ServicePaymentHistoryCronDto paymentHistoryCronDto = new ServicePaymentHistoryCronDto();
            paymentHistoryCronDto.setCustomerDeviceBranchId(((Long) obj[0]));
            paymentHistoryCronDto.setDeleted((Boolean) obj[1]);
            paymentHistoryCronDto.setCustomerBranchId(((Long) obj[2]));
            paymentHistoryCronDto.setDeviceId(((Long) obj[3]));
            paymentHistoryCronDto.setStatus(DeviceStatus.valueOf(obj[4].toString()));
            paymentHistoryCronDto.setDueDate(LocalDate.parse(obj[5].toString(), formatter));
            paymentHistoryCronDto.setFreeTrailPeriod((Boolean) obj[6]);
            paymentHistoryCronDto.setStartDate(LocalDate.parse(obj[7].toString(), formatter));
            paymentHistoryCronDto.setServiceAllowed((Boolean) obj[8]);
            paymentHistoryCronDto.setTariffId(((Long) obj[9]));
            paymentHistoryCronDto.setTariffName(obj[10].toString());
            paymentHistoryCronDto.setTariffPeriod((Integer) obj[11]);
            paymentHistoryCronDto.setTariffPeriodType(PeriodType.valueOf(obj[12].toString()));
            paymentHistoryCronDto.setTariffPrice((BigDecimal) obj[13]);
            paymentHistoryCronDto.setTariffSpecialType(obj[14].toString());
            paymentHistoryCronDto.setTariffFreeTrailPeriod((Integer) obj[15]);
            paymentHistoryCronDto.setTariffFreeTrailPeriodType(FreeTrialPeriodType.valueOf(obj[16].toString()));
            paymentHistoryCronDto.setCustomerId(((Long) obj[17]));
            paymentHistoryCronDto.setCustomerTin(obj[18].toString());
            if (obj[19] != null)
                paymentHistoryCronDto.setContractId(((Long) obj[19]));
            paymentHistoryCronDto.setSerialNumber(obj[20].toString());
            if (obj[21] != null)
                paymentHistoryCronDto.setFiscalizationStatus(DeviceStatus.valueOf(obj[21].toString()));
            if (obj[22] != null)
                paymentHistoryCronDto.setInitialDate(LocalDate.parse(obj[22].toString(), formatter));
            if (obj[23] != null)
                paymentHistoryCronDto.setSuspendStatus(DeviceSuspendStatus.valueOf(obj[23].toString()));
            if (obj[24] != null)
                paymentHistoryCronDto.setSuspendDate(LocalDate.parse(obj[24].toString(), formatter));
            if (obj[25] != null)
                paymentHistoryCronDto.setFiscalizationCreatedDate(LocalDate.parse(obj[25].toString().substring(0, 10), formatter));
            paymentHistoryCronDtos.add(paymentHistoryCronDto);
        });
        return paymentHistoryCronDtos;
    }

    @Override
    public void createMonthInvoiceForTest(String tin) {
        creteInvoiceMonthlyPeriodType(tin);
    }

    @Override
    public void createYearlyInvoiceForTest(String tin) {
        creteInvoiceYearlyPeriodType(tin);
    }

    private boolean isTimeExpired(LocalDate dueDate) {
        return dueDate.equals(LocalDate.now()) || dueDate.isBefore(LocalDate.now());
    }

    @Override
    public void paymentHistoryCreate(String tin) {
        List<ServicePaymentHistoryCronDto> list = getCustomerDevices(tin);
        if (list.isEmpty())
            return;

        Long customerId = list.getFirst().getCustomerId();
        List<Long> customerBranchDeviceIds = list
            .stream()
            .map(ServicePaymentHistoryCronDto::getCustomerDeviceBranchId)
            .collect(Collectors.toList());

        Set<ServicePaymentHistory> historySet = repository.findLastHistories(customerId, customerBranchDeviceIds);

        List<ServicePaymentHistory> histories = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (ServicePaymentHistoryCronDto dto : list) {

            if (!isTimeExpired(dto.getDueDate()) && dto.isFreeTrailPeriod()) {
                launcherDeviceStatusSwitch(Collections.singletonList(dto.getSerialNumber()), LauncherStatus.UNBLOCK);
                continue;
            }

            if ((dto.getSuspendDate() != null && isTimeExpired(dto.getSuspendDate())) &&
                (SUSPENDED == dto.getSuspendStatus() || DEREGISTER == dto.getSuspendStatus())) {

                if (UN_BLOCKED == dto.getStatus()) {
                    CustomerBranchDevice customerBranchDevice = customerBranchDeviceService.findById(dto.getCustomerDeviceBranchId());
                    customerBranchDevice.setStatus(BLOCKED);
                    customerBranchDeviceService.save(customerBranchDevice);

                    launcherDeviceStatusSwitch(Collections.singletonList(dto.getSerialNumber()), LauncherStatus.BLOCK);
                }
                continue;
            }
            // SUSPEND DEVICE IF NOT PAID FOR SERVICE IN 2 MONTH
            if (SUSPENDED != dto.getSuspendStatus() && PeriodType.YEARLY != dto.getTariffPeriodType()) {
                CurrentStatementResponseDto needAmount = ledgerService.getNeedAmountAndStatement(customerId);
                if (needAmount.getNeedAmount().compareTo(BigDecimal.ZERO) < 0) {
                    LedgerResponseDto ledgerResponseDto = ledgerService.getClientLastTopUp(customerId);
                    LocalDate lasPaidMonthFirstDay;
                    if (ledgerResponseDto != null) {
                        lasPaidMonthFirstDay = DateUtil.getMonthFirstDate(ledgerResponseDto.getCreatedDate().toLocalDate());
                    } else {
                        lasPaidMonthFirstDay = DateUtil.getMonthFirstDate(dto.getInitialDate());
                    }
                    LocalDate thisMonthFirstDay = DateUtil.getMonthFirstDate();
                    long monthsBetween = ChronoUnit.MONTHS.between(lasPaidMonthFirstDay, thisMonthFirstDay);
                    if (monthsBetween > 1) {
                        customerBranchDeviceService.suspend(dto.getCustomerDeviceBranchId());
                        continue;
                    }
                }
            }

            LocalDate startDate = LocalDate.now();
            LocalDate calculatedDate = DateUtil.calculateDueDate();
            Long customerBranchDeviceId = null;

            if (dto.getTariffPeriodType() == PeriodType.YEARLY) {

                boolean hasRow = false;
                for (ServicePaymentHistory h : historySet) {
                    if ((h.getCustomerBranchDeviceId().equals(dto.getCustomerDeviceBranchId()) ||
                        h.getDeviceSerialNumber().equals(dto.getSerialNumber())) &&
                        !isTimeExpired(h.getDueDate())) {
                        hasRow = true;
                        break;
                    }
                }

                if (!hasRow) {
                    ServicePaymentHistory history = new ServicePaymentHistory();
                    history.setContractId(dto.getContractId());
                    history.setCustomerBranchDeviceId(dto.getCustomerDeviceBranchId());
                    history.setCustomerId(dto.getCustomerId());
                    if (isTimeExpired(dto.getDueDate())) {
                        LocalDate date = dto.getDueDate().plusDays(1);
                        history.setMonth(date.getMonth());
                        history.setYear(date.getYear());
                    } else {
                        history.setMonth(dto.getStartDate().getMonth());
                        history.setYear(dto.getStartDate().getYear());
                    }
                    history.setAmount(dto.getTariffPrice());
                    history.setTariffId(dto.getTariffId());
                    history.setStartDate(dto.getDueDate());
                    history.setDueDate(dto.getDueDate().plusYears(1));
                    history.setDeviceSerialNumber(dto.getSerialNumber());
                    histories.add(history);

                    startDate = history.getStartDate();
                    calculatedDate = history.getDueDate();
                    customerBranchDeviceId = history.getCustomerBranchDeviceId();
                }
            } else {
                while (dto.getDueDate().isBefore(calculatedDate) || dto.getDueDate().equals(calculatedDate)) {

                    if (dto.isFreeTrailPeriod() || dto.getStartDate().equals(dto.getInitialDate())) {
                        dto.setStartDate(dto.getDueDate());
                        dto.setDueDate(DateUtil.calculateDueDate(dto.getStartDate()));
                        dto.setFreeTrailPeriod(false);
                    }

                    ServicePaymentHistory history = new ServicePaymentHistory();
                    history.setContractId(dto.getContractId());
                    history.setCustomerBranchDeviceId(dto.getCustomerDeviceBranchId());
                    history.setCustomerId(dto.getCustomerId());
                    history.setTariffId(dto.getTariffId());
                    history.setDeviceSerialNumber(dto.getSerialNumber());
                    history.setStartDate(dto.getStartDate());
                    history.setMonth(history.getStartDate().getMonth());
                    history.setYear(history.getStartDate().getYear());
                    history.setDueDate(dto.getDueDate());
                    BigDecimal calculatedAmount = BigDecimalUtil.calculatePrice(dto.getStartDate(), dto.getDueDate(), dto.getTariffPrice());
                    history.setAmount(BigDecimalUtil.setScaleByTwo(calculatedAmount));

                    if (!historySet.contains(history)) {
                        histories.add(history);
                        //get start date and due date for blocking
                        startDate = history.getStartDate();
                        calculatedDate = history.getDueDate();
                        customerBranchDeviceId = history.getCustomerBranchDeviceId();
                    }
                    //change StartDate to next Calculation to end of month to calculate amount
                    if (dto.getDueDate().equals(calculatedDate)) {
                        break;
                    }
                    dto.setStartDate(history.getDueDate());
                    dto.setDueDate(DateUtil.calculateDueDate(history.getDueDate()));
                }
            }
            if (customerBranchDeviceId != null) {
                builder.append(" Update customer_branch_device set ");
                builder.append(" start_date ='").append(startDate).append("', ");
                builder.append(" due_date ='").append(calculatedDate).append("', ");
                builder.append(" is_free_trail_period =").append(Boolean.FALSE).append(" ");
                builder.append(" where id = ").append(customerBranchDeviceId).append(";");
            }
        }
        if (!histories.isEmpty()) {
            try {
                repository.saveAll(histories);
                String sql = builder.toString();
                if (!sql.isEmpty()) {
                    Query query = entityManager.createNativeQuery(sql);
                    query.executeUpdate();
                }
            } catch (DataIntegrityViolationException e) {
                log.error(e.getLocalizedMessage());
            }
        }

    }

    public void paymentHistoryCreateNew(String tin) {
//        List<ServicePaymentHistoryCronDto> list = getCustomerDevices(tin);
//        if (list.isEmpty())
//            return;
//
//        Long customerId = list.get(0).getCustomerId();
//        List<Long> customerBranchDeviceIds = list
//            .stream()
//            .map(ServicePaymentHistoryCronDto::getCustomerDeviceBranchId)
//            .collect(Collectors.toList());
//
//        Set<ServicePaymentHistory> historySet = repository.findLastHistories(customerId, customerBranchDeviceIds);
//
//        List<ServicePaymentHistory> histories = new ArrayList<>();
//        StringBuilder builder = new StringBuilder();
//        for (ServicePaymentHistoryCronDto dto : list) {
//
//            if (!isTimeExpired(dto.getDueDate()) && dto.isFreeTrailPeriod()) {
//                launcherDeviceStatusSwitch(Collections.singletonList(dto.getSerialNumber()), LauncherEnum.UNBLOCK);
//                continue;
//            }
//
//            if ((dto.getSuspendDate() != null && isTimeExpired(dto.getSuspendDate())) &&
//                (SUSPENDED == dto.getSuspendStatus() || DEREGISTER == dto.getSuspendStatus())) {
//
//                if (UN_BLOCKED == dto.getStatus()) {
//                    CustomerBranchDevice customerBranchDevice = customerBranchDeviceService.findById(dto.getCustomerDeviceBranchId());
//                    customerBranchDevice.setStatus(BLOCKED);
//                    customerBranchDeviceService.save(customerBranchDevice);
//
//                    launcherDeviceStatusSwitch(Collections.singletonList(dto.getSerialNumber()), LauncherEnum.BLOCK);
//                }
//                continue;
//            }
//            // SUSPEND DEVICE IF NOT PAID FOR SERVICE IN 2 MONTH
////            if (SUSPENDED != dto.getSuspendStatus() && PeriodType.YEARLY != dto.getTariffPeriodType()) {
////                CurrentStatementResponseDto needAmount = ledgerService.getNeedAmountAndStatement(customerId);
////                if (needAmount.getNeedAmount().compareTo(BigDecimal.ZERO) < 0) {
////                    LedgerResponseDto ledgerResponseDto = ledgerService.getClientLastTopUp(customerId);
////                    LocalDate lasPaidMonthFirstDay;
////                    if (ledgerResponseDto != null) {
////                        lasPaidMonthFirstDay = DateUtil.getMonthFirstDate(ledgerResponseDto.getCreatedDate().toLocalDate());
////                    } else {
////                        lasPaidMonthFirstDay = DateUtil.getMonthFirstDate(dto.getInitialDate());
////                    }
////                    LocalDate thisMonthFirstDay = DateUtil.getMonthFirstDate();
////                    long monthsBetween = ChronoUnit.MONTHS.between(lasPaidMonthFirstDay, thisMonthFirstDay);
////                    if (monthsBetween > 1) {
////                        customerBranchDeviceService.suspend(dto.getCustomerDeviceBranchId());
////                        continue;
////                    }
////                }
////            }
//
//            LocalDate calculatedDate = DateUtil.calculateDueDate();
//            Long customerBranchDeviceId = null;
//
//            if (dto.isFreeTrailPeriod() || dto.getStartDate().equals(dto.getInitialDate())) {
//                dto.setStartDate(dto.getDueDate());
//                dto.setDueDate(DateUtil.calculateDueDate(dto.getStartDate()));
//                dto.setFreeTrailPeriod(false);
//            }
//            if (dto.getDueDate().isBefore(calculatedDate) || dto.getDueDate().equals(calculatedDate)) {
//
//                ServicePaymentHistory history = new ServicePaymentHistory();
//                history.setContractId(dto.getContractId());
//                history.setCustomerBranchDeviceId(dto.getCustomerDeviceBranchId());
//                history.setCustomerId(dto.getCustomerId());
//                history.setTariffId(dto.getTariffId());
//                history.setDeviceSerialNumber(dto.getSerialNumber());
//                history.setStartDate(dto.getStartDate());
//                history.setMonth(dto.getStartDate().getMonth());
//
//                if (PeriodType.YEARLY == dto.getTariffPeriodType()) {
//                    history.setDueDate(dto.getStartDate().plusYears(1));
//                    history.setYear(dto.getDueDate().getYear());
//                } else {
//                    history.setYear(dto.getStartDate().getYear());
//                    history.setDueDate(DateUtil.calculateDueDate(dto.getStartDate()));
//                }
//
//                BigDecimal calculatedAmount = dto.getTariffPrice();
//                if (PeriodType.MONTHLY == dto.getTariffPeriodType()) {
//                    calculatedAmount = BigDecimalUtil.calculatePrice(history.getStartDate(), history.getDueDate(), dto.getTariffPrice());
//                }
//                history.setAmount(BigDecimalUtil.setScaleByTwo(calculatedAmount));
//
//                if (!historySet.contains(history)) {
//                    histories.add(history);
//                    //get start date and due date for blocking
//                    calculatedDate = history.getDueDate();
//                    customerBranchDeviceId = history.getCustomerBranchDeviceId();
//                }
//
//                if (dto.getDueDate().equals(calculatedDate)) {
//                    continue;
//                }
//                dto.setStartDate(history.getDueDate());
//                dto.setDueDate(DateUtil.calculateDueDate(history.getDueDate()));
//
//                if (customerBranchDeviceId != null) {
//                    builder.append(" Update customer_branch_device set ");
//                    builder.append(" start_date ='").append(dto.getStartDate()).append("', ");
//                    builder.append(" due_date ='").append(dto.getDueDate()).append("', ");
//                    builder.append(" is_free_trail_period =").append(Boolean.FALSE).append(" ");
//                    builder.append(" where id = ").append(history.getCustomerBranchDeviceId()).append(";");
//                }
//            }
//        }
//
//        String sql = builder.toString();
//        if (!sql.isEmpty()) {
//            entityManager.createNativeQuery(sql).executeUpdate();
//        }
//        if (!histories.isEmpty()) {
//            repository.saveAll(histories);
//        }
    }


    @Override
    public void deviceStatusSwitch(String tin) {

        List<String> unBlockList = new ArrayList<>();
        List<String> blockList = new ArrayList<>();

        CustomerTinAndIdDto customer = customerRepository.findFirstByTinOrOldTin(tin, tin, CustomerTinAndIdDto.class)
            .orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));

        CurrentStatementResponseDto balance = ledgerService.getNeedAmountAndStatement(customer.getId());
        List<CustomerBranchDevice> customerBranchDevices = customerBranchDeviceRepository.findByCustomerId(customer.getId());
        log.info("custome: {} balance: {} needAmount: {}", customer.getTin(), balance.getBalance(), balance.getNeedAmount());
        if (balance.getNeedAmount().compareTo(BigDecimal.ZERO) >= 0) {
            for (CustomerBranchDevice customerBranchDevice : customerBranchDevices) {
                log.info("BEFORE custome: {} deviceNumber: {}, suspendStatus: {}, status: {}",
                        customer.getTin(), customerBranchDevice.getDevice().getSerialNumber(),
                        customerBranchDevice.getSuspendStatus(), customerBranchDevice.getStatus());
                activate(customerBranchDevice);
                log.info("AFTER custome: {} deviceNumber: {}, suspendStatus: {}, status: {}",
                        customer.getTin(), customerBranchDevice.getDevice().getSerialNumber(),
                        customerBranchDevice.getSuspendStatus(), customerBranchDevice.getStatus());
                if (BLOCKED == customerBranchDevice.getStatus()) {
                    if (!(SUSPENDED == customerBranchDevice.getSuspendStatus() ||
                        DEREGISTER == customerBranchDevice.getSuspendStatus())) {
                        customerBranchDevice.setStatus(UN_BLOCKED);
                        unBlockList.add(customerBranchDevice.getDevice().getSerialNumber());
                        log.info("custome: {} deviceNumber: {}, status: {}",
                                customer.getTin(), customerBranchDevice.getDevice().getSerialNumber(),
                                 customerBranchDevice.getStatus());
                    }
                } else {
                    if ((SUSPENDED == customerBranchDevice.getSuspendStatus() &&
                        customerBranchDevice.getSuspendDate().isBefore(LocalDate.now())) ||
                        DEREGISTER == customerBranchDevice.getSuspendStatus()) {
                        customerBranchDevice.setStatus(BLOCKED);
                        blockList.add(customerBranchDevice.getDevice().getSerialNumber());
                        log.info("custome: {} deviceNumber: {}, status: {}",
                                customer.getTin(), customerBranchDevice.getDevice().getSerialNumber(),
                                customerBranchDevice.getStatus());
                    }
                }
            }
        } else {
            for (CustomerBranchDevice customerBranchDevice : customerBranchDevices) {
                //unlock until free period, free period will set by operators
                if (customerBranchDevice.getFreePeriodDate() != null &&
                    !LocalDate.now().isAfter(customerBranchDevice.getFreePeriodDate()) &&
                    BLOCKED == customerBranchDevice.getStatus()) {
                    unBlockList.add(customerBranchDevice.getDevice().getSerialNumber());
                    log.info("custome: {} deviceNumber: {}, status: {}",
                            customer.getTin(), customerBranchDevice.getDevice().getSerialNumber(),
                            customerBranchDevice.getStatus());
                } else {
                    customerBranchDevice.setStatus(BLOCKED);
                    blockList.add(customerBranchDevice.getDevice().getSerialNumber());
                    log.info("custome: {} deviceNumber: {}, status: {}",
                            customer.getTin(), customerBranchDevice.getDevice().getSerialNumber(),
                            customerBranchDevice.getStatus());
                }
            }
        }

        if (!blockList.isEmpty()) {
            launcherDeviceStatusSwitch(blockList, LauncherStatus.BLOCK);
        }
        if (!unBlockList.isEmpty()) {
            launcherDeviceStatusSwitch(unBlockList, LauncherStatus.UNBLOCK);
        }
        customerBranchDeviceRepository.saveAll(customerBranchDevices);
    }

    public void activate(CustomerBranchDevice customerBranchDevice) {
        DeviceSuspendHistory history = deviceSuspendHistoryOldService.getLastByCustomerBranchDeviceId(customerBranchDevice.getId());
        if (history == null)
            return;

        if (history.getCreatorId() != null) {
            return;
        }

        if (PeriodType.YEARLY == customerBranchDevice.getTariff().getPeriodType())
            return;

        LocalDate today = LocalDate.now();
        history.setDueDate(today);
        history.setActive(false);
        deviceSuspendHistoryOldService.save(history);

        customerBranchDevice.setSuspendStatus(DeviceSuspendStatus.ACTIVE);
        customerBranchDevice.setSuspendDate(null);
        LocalDate endDate = DateUtil.calculateDueDate();
        if (!endDate.equals(today)) {
            customerBranchDevice.setStartDate(today);
            customerBranchDevice.setDueDate(endDate);
        }
    }

    @Override
    public void recalculation(Payload payload) {
        customerRepository
            .findById(payload.getId())
            .ifPresent(customer -> customerBranchDeviceRepository
                .findByCustomerId(payload.getId())
                .forEach(customerBranchDevice ->
                    this.recalculateHistory(customer.getTin(), customerBranchDevice.getDevice().getSerialNumber(), LocalDate.now())
                ));
    }

    @Transactional
    public void recalculateHistory(String tin, String deviceSerialNumber, LocalDate stopDate) {
        ServicePaymentHistory history = repository.getHistoryByDeviceSerialNumberAndTin(tin, deviceSerialNumber, stopDate.getMonth());
        CustomerBranchDevice customerBranchDevice = null;
        if (history != null) {
            if (history.getTariff().getPeriodType() != PeriodType.YEARLY) {
                // recalculate history for create invoice and block this KKM
                BigDecimal calculatedAmount = BigDecimalUtil.calculatePrice(history.getStartDate(), stopDate, history.getTariff().getPrice());
                if (calculatedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    throw validationService.badRequestExceptionThrow(AMOUNT_ERROR).get();
                }
                history.setAmount(calculatedAmount);
                history.setDueDate(stopDate);
                // create invoice only to this history
                creteInvoiceForRecalculation(history, tin);
                repository.save(history);
            }
            customerBranchDevice = history.getCustomerBranchDevice();
        }

        //Set Status to DEREGISTER
        if (customerBranchDevice == null) {
            customerBranchDevice = customerBranchDeviceRepository.findOneByTinAndDeviceSerialNumber(tin, deviceSerialNumber).orElse(null);
        }
        if (customerBranchDevice == null) {
            return;
        }

        if (customerBranchDevice.getTariff() != null && customerBranchDevice.getTariff().getPeriodType() != PeriodType.YEARLY) {
            customerBranchDevice.setDueDate(stopDate);
        }
        customerBranchDevice.setSuspendStatus(DEREGISTER);
        customerBranchDevice.setSuspendDate(stopDate);
        customerBranchDevice.setServiceAllowed(true);
        customerBranchDevice.setTariffId(null);
        customerBranchDevice.setContractId(null);
        customerBranchDevice.setStatus(BLOCKED);
        customerBranchDeviceRepository.save(customerBranchDevice);

        // Block KKM after unregister
        launcherDeviceStatusSwitch(Collections.singletonList(customerBranchDevice.getDevice().getSerialNumber()), LauncherStatus.BLOCK);
    }


    @Override
    public void deviceStatusSyncWithLauncher(String tin) {
        deviceStatusSwitch(tin);
    }


    private void launcherDeviceStatusSwitch(List<String> deviceList, LauncherStatus launcherStatus) {
        if (launcherStatus.equals(LauncherStatus.BLOCK))
            launcherService.block(deviceList);
        else
            launcherService.unlock(deviceList);
    }

    @Override
    public void createInvoice(String tin) {
        creteInvoiceYearlyPeriodType(tin);
        if (DateUtil.getMonthLastDate().isEqual(LocalDate.now())) {
            creteInvoiceMonthlyPeriodType(tin);
        }
    }


    private void creteInvoiceForRecalculation(ServicePaymentHistory history, String tin) {
        List<CronInvoiceDto> dtos = new ArrayList<>();

        Tariff tariff = history.getTariff();
        CronInvoiceDto dto = new CronInvoiceDto();
        dto.setContractId(history.getContractId());
        dto.setDeviceCount(1);
        dto.setTariffPrice(tariff.getPrice());
        dto.setPrice(history.getAmount());
        dto.setTariffName(tariff.getName());
        dto.setTariffId(tariff.getId());
        dtos.add(dto);

        ServiceOrderInvoice invoice = prepareToCreateInvoice(dtos, getVatPercent());

        history.setServiceOrderInvoiceId(invoice.getId());

//        xfileProducer.sendServiceOrderInvoice(InvoiceToFacturaMapping.mapInvoiceToFactura(invoice));

        //switch devices
        deviceStatusSwitch(tin);
    }


    private void creteInvoiceMonthlyPeriodType(String tin) {
        List<ServicePaymentHistory> histories = repository.findForInvoiceMonthlyByCustomerTin(tin);
        if (histories.isEmpty())
            return;

        prepareToCreateInvoice(tin, histories);
    }

    private void prepareToCreateInvoice(String tin, List<ServicePaymentHistory> histories) {
        List<CronInvoiceDto> dtos = new ArrayList<>();
        for (ServicePaymentHistory history : histories) {
            Tariff tariff = history.getTariff();
            CronInvoiceDto dto = new CronInvoiceDto();
            dto.setContractId(history.getContractId());
            dto.setDeviceCount(1);
            dto.setTariffPrice(tariff.getPrice());
            dto.setPrice(history.getAmount());
            dto.setTariffName(tariff.getName());
            dto.setTariffId(tariff.getId());
            dtos.add(dto);
        }

        ServiceOrderInvoice invoice = prepareToCreateInvoice(dtos, getVatPercent());

        for (ServicePaymentHistory history : histories) {
            history.setServiceOrderInvoiceId(invoice.getId());
        }
        repository.saveAll(histories);
        //switch devices
        deviceStatusSwitch(tin);
    }

    private BigDecimal getVatPercent() {
        return vatService.getActiveVat().getPercent();
    }

    private void creteInvoiceYearlyPeriodType(String tin) {
        List<ServicePaymentHistory> histories = repository.findForInvoiceYearlyByCustomerTin(tin);
        if (histories.isEmpty())
            return;

        prepareToCreateInvoice(tin, histories);
    }


    private ServiceOrderInvoice prepareToCreateInvoice(List<CronInvoiceDto> cronInvoiceDtos, BigDecimal vatPercent) {
        ServiceOrderInvoice invoice = new ServiceOrderInvoice();

        Long contractId = 0L;
        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal totalSellPrice = BigDecimal.ZERO;
        BigDecimal totalVatPrice;
        int totalProductCount = 0;

        for (CronInvoiceDto dto : cronInvoiceDtos) {
            contractId = dto.getContractId();

            ServiceOrderInvoiceDetail invoiceDetail = new ServiceOrderInvoiceDetail();

            Integer deviceCount = dto.getDeviceCount();
            BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(dto.getPrice(), vatPercent);
            BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(dto.getPrice(), vatPriceFor1);

            invoiceDetail.setTariffId(dto.getTariffId());
            invoiceDetail.setTariffName(dto.getTariffName());
            invoiceDetail.setTariff(tariffService.findById(dto.getTariffId()));
            invoiceDetail.setProductCount(dto.getDeviceCount());
            invoiceDetail.setPrice1pc(productPriceFor1);

            BigDecimal vatTariffPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(dto.getTariffPrice(), vatPercent);
            BigDecimal productTariffPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(dto.getTariffPrice(), vatTariffPriceFor1);
            invoiceDetail.setSellPrice1pc(productTariffPriceFor1);

            invoiceDetail.setVatPrice1pc(vatPriceFor1);
            invoiceDetail.setVatRate(vatPercent);

            BigDecimal overallPrice = productPriceFor1.multiply(BigDecimal.valueOf(deviceCount));
            BigDecimal overallSellPrice = BigDecimalUtil.setScaleByTwo(dto.getPrice());
            BigDecimal overallVatPrice = vatPriceFor1.multiply(BigDecimal.valueOf(deviceCount));
            invoiceDetail.setOverallPrice(overallPrice);
            invoiceDetail.setOverallSellPrice(BigDecimalUtil.setScaleByTwo(overallSellPrice));
            invoiceDetail.setOverallVatPrice(overallVatPrice);

            invoice.addChild(invoiceDetail);

            totalSellPrice = totalSellPrice.add(overallSellPrice);
            totalProductCount += deviceCount;
        }

        BigDecimal totalVat = BigDecimalUtil.calculateVatPriceFor1(totalSellPrice, vatPercent);
        BigDecimal totalProductPrice = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(totalSellPrice, totalVat);
        totalPrice = totalPrice.add(totalProductPrice);
        totalVatPrice = totalVat;


        invoice.setTotalPrice(BigDecimalUtil.setScaleByTwo(totalPrice));
        invoice.setTotalPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalPrice)));
        invoice.setTotalSellPrice(BigDecimalUtil.setScaleByTwo(totalSellPrice));
        invoice.setTotalSellPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalSellPrice)));
        invoice.setTotalVatPrice(BigDecimalUtil.setScaleByTwo(totalVatPrice));
        invoice.setTotalVatPriceInWord(NumberToWordUtil.digits2Text(BigDecimalUtil.setScaleByTwo(totalVatPrice)));
        invoice.setTotalProductCount(totalProductCount);
        invoice.setStatus(ContractStatus.ACCEPTED);
        invoice.setContractId(contractId);
        Contract contract = contractService.findById(contractId);
        invoice.setContract(contract);
        if (invoice.getTotalSellPrice().compareTo(BigDecimal.ZERO) != 0) {
            invoice = serviceOrderInvoiceOldService.save(invoice);
            changeBalance(invoice);
            return invoice;
        }
        throw validationService.badRequestExceptionThrow(AMOUNT_ERROR).get();
    }

    private void changeBalance(ServiceOrderInvoice invoice) {
        Contract contract = contractService.findById(invoice.getContractId());
        Customer customer = contract.getCustomer();
        Company company = contract.getCompany();

        LedgerInvoiceRequestDto requestDto = new LedgerInvoiceRequestDto()
            .id(invoice.getId())
            .documentNo(contract.getName())
            .transactionNumber(invoice.getId().toString())
            .contractNumber(contract.getName())
            .serviceType(ServiceType.SERVICE)
            .amount(invoice.getTotalSellPrice())
            .paymentDate(invoice.getCreatedDate().toLocalDate())
            .company(company)
            .customer(customer)
            .link("/api/serviceOrderInvoice/" + invoice.getId() + "/generateTemplate")
            .description(invoice.createDescription(contract));
        ledgerService.payForOrder(requestDto);
    }


}
