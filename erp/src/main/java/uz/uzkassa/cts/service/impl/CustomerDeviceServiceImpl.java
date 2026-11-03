package uz.uzkassa.cts.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.report.ContractReport;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.bill.BillDTO;
import uz.uzkassa.cts.dto.bill.BillResponseDTO;
import uz.uzkassa.cts.dto.contractforleasing.request.ContractForLeasingDeviceRequestDto;
import uz.uzkassa.cts.dto.customer.admin.CustomerDevicesByDeviceTypeDTO;
import uz.uzkassa.cts.dto.customerdevice.CustomerDeviceListAdminDTO;
import uz.uzkassa.cts.dto.device.*;
import uz.uzkassa.cts.dto.device.admin.DeviceDetailAdminDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.launcher.LauncherStatus;
import uz.uzkassa.cts.dto.launcher.LauncherTerminalRequestDto;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;
import uz.uzkassa.cts.dto.payment.PaymentContractActDTO;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActPayload;
import uz.uzkassa.cts.dto.rentact.request.RentActRequestDto;
import uz.uzkassa.cts.dto.sync.SyncCustomerDeviceListDTO;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInDeviceAddRequestDto;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqContractReportProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.RandomUtils;

import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
public class CustomerDeviceServiceImpl extends BaseServiceImpl<CustomerDevice, Long> implements CustomerDeviceService {

    private final CustomerDeviceRepository customerDeviceRepository;
    private final CustomerRepository customerRepository;
    private final CompanyDeviceRepository companyDeviceRepository;
    private final BranchDeviceRepository branchDeviceRepository;
    private final DeviceRepository deviceRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final FileUploadRepository fileUploadRepository;
    private final WarehouseHistoryService warehouseHistoryService;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final DeviceService deviceService;
    private final CompanyDeviceService companyDeviceService;
    private final BranchDeviceService branchDeviceService;
    private final UserRepository userRepository;
    private final CustomerBranchRepository customerBranchRepository;
    private final BranchRepository branchRepository;
    private final TradeInContractService tradeInContractService;
    private final RentActService rentActService;
    private final OfdApplicationRepository ofdApplicationRepository;
    private final ActOfTransmissionFactoryService actOfTransmissionService;
    private final LauncherService launcherService;
    private final ThymeleafService thymeleafService;
    private final ContractReportRepository contractReportRepository;
    private final BillService billService;
    private final PaymentContractService paymentContractService;
    private final RabbitMqContractReportProducer rabbitMqContractReportProducer;
    private final RentDeviceActService rentDeviceActService;
    private final ContractLeasingFactoryService contractLeasingService;
    private final RabbitMqSmartPosProducer smartPosProducer;
    private final CustomerBranchDevicePaymentTypeService customerBranchDevicePaymentTypeService;
    private final DeviceModelRepository deviceModelRepository;
    private final CacheService cacheService;
    private final LogHistoryService logHistoryService;


    public CustomerDeviceServiceImpl(CustomerDeviceRepository customerDeviceRepository,
                                     CustomerRepository customerRepository,
                                     CompanyDeviceRepository companyDeviceRepository,
                                     BranchDeviceRepository branchDeviceRepository,
                                     DeviceRepository deviceRepository,
                                     FiscalizationRepository fiscalizationRepository,
                                     FileUploadRepository fileUploadRepository,
                                     WarehouseHistoryService warehouseHistoryService,
                                     CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                     DeviceService deviceService,
                                     CompanyDeviceService companyDeviceService,
                                     BranchDeviceService branchDeviceService,
                                     UserRepository userRepository, CustomerBranchRepository customerBranchRepository,
                                     BranchRepository branchRepository, TradeInContractService tradeInContractService,
                                     RentActService rentActService, OfdApplicationRepository ofdApplicationRepository,
                                     ActOfTransmissionFactoryService actOfTransmissionService, LauncherService launcherService,
                                     ThymeleafService thymeleafService, ContractReportRepository contractReportRepository, BillService billService,
                                     PaymentContractService paymentContractService, RabbitMqContractReportProducer rabbitMqContractReportProducer,
                                     RentDeviceActService rentDeviceActService, ContractLeasingFactoryService contractLeasingService,
                                     RabbitMqSmartPosProducer smartPosProducer, CustomerBranchDevicePaymentTypeService customerBranchDevicePaymentTypeService, DeviceModelRepository deviceModelRepository, CacheService cacheService, LogHistoryService logHistoryService) {
        super(customerDeviceRepository);
        this.customerDeviceRepository = customerDeviceRepository;
        this.customerRepository = customerRepository;
        this.companyDeviceRepository = companyDeviceRepository;
        this.branchDeviceRepository = branchDeviceRepository;
        this.deviceRepository = deviceRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.fileUploadRepository = fileUploadRepository;
        this.warehouseHistoryService = warehouseHistoryService;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.deviceService = deviceService;
        this.companyDeviceService = companyDeviceService;
        this.branchDeviceService = branchDeviceService;
        this.userRepository = userRepository;
        this.customerBranchRepository = customerBranchRepository;
        this.branchRepository = branchRepository;
        this.tradeInContractService = tradeInContractService;
        this.rentActService = rentActService;
        this.ofdApplicationRepository = ofdApplicationRepository;
        this.actOfTransmissionService = actOfTransmissionService;
        this.launcherService = launcherService;
        this.thymeleafService = thymeleafService;
        this.contractReportRepository = contractReportRepository;
        this.billService = billService;
        this.paymentContractService = paymentContractService;
        this.rabbitMqContractReportProducer = rabbitMqContractReportProducer;
        this.rentDeviceActService = rentDeviceActService;
        this.contractLeasingService = contractLeasingService;
        this.smartPosProducer = smartPosProducer;
        this.customerBranchDevicePaymentTypeService = customerBranchDevicePaymentTypeService;
        this.deviceModelRepository = deviceModelRepository;
        this.cacheService = cacheService;
        this.logHistoryService = logHistoryService;
    }

    /**
     * Register old devices
     */
    @Override
    public String register(CustomerDeviceDTO customerDeviceDTO) {
        Customer customer = customerRepository.findById(customerDeviceDTO.getCustomerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        CustomerBranch customerBranch = customerBranchRepository.findById(customerDeviceDTO.getCustomerBranchId()).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
        Long branchId;
        if (SecurityUtils.getBranchId() == null || SecurityUtils.getBranchId() == 0) {
            User user = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
            branchId = user.getBranchId();
        } else {
            branchId = SecurityUtils.getBranchId();
        }
        Branch branch = branchRepository.findById(branchId).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
        if (DeviceType.SOFTWARE == customerDeviceDTO.getDeviceType()) {
            return registerSoftwareDevice(customerDeviceDTO, customer, customerBranch, branch);
        } else {
            return registerPhysicalDevice(customerDeviceDTO, customer, customerBranch, branch);
        }
    }

    private String registerPhysicalDevice(CustomerDeviceDTO customerDeviceDTO, Customer customer, CustomerBranch customerBranch, Branch branch) {
        String billId = "";
        if (DevicePaymentType.RENT == customerDeviceDTO.getDevicePaymentType() && CollectionUtils.isEmpty(customerDeviceDTO.getAccessories())) {
            throw validationService.requiredExceptionThrow(ACCESSORIES).get();
        }
        for (Long deviceId : customerDeviceDTO.getDevices()) {
            Device device = deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
            if (EnumSet.of(DevicePaymentType.REREGISTRATION, DevicePaymentType.WHOLESALE_TRADE, DevicePaymentType.FREE_REPLACEMENT_KKM).contains(customerDeviceDTO.getDevicePaymentType())) {
                Optional<ContractReport> optionalContractReport = contractReportRepository.findBySerialNumber(device.getSerialNumber());
                if (optionalContractReport.isEmpty()) {
                    throw validationService.badRequestExceptionThrow(CONTRACT_REPORT, device.getSerialNumber()).get();
                }
                if (Objects.nonNull(optionalContractReport.get().getSaleStatus())) {
                    ContractReport contractReport = optionalContractReport.get();
                    if (DevicePaymentType.REREGISTRATION.equals(customerDeviceDTO.getDevicePaymentType()) &&
                            EnumSet.of(ContractReportInvoiceSaleStatus.DEFECT,
                                    ContractReportInvoiceSaleStatus.DEVICE_FOR_TEST,
                                    ContractReportInvoiceSaleStatus.EXCHANGE_DEVICE,
                                    ContractReportInvoiceSaleStatus.EMPTY).contains(contractReport.getSaleStatus())) {
                        throw validationService.badRequestExceptionThrow(CONTRACT_REPORT_SALE_STATUS).get();
                    } else {
                        if (DevicePaymentType.WHOLESALE_TRADE.equals(customerDeviceDTO.getDevicePaymentType()) &&
                                !contractReport.getSaleStatus().equals(ContractReportInvoiceSaleStatus.WHOLESALE_TRADE) ||
                                contractReport.getSaleStatus().equals(ContractReportInvoiceSaleStatus.EMPTY) ||
                                contractReport.getSaleStatus().equals(ContractReportInvoiceSaleStatus.RENTAL_CDTI)) {
                            throw validationService.badRequestExceptionThrow(CONTRACT_REPORT_SALE_STATUS).get();
                        } else if (DevicePaymentType.FREE_REPLACEMENT_KKM.equals(customerDeviceDTO.getDevicePaymentType()) &&
                                !contractReport.getSaleStatus().equals(ContractReportInvoiceSaleStatus.FREE_REPLACEMENT_KKM)) {
                            throw validationService.badRequestExceptionThrow(CONTRACT_REPORT_SALE_STATUS).get();
                        }
                    }
                } else {
                    throw validationService.badRequestExceptionThrow(CONTRACT_REPORT_SALE_STATUS).get();
                }
            }
            if (customerDeviceDTO.isFromRegistry()) {
                if (companyDeviceRepository.totalByDeviceId(customerDeviceDTO.getCustomerId(), device.getId()).intValue() > 0) {
                    throw validationService.entityAlreadyExistExceptionThrow(DEVICE, SERIAL_NUMBER, device.getSerialNumber()).get();
                }
                //Register to company
                CompanyDevice companyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(customer.getCompanyId(), device.getId());
                if (companyDevice == null) {
                    companyDevice = new CompanyDevice();
                    companyDevice.setDeviceId(device.getId());
                    companyDevice.setCompanyId(customer.getCompanyId());
                    companyDevice.setStatus(DeviceStatus.REGISTERED);
                    companyDeviceRepository.save(companyDevice);
                    warehouseHistoryService.create(device.getSerialNumber(), companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.REGISTERED);
                }
                //Transfer
                companyDevice.setDeviceId(device.getId());
                companyDevice.setCompanyId(customer.getCompanyId());
                companyDevice.setStatus(DeviceStatus.TRANSFERRED);
                companyDeviceRepository.save(companyDevice);
                warehouseHistoryService.create(device.getSerialNumber(), companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.TRANSFERRED);
                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, "Реестр", customer.getCompany().getName());

                //Register to branch
                BranchDevice branchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(branch.getId(), device.getId());
                if (branchDevice == null) {
                    branchDevice = new BranchDevice();
                    branchDevice.setBranchId(branch.getId());
                    branchDevice.setDeviceId(device.getId());
                    branchDevice.setStatus(DeviceStatus.REGISTERED);
                    branchDeviceRepository.save(branchDevice);
                    warehouseHistoryService.create(device.getSerialNumber(), branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.REGISTERED);
                }
                //Transfer
                branchDevice.setBranchId(branch.getId());
                branchDevice.setDeviceId(device.getId());
                branchDevice.setStatus(DeviceStatus.TRANSFERRED);
                branchDeviceRepository.save(branchDevice);
                warehouseHistoryService.create(device.getSerialNumber(), branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.TRANSFERRED);
                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, customer.getCompany().getName(), branch.getName());
            } else {
                BranchDevice branchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(SecurityUtils.getBranchId(), device.getId());
                branchDevice.setBranchId(branch.getId());
                branchDevice.setDeviceId(device.getId());
                branchDevice.setStatus(DeviceStatus.TRANSFERRED);
                branchDeviceRepository.save(branchDevice);
                warehouseHistoryService.create(device.getSerialNumber(), branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.TRANSFERRED);
                logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, customer.getCompany().getName(), branch.getName());
            }

            //Register to customer
            CustomerDevice customerDevice = new CustomerDevice();
            customerDevice.setCustomerId(customer.getId());
            customerDevice.setDeviceId(device.getId());
            customerDevice.setPaid(!customer.isAllowDeviceRegister());
            customerDeviceRepository.save(customerDevice);
            warehouseHistoryService.create(device.getSerialNumber(), customerDevice.getId(), EntityTypeEnum.CUSTOMER_DEVICE, DeviceStatus.REGISTERED);

            //Transfer from customer
            customerDevice.setStatus(DeviceStatus.TRANSFERRED);
            customerDeviceRepository.save(customerDevice);
            warehouseHistoryService.create(device.getSerialNumber(), customerDevice.getId(), EntityTypeEnum.CUSTOMER_DEVICE, DeviceStatus.TRANSFERRED);
            logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, branch.getName(), customer.getName());

            //Register to customer branch
            CustomerBranchDevice customerBranchDevice = new CustomerBranchDevice();
            customerBranchDevice.setDeviceId(device.getId());
            customerBranchDevice.setStatus(DeviceStatus.REGISTERED);
            customerBranchDevice.setCustomerBranchId(customerBranch.getId());
            customerBranchDevice.setDeviceType(customerDeviceDTO.getDeviceType());

            switch (customerDeviceDTO.getDevicePaymentType()) {
                case LEASING,
                        LEASING_PRIME -> {
                    if (DevicePaymentType.LEASING.equals(customerDeviceDTO.getDevicePaymentType())) {
                        customerBranchDevice.setPaymentType(DevicePaymentType.LEASING);
                    } else {
                        if (!DeviceModelType.P10.name().equals(device.getModel().getCode())) {
                            throw validationService.badRequestExceptionThrow(SELECT_TYPE).get();
                        }
                        customerBranchDevice.setPaymentType(DevicePaymentType.LEASING_PRIME);
                    }
                }
                case RENTAL_CDTI -> {
                    Optional<ContractReport> optionalContractReport = contractReportRepository.findBySerialNumber(device.getSerialNumber());
                    if (optionalContractReport.isPresent() && !ContractReportInvoiceSaleStatus.RENTAL_CDTI.equals(optionalContractReport.get().getSaleStatus())) {
                        throw validationService.badRequestExceptionThrow(REGISTER_DEVICE).get();
                    }
                    if (CustomerAppType.CTS.equals(customer.getAppType())) {
                        throw validationService.badRequestExceptionThrow(CUSTOMER_APP_TYPE).get();
                    }
                    if (!Objects.equals(device.getModel().getCode(), DeviceModelType.P10.name())) {
                        throw validationService.badRequestExceptionThrow(SELECT_TYPE).get();
                    }
                    customerBranchDevice.setPaymentType(DevicePaymentType.RENTAL_CDTI);
                }
                case A_PAY -> {
                    Optional<ContractReport> optionalContractReport = contractReportRepository.findBySerialNumber(device.getSerialNumber());
                    if (optionalContractReport.isPresent() && ContractReportInvoiceSaleStatus.RENTAL_CDTI.equals(optionalContractReport.get().getSaleStatus())) {
                        throw validationService.badRequestExceptionThrow(SELL_DEVICE).get();
                    }
                    if (!Objects.equals(device.getModel().getCode(), DeviceModelType.P10.name())) {
                        throw validationService.badRequestExceptionThrow(SELECT_TYPE).get();
                    }
                    if (customerDeviceDTO.getDeviceType() == null) {
                        throw validationService.badRequestExceptionThrow(SELECT_DEVICE_TYPE, DEVICE_TYPE).get();
                    }
                    customerBranchDevice.setPaymentType(DevicePaymentType.A_PAY);
                }
                default -> customerBranchDevice.setPaymentType(customerDeviceDTO.getDevicePaymentType());
            }

            customerBranchDeviceRepository.save(customerBranchDevice);
            if (DevicePaymentType.A_PAY.equals(customerDeviceDTO.getDevicePaymentType())) {
                BillDTO billDTO = new BillDTO();
                billDTO.setDeviceId(deviceId);
                billDTO.setBranchId(branch.getId());
                billDTO.setCustomerId(customer.getId());
                billDTO.setCustomerBranchId(customerBranch.getId());
                billDTO.setCustomerBranchDeviceId(customerBranchDevice.getId());
                billDTO.setPaymentQuoteId(customerDeviceDTO.getContractId());
                billDTO.setDeviceType(customerDeviceDTO.getDeviceType());
                billId = billService.create(billDTO);
            }

            warehouseHistoryService.create(device.getSerialNumber(), customerBranchDevice.getId(), EntityTypeEnum.CUSTOMER_BRANCH_DEVICE, DeviceStatus.REGISTERED);
            logHistoryService.logHistory(device.getSerialNumber(), false, Action.TRANSFER, customer.getName(), customerBranch.getName());

            /* CUSTOMER BRANCH DEVICE payment status is default NOT_CONNECTED */
            customerBranchDevicePaymentTypeService.create(customerBranchDevice.getId());

            //Update device status
            deviceService.updateStatus(device, DeviceStatus.REGISTERED);
            //Unblock launcher KKM
            launcherService.unlock(Collections.singletonList(device.getSerialNumber()));

            /*CREATE device in CONTRACT REPORT table for this status*/
            if (EnumSet.complementOf(EnumSet.of(DevicePaymentType.FULL_PAYMENT, DevicePaymentType.REREGISTRATION, DevicePaymentType.RENT)).contains(customerDeviceDTO.getDevicePaymentType())) {
                rabbitMqContractReportProducer.createContractReportThroughRegisterKKM(customerDeviceDTO);
            }
        }

        switch (customerDeviceDTO.getDevicePaymentType()) {
            case LEASING,
                    LEASING_PRIME -> {
                ContractForLeasingDeviceRequestDto contractForLeasingDeviceRequestDto = new ContractForLeasingDeviceRequestDto();
                contractForLeasingDeviceRequestDto.setDeviceIds(customerDeviceDTO.getDevices());
                contractLeasingService.addDevice(customerDeviceDTO.getContractId(), contractForLeasingDeviceRequestDto);
            }
            case STS_TRADE_IN ->
                    tradeInContractService.addDevice(customerDeviceDTO.getContractId(), new TradeInDeviceAddRequestDto(customerDeviceDTO.getDevices().getFirst()), true);
            case RENT -> {
                RentActRequestDto rentActRequestDto = new RentActRequestDto();
                rentActRequestDto.setCustomerId(customer.getId());
                rentActRequestDto.setActId(customerDeviceDTO.getContractId());
                rentActRequestDto.setAccessories(customerDeviceDTO.getAccessories());
                rentActService.addDevice(rentActRequestDto);
            }
            case FULL_PAYMENT -> /*payment contract and act*/
                    paymentContractService.generateAct(new PaymentContractActDTO(customerDeviceDTO.getContractId(), customerDeviceDTO.getDevices().getFirst()));
            case RENTAL_CDTI -> /*rent a device act*/
                    rentDeviceActService.create(new RentDeviceActPayload(customer.getId(), customerDeviceDTO.getDevices().getFirst()));
        }

        return billId;
    }

    private String registerSoftwareDevice(CustomerDeviceDTO customerDeviceDTO, Customer customer, CustomerBranch customerBranch, Branch branch) {
        if (customerBranchDeviceRepository.existsByCustomerBranchIdAndDeviceType(customerBranch.getId(), customerDeviceDTO.getDeviceType())) {
            throw validationService.badRequestExceptionThrow(CUSTOMER_BRANCH_DEVICE_TYPE, customerDeviceDTO.getDeviceType().getName()).get();
        }
        DeviceModel deviceModel = deviceModelRepository.findFirstByCode(DeviceModelType.SW.name());
        if (deviceModel == null) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE_MODEL).get();
        }
        String softwareDeviceSerial = RandomUtils.generateVirtualDeviceSerial();
        DeviceDTO deviceDTO = new DeviceDTO();
        deviceDTO.setModelId(deviceModel.getId());
        deviceDTO.setType(DeviceType.VIRTUAL);
        deviceDTO.setSerialNumber(softwareDeviceSerial);
        Long deviceId = deviceService.create(deviceDTO);

        //Register to company
        CompanyDevice companyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(customer.getCompanyId(), deviceId);
        if (companyDevice == null) {
            companyDevice = new CompanyDevice();
            companyDevice.setDeviceId(deviceId);
            companyDevice.setCompanyId(customer.getCompanyId());
            companyDevice.setStatus(DeviceStatus.REGISTERED);
            companyDeviceRepository.save(companyDevice);
            warehouseHistoryService.create(softwareDeviceSerial, companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.REGISTERED);
        }
        //Transfer
        companyDevice.setDeviceId(deviceId);
        companyDevice.setCompanyId(customer.getCompanyId());
        companyDevice.setStatus(DeviceStatus.TRANSFERRED);
        companyDeviceRepository.save(companyDevice);
        warehouseHistoryService.create(softwareDeviceSerial, companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.TRANSFERRED);
        logHistoryService.logHistory(softwareDeviceSerial, false, Action.TRANSFER, "Реестр", customer.getCompany().getName());

        //Register to branch
        BranchDevice branchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(branch.getId(), deviceId);
        if (branchDevice == null) {
            branchDevice = new BranchDevice();
            branchDevice.setBranchId(branch.getId());
            branchDevice.setDeviceId(deviceId);
            branchDevice.setStatus(DeviceStatus.REGISTERED);
            branchDeviceRepository.save(branchDevice);
            warehouseHistoryService.create(softwareDeviceSerial, branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.REGISTERED);
        }
        //Transfer
        branchDevice.setBranchId(branch.getId());
        branchDevice.setDeviceId(deviceId);
        branchDevice.setStatus(DeviceStatus.TRANSFERRED);
        branchDeviceRepository.save(branchDevice);
        warehouseHistoryService.create(softwareDeviceSerial, branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.TRANSFERRED);
        logHistoryService.logHistory(softwareDeviceSerial, false, Action.TRANSFER, customer.getCompany().getName(), branch.getName());

        //Register to customer
        CustomerDevice customerDevice = new CustomerDevice();
        customerDevice.setCustomerId(customer.getId());
        customerDevice.setDeviceId(deviceId);
        customerDevice.setPaid(!customer.isAllowDeviceRegister());
        customerDeviceRepository.save(customerDevice);
        warehouseHistoryService.create(softwareDeviceSerial, customerDevice.getId(), EntityTypeEnum.CUSTOMER_DEVICE, DeviceStatus.REGISTERED);

        //Transfer from customer
        customerDevice.setStatus(DeviceStatus.TRANSFERRED);
        customerDeviceRepository.save(customerDevice);
        warehouseHistoryService.create(softwareDeviceSerial, customerDevice.getId(), EntityTypeEnum.CUSTOMER_DEVICE, DeviceStatus.TRANSFERRED);
        logHistoryService.logHistory(softwareDeviceSerial, false, Action.TRANSFER, branch.getName(), customer.getName());

        //Register to customer branch
        CustomerBranchDevice customerBranchDevice = new CustomerBranchDevice();
        customerBranchDevice.setDeviceId(deviceId);
        customerBranchDevice.setStatus(DeviceStatus.REGISTERED);
        customerBranchDevice.setCustomerBranchId(customerDeviceDTO.getCustomerBranchId());
        customerBranchDevice.setDeviceType(customerDeviceDTO.getDeviceType());
        customerBranchDevice.setPaymentType(DevicePaymentType.SOFTWARE);
        customerBranchDeviceRepository.save(customerBranchDevice);

        warehouseHistoryService.create(softwareDeviceSerial, customerBranchDevice.getId(), EntityTypeEnum.CUSTOMER_BRANCH_DEVICE, DeviceStatus.REGISTERED);
        logHistoryService.logHistory(softwareDeviceSerial, false, Action.TRANSFER, customer.getName(), customerBranch.getName());

        deviceService.updateStatus(deviceRepository.findById(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE)), DeviceStatus.REGISTERED);
        return null;
    }

    @Override
    public CustomerDeviceCheckDTO getPaidDeviceCount(String tin) {
        CustomerDeviceCheckDTO result = new CustomerDeviceCheckDTO();
        result.setRegisteredCount(customerDeviceRepository.totalByCustomerTin(tin));
        result.setPaidCount(customerDeviceRepository.getPaidDeviceCount(tin));
        result.setCanRegisterCount(result.getPaidCount() - result.getRegisteredCount());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SerialNumberDTO> lookUp(BaseFilter filter) {
        ResultList<SerialNumberDTO> resultList = customerDeviceRepository.getLookUp(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerBranchDeviceListDTO> list(BaseFilter filter) {
        List<CustomerBranchDeviceListDTO> result = new ArrayList<>();
        ResultList<CustomerDevice> resultList = customerDeviceRepository.getList(filter);
        resultList.getList().forEach(customerDevice -> {
            CustomerBranchDeviceListDTO branchDeviceListDTO = new CustomerBranchDeviceListDTO();
            branchDeviceListDTO.setId(customerDevice.getId());
            branchDeviceListDTO.setCustomerId(customerDevice.getCustomerId());
            branchDeviceListDTO.setDeviceSerial(customerDevice.getDevice().getSerialNumber());
            branchDeviceListDTO.setDeviceId(customerDevice.getDeviceId());
            branchDeviceListDTO.setTransferable(true);
            BillResponseDTO billResponseDTO = billService.getByDevice(customerDevice.getDeviceId());
            if (billResponseDTO != null) {
                branchDeviceListDTO.setBill(billResponseDTO);
            }
            Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(customerDevice.getCustomerId(), customerDevice.getDeviceId());
            CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findFirstByCustomerBranch_CustomerIdAndDeviceId(customerDevice.getCustomerId(), customerDevice.getDeviceId());
            if (fiscalization != null) {
                branchDeviceListDTO.setTransferable(false);
                branchDeviceListDTO.setFiscalizationDate(fiscalization.getCreatedDate());
                branchDeviceListDTO.setFiscalModuleSerial(fiscalization.getFiscalModule().getSerialNumber());
                branchDeviceListDTO.setFiscalModuleId(fiscalization.getFiscalModuleId());
                branchDeviceListDTO.setCustomerBranch(fiscalization.getCustomerBranch().getDTO());
                branchDeviceListDTO.setFiscalizationId(fiscalization.getId());
                if (fiscalization.getOfdApplicationId() != null) {
                    OfdApplication ofdApplication = ofdApplicationRepository.findById(fiscalization.getOfdApplicationId()).orElse(null);
                    if (ofdApplication != null) {
                        branchDeviceListDTO.setOfdId(ofdApplication.getOfdId());
                        branchDeviceListDTO.setOfdStatus(ofdApplication.getOfdStatus());
                        branchDeviceListDTO.setOfdFileUrl(ofdApplication.getApplicationFileUrl());
                        branchDeviceListDTO.setOfdApplicationType(ofdApplication.getApplicationType());
                    }
                } else {
                    branchDeviceListDTO.setSync(true);
                }
                FileUpload applicationFile = fileUploadRepository.findFirstByDocumentTypeAndEntityTypeAndEntityId(DocumentType.APPLICATION, EntityTypeEnum.FISCALIZATION, fiscalization.getId());
                if (applicationFile != null) {
                    branchDeviceListDTO.setApplicationFileUrl(applicationFile.getDownloadUrl());
                    branchDeviceListDTO.setApplicationFileId(applicationFile.getId());
                }
            }

            if (customerBranchDevice != null) {
                branchDeviceListDTO.setCustomerBranch(customerBranchDevice.getCustomerBranch().getDTO());
                branchDeviceListDTO.setTariffId(customerBranchDevice.getTariffId());
                branchDeviceListDTO.setCustomerBranchDeviceId(customerBranchDevice.getId());
                branchDeviceListDTO.setDeviceStatus(customerBranchDevice.getStatus());
                branchDeviceListDTO.setPaymentType(customerBranchDevice.getPaymentType());
                branchDeviceListDTO.setPaymentStatuses(customerBranchDevice
                        .getPaymentTypes()
                        .stream()
                        .map(CustomerBranchDevicePaymentType::getPaymentType)
                        .collect(Collectors.toSet()));
                branchDeviceListDTO.setDeviceType(customerBranchDevice.getDeviceType());
            }
            result.add(branchDeviceListDTO);
        });

        return new PageImpl<>
                (result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void delete(Long id) {
        CustomerDevice customerDevice = customerDeviceRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
        if (DeviceType.PHYSICAL == customerDevice.getDevice().getType()) {
            Optional<ContractReport> contractOptional =
                    contractReportRepository.findBySerialNumberAndSaleStatusIn(customerDevice.getDevice().getSerialNumber(), List.of(ContractReportInvoiceSaleStatus.SOLD, ContractReportInvoiceSaleStatus.DEVICE_REPLACEMENT, ContractReportInvoiceSaleStatus.DISMANTLING, ContractReportInvoiceSaleStatus.WHOLESALE_TRADE));
            if (contractOptional.isEmpty()) {
                throw validationService.badRequestExceptionThrow(CONTRACT_REPORT_SALE_STATUS).get();
            }
            ActOfTransmission actOfTransmission = actOfTransmissionService.getByDeviceIdAndCustomerId(customerDevice.getDeviceId(), customerDevice.getCustomerId());
            boolean actChecked = false;
            if (actOfTransmission != null) {
                if (ContractStatus.CLOSED.equals(actOfTransmission.getStatus()) || ContractStatus.REJECTED.equals(actOfTransmission.getStatus())) {
                    actChecked = true;
                }
            } else {
                actChecked = true;
            }
            if (!actChecked) {
                throw validationService.badRequestExceptionThrow(ACT_TRANSMISSION_CLOSED, actOfTransmission.getNumber()).get();
            }
            Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndDeviceId(customerDevice.getCustomerId(), customerDevice.getDeviceId());
            if (fiscalization != null) {
                if (fiscalization.getOfdApplication() != null) {
                    if (ApplicationType.REGISTER.equals(fiscalization.getOfdApplication().getApplicationType()) && OfdStatus.REJECT.equals(fiscalization.getOfdApplication().getOfdStatus())) {
                        //todo
                    } else if (!ApplicationType.DEREGISTER.equals(fiscalization.getOfdApplication().getApplicationType())) {
                        throw validationService.badRequestExceptionThrow(FISCALIZATION_DELETE_VALIDATION_MESSAGE, customerDevice.getDevice().getSerialNumber()).get();
                    }
                    if (!(ApplicationType.DEREGISTER.equals(fiscalization.getOfdApplication().getApplicationType()) && OfdStatus.ACCEPT.equals(fiscalization.getOfdApplication().getOfdStatus()))) {
                        throw validationService.badRequestExceptionThrow(FISCALIZATION_DELETE_VALIDATION_MESSAGE, customerDevice.getDevice().getSerialNumber()).get();
                    }
                }

                //Update Fiscalization
                fiscalization.setDeleted(true);
                fiscalization.setStatus(DeviceStatus.DELETED);
                fiscalizationRepository.save(fiscalization);
                cacheService.evictFiscalCache(fiscalization.getHashKey());

                logHistoryService.logHistory(fiscalization.getDevice().getSerialNumber(), false, Action.UPDATE, fiscalization.getStatus().getCode(), DeviceStatus.DELETED.getCode());
                logHistoryService.logHistory(fiscalization.getFiscalModule().getSerialNumber(), true, Action.UPDATE, fiscalization.getStatus().getCode(), DeviceStatus.DELETED.getCode());
            }
        }

        //Delete from a customer branch device
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findFirstByCustomerBranch_CustomerIdAndDeviceId(customerDevice.getCustomerId(), customerDevice.getDeviceId());
        if (customerBranchDevice != null) {
            customerBranchDevice.setDeleted(true);
            customerBranchDevice.setStatus(DeviceStatus.DELETED);
            customerBranchDeviceRepository.save(customerBranchDevice);
            warehouseHistoryService.create(customerDevice.getDevice().getSerialNumber(), customerBranchDevice.getId(), EntityTypeEnum.CUSTOMER_BRANCH_DEVICE, DeviceStatus.DELETED);
            logHistoryService.logHistory(customerBranchDevice.getDevice().getSerialNumber(), false, Action.DELETE, customerBranchDevice.getCustomerBranch().getName(), null);
        }

        //Delete from customer device
        customerDevice.setDeleted(true);
        customerDevice.setStatus(DeviceStatus.DELETED);
        customerDeviceRepository.save(customerDevice);
        warehouseHistoryService.create(customerDevice.getDevice().getSerialNumber(), customerDevice.getId(), EntityTypeEnum.CUSTOMER_DEVICE, DeviceStatus.DELETED);
        logHistoryService.logHistory(customerDevice.getDevice().getSerialNumber(), false, Action.DELETE, customerDevice.getCustomer().getName(), null);

        //Delete from branch device
        branchDeviceService.deleteByDeviceId(customerDevice.getDeviceId());

        //Delete from company device
        companyDeviceService.deleteByDeviceId(customerDevice.getDeviceId());

        if (DeviceType.PHYSICAL == customerDevice.getDevice().getType()) {
            deviceService.updateStatus(customerDevice.getDevice(), DeviceStatus.NOT_REGISTERED);

            //After deregistered device from customer, block the device status on Launcher
            LauncherTerminalRequestDto launcherTerminalRequestDto = new LauncherTerminalRequestDto();
            launcherTerminalRequestDto.setTerminalNumbers(Collections.singletonList(customerDevice.getDevice().getSerialNumber()));
            launcherTerminalRequestDto.setAction(LauncherStatus.BLOCK.name());
            launcherTerminalRequestDto.setMessage(thymeleafService.generateCronBlockTextHTML());
            launcherService.action(launcherTerminalRequestDto);
        } else {
            deviceService.deleteById(customerDevice.getDevice().getId());
        }

        /*remove device from smartpos*/
        smartPosProducer.removeDevice(new SmartPosRemoveDevicePayload(customerDevice.getDevice().getSerialNumber()));
    }

    @Override
    public void deleteByDeviceId(Long deviceId) {
        List<CustomerDevice> customerDevices = customerDeviceRepository.findAllByDeviceId(deviceId);
        if (CollectionUtils.isEmpty(customerDevices)) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
        }

        customerDevices.forEach(customerDevice -> {
            customerDevice.setDeleted(true);
            customerDevice.setStatus(DeviceStatus.DELETED);
            customerDeviceRepository.save(customerDevice);
            warehouseHistoryService.create(customerDevice.getDevice().getSerialNumber(), customerDevice.getId(), EntityTypeEnum.CUSTOMER_DEVICE, DeviceStatus.DELETED);

            logHistoryService.logHistory(customerDevice.getDevice().getSerialNumber(), false, Action.DELETE, customerDevice.getCustomer().getName(), null);
        });
    }

    @Transactional(readOnly = true)
    public Long getListCountToSync() {
        if (isSuperAdmin() && getCompanyId() != null)
            return customerDeviceRepository.countByCompanyId(getCompanyId());
        else
            return customerDeviceRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<SyncCustomerDeviceListDTO> getListToSync(BaseFilter filter) {

        return new PageImpl<>
                (customerDeviceRepository.getListToSync(filter),
                        filter.getOrderedPageable(), 0L);
    }

    @Override
    public Page<CustomerDeviceListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<CustomerDevice> resultList = customerDeviceRepository.getResultList(filter);
        List<CustomerDeviceListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(customerDevice -> {
                    CustomerDeviceListAdminDTO customerDeviceListDTO = new CustomerDeviceListAdminDTO();
                    customerDeviceListDTO.setId(customerDevice.getId());
                    customerDeviceListDTO.setDevice(customerDevice.getDevice().toSerialNumberDTO());
                    customerDeviceListDTO.setStatus(customerDevice.getStatus());
                    customerDeviceListDTO.setCompany(customerDevice.getCustomer().getCompany().toShortDTO());
                    customerDeviceListDTO.setBranch(customerDevice.getCustomer().getBranch().toCommonDTO());
                    customerDeviceListDTO.setCustomer(customerDevice.getCustomer().toCommonDTO());
                    customerDeviceListDTO.setCreatedDate(customerDevice.getCreatedDate());
                    customerDeviceListDTO.setCreator(customerDevice.getCreator().toCommonDTO());
                    return customerDeviceListDTO;
                }).toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public DeviceDetailAdminDTO getForAdmin(Long id) {
        return customerDeviceRepository.findById(id).map(customerDevice -> deviceService.getForAdmin(customerDevice.getDeviceId())).orElseThrow(validationService.entityNotFoundExceptionThrow(DEVICE));
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return customerDeviceRepository.getStatistics(filter);
    }

    @Override
    public List<CustomerDevicesByDeviceTypeDTO> getActiveDeviceCountByTin(String tin, String pinfl) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(StringUtils.isNotEmpty(pinfl) ? pinfl : tin, tin);
        if (customer == null) throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();
        List<CustomerBranchDevice> customerBranchDevices = customerBranchDeviceRepository
                .findAllByCustomerBranch_CustomerIdAndDeviceTypeNotLikeAndDeletedIsFalse(customer.getId(), DeviceType.FISCAL);
        List<CustomerDevicesByDeviceTypeDTO> responseDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(customerBranchDevices)) {
            Map<DeviceType, List<CustomerBranchDevice>> sortingByDeviceTypes = customerBranchDevices
                    .stream()
                    .collect(Collectors.groupingBy(CustomerBranchDevice::getDeviceType));
            sortingByDeviceTypes.forEach((deviceType, customerBranchDeviceList) -> {
                CustomerDevicesByDeviceTypeDTO responseDTO = new CustomerDevicesByDeviceTypeDTO();
                responseDTO.setTin(tin);
                responseDTO.setCount(customerBranchDeviceList.size());
                responseDTO.setDeviceType(deviceType.getCode());
                responseDTOs.add(responseDTO);
            });
        }
        CustomerDevicesByDeviceTypeDTO responseDTO = new CustomerDevicesByDeviceTypeDTO();
        responseDTO.setTin(tin);
        responseDTO.setCount(fiscalizationRepository.getActiveFiscalizationCount(customer.getId()));
        responseDTO.setDeviceType(DeviceType.FISCAL.getCode());
        responseDTOs.add(responseDTO);
        return responseDTOs;
    }
}
