package uz.uzkassa.cts.service.impl;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.domain.DeviceTariff;
import uz.uzkassa.cts.domain.LedgerDetail;
import uz.uzkassa.cts.dto.cron.CronDeviceSwitchDto;
import uz.uzkassa.cts.dto.launcher.LauncherNotification;
import uz.uzkassa.cts.dto.launcher.LauncherRabbitPaginationRequestDto;
import uz.uzkassa.cts.dto.launcher.LauncherTerminalRequestDto;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.enums.DeviceStatus;
import uz.uzkassa.cts.enums.PeriodType;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqLauncherProducer;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.DeviceTariffRepository;
import uz.uzkassa.cts.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class DeviceTariffServiceImpl extends BaseServiceImpl<DeviceTariff, Long> implements DeviceTariffService {

    private final LedgerService ledgerService;
    private final CronService cronService;
    private final LauncherService launcherService;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final ThymeleafService thymeleafService;
    private final RabbitMqLauncherProducer rabbitMqLauncherProducer;

    public DeviceTariffServiceImpl(DeviceTariffRepository repository,
                                   LedgerService ledgerService,
                                   CronService cronService,
                                   LauncherService launcherService,
                                   CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                   ThymeleafService thymeleafService,
                                   RabbitMqLauncherProducer rabbitMqLauncherProducer) {
        super(repository);
        this.ledgerService = ledgerService;
        this.cronService = cronService;
        this.launcherService = launcherService;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.thymeleafService = thymeleafService;
        this.rabbitMqLauncherProducer = rabbitMqLauncherProducer;
    }

    @Override
    public void switchDevices(LocalDate localDate) {
        List<CronDeviceSwitchDto> deviceList = cronService.getCustomerDevicesForStatusChange(localDate);
        Map<String, List<CronDeviceSwitchDto>> map = deviceList.stream().collect(Collectors.groupingBy(CronDeviceSwitchDto::getCustomerTin));

        List<LedgerDetail> customersBalance = ledgerService.getAllCustomersServiceBalance();
        Map<String, LedgerDetail> balanceMap = mapLedgerWithTin(customersBalance);

        LauncherTerminalRequestDto blockList = new LauncherTerminalRequestDto();
        blockList.setAction("BLOCK");
        blockList.setMessage(thymeleafService.generateCronBlockTextHTML());
        LauncherTerminalRequestDto unBlockList = new LauncherTerminalRequestDto();
        unBlockList.setAction("UNBLOCK");
        //unBlockList.setMessage("Payment Accepted");

        for (Map.Entry<String, List<CronDeviceSwitchDto>> entry : map.entrySet()) {
            String tin = entry.getKey();
            List<CronDeviceSwitchDto> deviceSwitchDtos = entry.getValue();

            BigDecimal needAmount = BigDecimal.ZERO;
            for (CronDeviceSwitchDto dto : deviceSwitchDtos) {
                if (isTimeExpired(dto.getDueDate()) && dto.getTariffPeriodType() != PeriodType.YEARLY) {
                    needAmount = needAmount.add(dto.getTariffPrice());
                }
            }

            LedgerDetail ledgerDetail = balanceMap.get(tin);
            BigDecimal clientBalance = ledgerDetail != null ? ledgerDetail.getBalance() : BigDecimal.ZERO;

            for (CronDeviceSwitchDto dto : deviceSwitchDtos) {

                if (isTimeExpired(dto.getDueDate())) {
                    if (dto.getTariffPeriodType() == PeriodType.MONTHLY) {
                        //if time expired, money enough unblocked devices
                        if (isBalanceEnough(needAmount, clientBalance)) {
                            if (dto.getStatus() == DeviceStatus.REGISTERED || dto.getStatus() == DeviceStatus.BLOCKED) {
                                unBlockList.getTerminalNumbers().add(dto.getSerialNumber());
                            }
                        }
                        //if time expired money not enough block device
                        else {
                            if (dto.getStatus() != DeviceStatus.BLOCKED) {
                                blockList.getTerminalNumbers().add(dto.getSerialNumber());
                            }
                        }

                    } else if (dto.getTariffPeriodType() == PeriodType.YEARLY) {
                        //if time expired and tariff is yearly blockA
                        if (dto.getStatus() != DeviceStatus.BLOCKED) {
                            blockList.getTerminalNumbers().add(dto.getSerialNumber());
                        }
                    }
                } else if (dto.isFreeTrailPeriod() && dto.getStatus() == DeviceStatus.BLOCKED) {
                    unBlockList.getTerminalNumbers().add(dto.getSerialNumber());
                }
            }
        }
    }

    @Override
    public void notifyDevices(LauncherNotification notification) {
        List<CustomerBranchDevice> devices = customerBranchDeviceRepository.findAll();
        LauncherTerminalRequestDto dto = new LauncherTerminalRequestDto();
        dto.setNotification(notification);
        List<String> serialNumbers = new ArrayList<>();
        for (CustomerBranchDevice dev : devices) {
            serialNumbers.add(dev.getDevice().getSerialNumber());
        }
//        serialNumbers.add("N5000103180");
        dto.setTerminalNumbers(serialNumbers);
        launcherService.notify(dto);
    }

    @Override
    public void blockUnblockDevices() {
        log.info("block unblock begin");
        long count = customerBranchDeviceRepository.count();
        log.info("device size: {}", count);
        int limit = 500;
        int page = (int) Math.ceil((double) count / limit);
        for (int i = 0; i < page; i++) {
            LauncherRabbitPaginationRequestDto dto = new LauncherRabbitPaginationRequestDto();
            dto.setPage(i);
            dto.setSize(limit);
            rabbitMqLauncherProducer.launcherSync(dto);
        }
    }

    public void blockUnblockDevices(LauncherRabbitPaginationRequestDto dto) {
        Pageable paging = PageRequest.of(dto.getPage(), dto.getSize());
        Page<CustomerBranchDevice> devices = customerBranchDeviceRepository.findAll(paging);

        LauncherTerminalRequestDto unBlockList = new LauncherTerminalRequestDto();
        unBlockList.setAction("UNBLOCK");
        LauncherTerminalRequestDto blockList = new LauncherTerminalRequestDto();
        blockList.setAction("BLOCK");
        blockList.setMessage(thymeleafService.generateCronBlockTextHTML());

        for (CustomerBranchDevice dev : devices) {
            if (CustomerAppType.CTS == dev.getCustomerBranch().getCustomer().getAppType()) {
                if (dev.getStatus() == DeviceStatus.BLOCKED)
                    blockList.getTerminalNumbers().add(dev.getDevice().getSerialNumber());
                else if (dev.getStatus() == DeviceStatus.UN_BLOCKED)
                    unBlockList.getTerminalNumbers().add(dev.getDevice().getSerialNumber());
            }
        }
//        blockList.getTerminalNumbers().add("N5000103180");
        launcherService.action(blockList);
        launcherService.action(unBlockList);
    }


    private boolean isTimeExpired(LocalDate dueDate) {
        return dueDate.isEqual(LocalDate.now()) || dueDate.isBefore(LocalDate.now());
    }

    private boolean isBalanceEnough(BigDecimal needAmount, BigDecimal ledgerBalance) {
        if (ledgerBalance.compareTo(BigDecimal.ZERO) == 0)
            return false;
        return needAmount.compareTo(ledgerBalance) <= 0;
    }


    private Map<String, LedgerDetail> mapLedgerWithTin(List<LedgerDetail> details) {
        Map<String, LedgerDetail> resultsMap = new HashMap<>();
        for (LedgerDetail detail : details) {
            resultsMap.put(detail.getTin(), detail);
        }
        return resultsMap;
    }


}
