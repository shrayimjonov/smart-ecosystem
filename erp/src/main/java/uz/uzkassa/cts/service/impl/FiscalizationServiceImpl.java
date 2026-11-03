package uz.uzkassa.cts.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.applicationfile.ApplicationTemplateDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.caseAgreement.CaseAgreementDTO;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;
import uz.uzkassa.cts.dto.device.DeviceFiscalModuleDTO;
import uz.uzkassa.cts.dto.device.admin.DeviceStatisticsAdminDTO;
import uz.uzkassa.cts.dto.fiscalization.FiscalizationListAdminDTO;
import uz.uzkassa.cts.dto.fm.*;
import uz.uzkassa.cts.dto.ofdapplication.OfdApplicationMergeDTO;
import uz.uzkassa.cts.dto.smartpos.SmartposFiscalizationListDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.enums.apay.APayStatus;
import uz.uzkassa.cts.enums.terminal.TerminalOfdStatus;
import uz.uzkassa.cts.enums.terminal.TerminalStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.AddressMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Transactional
@Service
@Slf4j
public class FiscalizationServiceImpl extends BaseServiceImpl<Fiscalization, Long> implements FiscalizationService {

    private final FiscalizationRepository fiscalizationRepository;
    private final CustomerFiscalModuleRepository customerFiscalModuleRepository;
    private final CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerDeviceRepository customerDeviceRepository;
    private final BranchDeviceRepository branchDeviceRepository;
    private final CompanyDeviceRepository companyDeviceRepository;
    private final DeviceRepository deviceRepository;
    private final FiscalModuleRepository fiscalModuleRepository;
    private final CustomerDeviceService customerDeviceService;
    private final BranchDeviceService branchDeviceService;
    private final CompanyDeviceService companyDeviceService;
    private final DeviceService deviceService;
    private final CustomerBranchFiscalModuleService customerBranchFiscalModuleService;
    private final CustomerFiscalModuleService customerFiscalModuleService;
    private final FiscalModuleService fiscalModuleService;
    private final WarehouseHistoryService warehouseHistoryService;
    private final RabbitMqSmartPosProducer rabbitMqSmartPosProducer;
    private final ServicePaymentHistoryRepository servicePaymentHistoryRepository;
    private final OfdApplicationService ofdApplicationService;
    private final ThymeleafService thymeleafService;
    private final ActOfTransmissionFactoryService actOfTransmissionService;
    private final OfdApplicationRepository ofdApplicationRepository;
    private final TradeInActRepository tradeInActRepository;
    private final TradeInInvoiceRepository tradeInInvoiceRepository;
    private final CustomerOldService customerOldService;
    private final RentActOfReturnRepository rentActOfReturnRepository;
    private final BillRepository billRepository;
    private final CustomerRequestService customerRequestService;
    private final AddressMapper addressMapper;
    private final PaymentActService paymentActService;
    private final CaseAgreementService caseAgreementService;
    private final FiscalizationSyncService fiscalizationSyncService;
    private final RelationRepository relationRepository;
    private final CacheService cacheService;
    private final LogHistoryService logHistoryService;

    protected static final ConcurrentHashMap<String, Fiscalization> fiscalizationsMap = new ConcurrentHashMap<>();


    public FiscalizationServiceImpl(FiscalizationRepository fiscalizationRepository,
                                    CustomerFiscalModuleRepository customerFiscalModuleRepository,
                                    CustomerBranchFiscalModuleRepository customerBranchFiscalModuleRepository,
                                    CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                    DeviceRepository deviceRepository, FiscalModuleRepository fiscalModuleRepository,
                                    CustomerDeviceService customerDeviceService, BranchDeviceService branchDeviceService,
                                    CompanyDeviceService companyDeviceService, DeviceService deviceService,
                                    CustomerBranchFiscalModuleService customerBranchFiscalModuleService,
                                    CustomerFiscalModuleService customerFiscalModuleService,
                                    FiscalModuleService fiscalModuleService, WarehouseHistoryService warehouseHistoryService,
                                    RabbitMqSmartPosProducer rabbitMqSmartPosProducer, CustomerDeviceRepository customerDeviceRepository,
                                    CompanyDeviceRepository companyDeviceRepository, BranchDeviceRepository branchDeviceRepository,
                                    ServicePaymentHistoryRepository servicePaymentHistoryRepository, OfdApplicationService ofdApplicationService,
                                    ThymeleafService thymeleafService, ActOfTransmissionFactoryService actOfTransmissionService,
                                    OfdApplicationRepository ofdApplicationRepository, TradeInActRepository tradeInActRepository,
                                    TradeInInvoiceRepository tradeInInvoiceRepository,
                                    CustomerOldService customerOldService, RentActOfReturnRepository rentActOfReturnRepository,
                                    BillRepository billRepository, CustomerRequestService customerRequestService, AddressMapper addressMapper,
                                    PaymentActService paymentActService, CaseAgreementService caseAgreementService, FiscalizationSyncService fiscalizationSyncService, RelationRepository relationRepository, CacheService cacheService, LogHistoryService logHistoryService) {
        super(fiscalizationRepository);
        this.fiscalizationRepository = fiscalizationRepository;
        this.customerFiscalModuleRepository = customerFiscalModuleRepository;
        this.customerBranchFiscalModuleRepository = customerBranchFiscalModuleRepository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.deviceRepository = deviceRepository;
        this.fiscalModuleRepository = fiscalModuleRepository;
        this.customerDeviceService = customerDeviceService;
        this.branchDeviceService = branchDeviceService;
        this.companyDeviceService = companyDeviceService;
        this.deviceService = deviceService;
        this.customerBranchFiscalModuleService = customerBranchFiscalModuleService;
        this.customerFiscalModuleService = customerFiscalModuleService;
        this.fiscalModuleService = fiscalModuleService;
        this.warehouseHistoryService = warehouseHistoryService;
        this.rabbitMqSmartPosProducer = rabbitMqSmartPosProducer;
        this.customerDeviceRepository = customerDeviceRepository;
        this.companyDeviceRepository = companyDeviceRepository;
        this.branchDeviceRepository = branchDeviceRepository;
        this.servicePaymentHistoryRepository = servicePaymentHistoryRepository;
        this.ofdApplicationService = ofdApplicationService;
        this.thymeleafService = thymeleafService;
        this.actOfTransmissionService = actOfTransmissionService;
        this.ofdApplicationRepository = ofdApplicationRepository;
        this.tradeInActRepository = tradeInActRepository;
        this.tradeInInvoiceRepository = tradeInInvoiceRepository;
        this.customerOldService = customerOldService;
        this.rentActOfReturnRepository = rentActOfReturnRepository;
        this.billRepository = billRepository;
        this.customerRequestService = customerRequestService;
        this.addressMapper = addressMapper;
        this.paymentActService = paymentActService;
        this.caseAgreementService = caseAgreementService;
        this.fiscalizationSyncService = fiscalizationSyncService;
        this.relationRepository = relationRepository;
        this.cacheService = cacheService;
        this.logHistoryService = logHistoryService;
    }

    @Override
    public Page<FiscalizationListAdminDTO> getListForAdmin(BaseFilter filter) {
        ResultList<Fiscalization> resultList = fiscalizationRepository.getResultList(filter);
        List<FiscalizationListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(fiscalization -> {
                    FiscalizationListAdminDTO fiscalizationListDTO = new FiscalizationListAdminDTO();
                    fiscalizationListDTO.setId(fiscalization.getId());
                    fiscalizationListDTO.setDevice(fiscalization.getDevice().toSerialNumberDTO());
                    fiscalizationListDTO.setFiscalModule(fiscalization.getFiscalModule().toSerialNumberDTO());
                    fiscalizationListDTO.setCompany(fiscalization.getBranch().getCompany().toShortDTO());
                    fiscalizationListDTO.setBranch(fiscalization.getBranch().toCommonDTO());
                    fiscalizationListDTO.setCustomer(fiscalization.getCustomer().toShortDTO());
                    fiscalizationListDTO.setCustomerBranch(fiscalization.getCustomerBranch().toCommonDTO());
                    fiscalizationListDTO.setStatus(fiscalization.getStatus());
                    fiscalizationListDTO.setCreatedDate(fiscalization.getCreatedDate());
                    fiscalizationListDTO.setCreator(fiscalization.getCreator().toCommonDTO());
                    return fiscalizationListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<DeviceStatisticsAdminDTO> getStatisticsForAdmin(BaseFilter filter) {
        return fiscalizationRepository.getStatisticsForAdmin(filter);
    }

    @Override
    public Long fiscalization(FiscalizationDTO fiscalizationDTO) {
        Device device = deviceRepository.findByModelIdAndSerialNumber(fiscalizationDTO.getDeviceModelId(), fiscalizationDTO.getDeviceSerial());
        if (device == null) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
        }
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findFirstByCustomerBranchIdAndDeviceId(fiscalizationDTO.getCustomerBranchId(), device.getId());
        if (DeviceType.TERMINAL_FISCAL.equals(customerBranchDevice.getDeviceType())) {
            throw validationService.badRequestExceptionThrow(FISCALIZATION_TERMINAL).get();
        }
        if (DevicePaymentType.A_PAY.equals(customerBranchDevice.getPaymentType())) {
            Optional<Bill> optionalBill = billRepository.findByCustomerIdAndDeviceId(fiscalizationDTO.getCustomerId(), device.getId());
            if (optionalBill.isPresent() && !APayStatus.PAID.equals(optionalBill.get().getPayStatus())) {
                throw validationService.badRequestExceptionThrow(FISCALIZATION_ERROR).get();
            }
        }
//        if (CustomerAppType.SMARTPOS.equals(customerBranchDevice.getCustomerBranch().getCustomer().getAppType()) && customerBranchDevice.getTariffId() == null) {
//            throw validationService.badRequestExceptionThrow("error.fiscalization.type.app.type").get();
//        }

        ActOfTransmission actOfTransmission = actOfTransmissionService.getByDeviceIdAndCustomerId(device.getId(), fiscalizationDTO.getCustomerId());
        if (actOfTransmission != null) {
            if (!(ContractStatus.ACCEPTED.equals(actOfTransmission.getStatus()) || ContractStatus.CLOSED.equals(actOfTransmission.getStatus()))) {
                throw validationService.badRequestExceptionThrow(ACT_TRANSMISSION_APPROVE, actOfTransmission.getNumber()).get();
            }
        }

        PaymentAct paymentAct = paymentActService.getByDeviceIdAndCustomerId(device.getId(), fiscalizationDTO.getCustomerId());
        if (paymentAct != null) {
            if (!(ContractStatus.ACCEPTED.equals(paymentAct.getStatus()) || ContractStatus.CLOSED.equals(paymentAct.getStatus()))) {
                throw validationService.badRequestExceptionThrow(PAYMENT_ACT, paymentAct.getNumber()).get();
            }
        }

        TradeInAct tradeInAct = tradeInActRepository.findFirstByDeviceIdAndContract_CustomerIdAndStatus(device.getId(), fiscalizationDTO.getCustomerId(), ContractStatus.ACCEPTED).orElse(null);
        if (tradeInAct != null) {
            if (!(ContractStatus.ACCEPTED.equals(tradeInAct.getStatus()) || ContractStatus.CLOSED.equals(tradeInAct.getStatus()))) {
                throw validationService.badRequestExceptionThrow(ACT_APPROVE, tradeInAct.getNumber()).get();
            }
            TradeInInvoice tradeInInvoice = tradeInInvoiceRepository.findByTradeInAct_IdAndContract_Id(tradeInAct.getId(), tradeInAct.getContract().getId()).orElse(null);
            if (tradeInInvoice != null) {
                if (!XfileStatus.ACCEPTED.equals(tradeInInvoice.getXfileStatus())) {
                    throw validationService.badRequestExceptionThrow(INVOICE_APPROVE, tradeInInvoice.getNumber()).get();
                }
            }
        }


        FiscalModule fiscalModule = fiscalModuleRepository.findFirstBySerialNumber(fiscalizationDTO.getFiscalModuleSerial());
        if (fiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }

        Fiscalization fiscalization = fiscalizationRepository.findFirstByDeviceIdAndFiscalModuleId(device.getId(), fiscalModule.getId());
        if (fiscalization != null) {
            throw validationService.badRequestExceptionThrow(DEVICE_AND_FISCAL_ALREADY_REGISTERED, device.getSerialNumber(), fiscalModule.getSerialNumber()).get();
        }

        fiscalModule.setStatus(DeviceStatus.REGISTERED);
        fiscalModuleRepository.save(fiscalModule);

        fiscalization = new Fiscalization();
        fiscalization.setDevice(device);
        fiscalization.setDeviceId(device.getId());
        fiscalization.setFiscalModule(fiscalModule);
        fiscalization.setFiscalModuleId(fiscalModule.getId());
        fiscalization.setBranchId(SecurityUtils.getBranchId());
        fiscalization.setCustomerId(fiscalizationDTO.getCustomerId());
        fiscalization.setCustomerBranchId(fiscalizationDTO.getCustomerBranchId());

        fiscalization.setHashKey(SecurityUtils.fiscalHashSha(fiscalizationDTO.getDeviceSerial(), fiscalizationDTO.getFiscalModuleSerial()));
        fiscalization.setStatus(DeviceStatus.FISCAL);
        save(fiscalization);

        logHistoryService.logHistory(device.getSerialNumber(), false, Action.UPDATE, null, null, device.getStatus().getCode(), DeviceStatus.FISCAL.getCode());
        logHistoryService.logHistory(device.getSerialNumber(), true, Action.UPDATE, null, null, fiscalModule.getStatus().getCode(), DeviceStatus.FISCAL.getCode());

        //Update Customer Fiscal Module status
        CustomerFiscalModule customerFiscalModule = customerFiscalModuleRepository.findByCustomerIdAndFiscalModuleId(fiscalizationDTO.getCustomerId(), fiscalModule.getId());
        if (customerFiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }
        customerFiscalModule.setStatus(DeviceStatus.FISCAL);
        customerFiscalModuleRepository.save(customerFiscalModule);

        //Update Customer Branch (TT) FM status
        CustomerBranchFiscalModule customerBranchFiscalModule = customerBranchFiscalModuleRepository.findFirstByCustomerBranchIdAndFiscalModuleId(fiscalizationDTO.getCustomerBranchId(), fiscalModule.getId());
        if (customerBranchFiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }
        customerBranchFiscalModule.setStatus(DeviceStatus.FISCAL);
        customerBranchFiscalModuleRepository.save(customerBranchFiscalModule);

        Customer customer = customerOldService.findById(fiscalization.getCustomerId());

        /*
         * CREATE
         * IN SMARTPOS
         * */
        rabbitMqSmartPosProducer.updateCustomer(fiscalization.getId());

        /*
         * CREATE
         * IN SUPPLY
         * */
        Optional<Relation> relationOptional = relationRepository
                .findFirstByFromIdAndFromTypeAndEntityType(customer.getId(), RelationType.SUPPLY, customer.getEntityType());
        if (relationOptional.isPresent()) {
            fiscalizationSyncService.syncFiscal(fiscalization.getId(), RelationType.SUPPLY);
        }

        if (CustomerAppType.SMARTPOS != customer.getAppType()) {
            if (!DevicePaymentType.RENT.equals(customerBranchDevice.getPaymentType())) {
                Tariff tariff = customerBranchDevice.getTariff();
                if (tariff == null) {
                    throw validationService.entityNotFoundExceptionThrow(TARIFF).get();
                }
                // add start date and due date for begin calculation
                customerBranchDevice.setDueDate(calculateDueDate(tariff.getFreeTrialPeriodType(), tariff.getFreeTrialPeriod()));
                LocalDate currentTime = LocalDate.now();
                customerBranchDevice.setStartDate(currentTime);
                customerBranchDevice.setInitialDate(currentTime);
                customerBranchDevice.setFreeTrailPeriod(true);
                customerBranchDeviceRepository.save(customerBranchDevice);
                cacheService.evictFiscalCache(fiscalization.getHashKey());
            }
        }

        /*
         * CREATE
         * IN CUSTOMER REQUEST TO SHOW CALL CENTER
         * */
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setCustomerId(customer.getId());
        customerRequestDTO.setRequestType(CustomerRequestType.NEW_BRANCH);
        customerRequestService.create(customerRequestDTO);

        /*
         * START
         * CREATE AGREEMENT
         * IN SMART_POS BILLING
         * */
        if (!customer.isWhiteList()) {
            CaseAgreementDTO caseAgreementDTO = new CaseAgreementDTO();
            caseAgreementDTO.setTin(customer.getTin());
            caseAgreementDTO.setEntityId(customerBranchDevice.getEntityId());
            caseAgreementDTO.setEntityType(customerBranchDevice.getEntityType());
            caseAgreementDTO.setType(CaseXizmatType.REGISTER_FISCAL);
            caseAgreementService.createAgreement(caseAgreementDTO);
//            caseAgreementService.acceptAgreement(customerBranchDevice.getEntityId(), customerBranchDevice.getEntityType());
        }
        /*
         * END
         * CREATE AGREEMENT
         * IN SMART_POS BILLING
         * */

        return fiscalization.getId();
    }

    @Override
    public Page<SmartposFiscalizationListDTO> list(BaseFilter filter) {
        ResultList<SmartposFiscalizationListDTO> resultList = fiscalizationRepository.getResultListDTO(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    private LocalDate calculateDueDate(FreeTrialPeriodType type, Integer period) {
        return switch (type) {
            case DAILY -> LocalDate.now().plusDays(period);
            case WEEKLY -> LocalDate.now().plusWeeks(period);
            case MONTHLY -> LocalDate.now().plusMonths(period);
            case YEARLY -> LocalDate.now().plusYears(period);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public FiscalizationDetailDTO getDetail(FiscalCheckDTO fiscalCheckDTO) {

        if (StringUtils.isEmpty(fiscalCheckDTO.getDeviceSerial()) || StringUtils.isEmpty(fiscalCheckDTO.getFiscalModuleSerial()) || StringUtils.isEmpty(fiscalCheckDTO.getHash())) {
            try {
                log.info(new ObjectMapper().writeValueAsString(fiscalCheckDTO));
            } catch (
                    JsonProcessingException e) {
                log.error(e.getMessage());
                throw validationService.badRequestExceptionThrow(UNEXPECTED_ERROR, e.getMessage()).get();
            }
        }
        Device device = deviceRepository.findBySerialNumber(fiscalCheckDTO.getDeviceSerial().trim());
        if (device == null) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
        }

        FiscalModule fiscalModule = fiscalModuleRepository.findBySerialNumber(fiscalCheckDTO.getFiscalModuleSerial().trim());
        if (fiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE).get();
        }

        Fiscalization fiscalization = fiscalizationRepository.findFirstByDeviceIdAndFiscalModuleId(device.getId(), fiscalModule.getId());
        if (fiscalization == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCALIZATION).get();
        }

        FiscalizationDetailDTO result = new FiscalizationDetailDTO();
        FiscalCustomerDTO customer = new FiscalCustomerDTO();
        customer.setId(fiscalization.getCustomerId());
        customer.setName(fiscalization.getCustomer().getCompanyName());
        customer.setTin(fiscalization.getCustomer().getTin());
        customer.setBusinessType(fiscalization.getCustomer().getBusinessType());
        customer.setCreatedDate(fiscalization.getCustomer().getCreatedDate());
        result.setCustomer(customer);

        FiscalCustomerBranchDTO customerBranch = new FiscalCustomerBranchDTO();
        customerBranch.setId(fiscalization.getCustomerBranchId());
        customerBranch.setName(fiscalization.getCustomerBranch().getName());
        customerBranch.setCreatedDate(fiscalization.getCustomerBranch().getCreatedDate());
        customerBranch.setAddress(addressMapper.toDetailDTO(fiscalization.getCustomerBranch().getAddress()));

        result.setCustomerBranch(customerBranch);

        return result;
    }

    @Override
    public void clearMapByHash(String hash) {
        cacheService.evictFiscalCache(hash);
    }

    @Override
    public FiscalizationCheckDeviceDTO fiscalizationCheck(FiscalCheckDTO fiscalCheckDTO) {

        if (StringUtils.isEmpty(fiscalCheckDTO.getDeviceSerial()) || StringUtils.isEmpty(fiscalCheckDTO.getFiscalModuleSerial()) || StringUtils.isEmpty(fiscalCheckDTO.getHash())) {
            throw validationService.badRequestExceptionThrow(DEVICE_AND_FISCAL_MODULE, fiscalCheckDTO.getDeviceSerial(), fiscalCheckDTO.getFiscalModuleSerial()).get();
        }

        Fiscalization fiscalization = fiscalizationsMap.get(fiscalCheckDTO.getHash());
        if (fiscalization == null) {
            fiscalization = fiscalizationRepository.findFirstByHashKeyOrderByIdDesc(fiscalCheckDTO.getHash());
        }
        if (fiscalization == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, fiscalCheckDTO.getFiscalModuleSerial().trim()).get();
        }

        if (!fiscalization.getDevice().getSerialNumber().equals(fiscalCheckDTO.getDeviceSerial())) {
            throw validationService.badRequestExceptionThrow(INCORRECT_INPUT, fiscalCheckDTO.getDeviceSerial()).get();
        }
        if (!fiscalization.getFiscalModule().getSerialNumber().equals(fiscalCheckDTO.getFiscalModuleSerial())) {
            throw validationService.badRequestExceptionThrow(INCORRECT_INPUT, fiscalCheckDTO.getFiscalModuleSerial()).get();
        }

        fiscalizationsMap.put(fiscalCheckDTO.getHash(), fiscalization);

        FiscalizationCheckDeviceDTO dto = new FiscalizationCheckDeviceDTO();
        DeviceStatus oldStatus = fiscalization.getStatus();
        dto.setStatus(TerminalStatus.get(fiscalization.getStatus().getCode()));
        LocalDateTime localDateTime = fiscalization.getCreatedDate().plus(999999999, ChronoField.MILLI_OF_SECOND.getBaseUnit());
        if (localDateTime.getNano() == 0) {
            localDateTime = localDateTime.plus(999999888, ChronoField.MILLI_OF_SECOND.getBaseUnit());
        }
        dto.setFiscalizationDate(localDateTime);

        if (!DeviceStatus.FISCAL_ACTIVE.equals(fiscalization.getStatus())) {
            fiscalization.setStatus(DeviceStatus.FISCAL_ACTIVE);
            fiscalizationRepository.save(fiscalization);
            cacheService.evictFiscalCache(fiscalCheckDTO.getHash());
        }

        RentActOfReturn rentActOfReturn = rentActOfReturnRepository.findByCustomerIdAndDeviceSerialNumber(fiscalization.getCustomerId(), fiscalization.getDevice().getSerialNumber());
        if (rentActOfReturn != null && !ContractStatus.ACCEPTED.equals(rentActOfReturn.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        if (fiscalization.getOfdApplicationId() != null) {
            OfdApplication ofdApplication = ofdApplicationRepository.findById(fiscalization.getOfdApplicationId()).orElse(null);
            if (ofdApplication == null) {
                return dto;
            }
            if (OfdStatus.ACCEPT.equals(ofdApplication.getOfdStatus()) || (OfdStatus.REJECT.equals(ofdApplication.getOfdStatus()) && ApplicationType.DEREGISTER.equals(ofdApplication.getApplicationType()))
                //Bu degani ro'yxatdan chiqarishga ariza yuborilgan, lekin NIC rad etgan. Ro'yxatdan chiqarmagan. Kassa hali faol.
            ) {
                if (!fiscalization.isSyncedSmartpos()) {
                    rabbitMqSmartPosProducer.updateCustomer(fiscalization.getId());
                    if (!oldStatus.equals(fiscalization.getStatus())) {
                        logHistoryService.logHistory(fiscalCheckDTO.getDeviceSerial().trim(), false, Action.UPDATE, null, null, oldStatus.getCode(), DeviceStatus.FISCAL_ACTIVE.getCode());
                        logHistoryService.logHistory(fiscalCheckDTO.getFiscalModuleSerial().trim(), true, Action.UPDATE, null, null, oldStatus.getCode(), DeviceStatus.FISCAL_ACTIVE.getCode());
                    }
                }
            }

            dto.setStatus(TerminalStatus.get(fiscalization.getStatus().getCode()));
            if (ofdApplication.getOfdStatus() != null) {
                dto.setOfdStatus(TerminalOfdStatus.get(ofdApplication.getOfdStatus().getCode()));
            }
            if (OfdStatus.ACCEPT.equals(ofdApplication.getOfdStatus()) || (OfdStatus.REJECT.equals(ofdApplication.getOfdStatus()) && ApplicationType.DEREGISTER.equals(ofdApplication.getApplicationType()))) {
                dto.setOfdStatus(TerminalOfdStatus.ACTIVE);
            }
            dto.setBranchId(fiscalization.getBranchId());
            dto.setCustomerBranchId(fiscalization.getCustomerBranchId());
            dto.setCustomerId(fiscalization.getCustomerId());
            dto.setSendGeolocation(fiscalization.getCustomer().isSendGeolocation());
            dto.setDeviceSerial(fiscalCheckDTO.getDeviceSerial().trim());
            dto.setFiscalModuleSerial(fiscalCheckDTO.getFiscalModuleSerial().trim());
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeviceFiscalModuleDTO> getLookupForOfd(BaseFilter filter) {
        return fiscalizationRepository.getLookupForOfd(filter);
    }

    @Override
    @Transactional(readOnly = true)
    public ArrayList<BigInteger> getListToSendOfd() {
        return fiscalizationRepository.getListToSendOfd();
    }

    @Override
    public void swapDevice(SwapDeviceDTO swapDeviceDTO) {

        Fiscalization fiscalization = fiscalizationRepository.findById(swapDeviceDTO.getFiscalizationId()).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));

        if (!(fiscalization.getOfdApplication() == null || ApplicationType.DEREGISTER_DEVICE.equals(fiscalization.getOfdApplication().getApplicationType()))) {
            throw validationService.badRequestExceptionThrow(SWAP_MESSAGE, swapDeviceDTO.getDeviceSerial()).get();
        }

        if (StringUtils.isNotEmpty(swapDeviceDTO.getOtherReason())) {
            fiscalization.setOtherReason(swapDeviceDTO.getOtherReason());
        } else if (swapDeviceDTO.getReason() != null) {
            Reason reasonEnum = Reason.get(swapDeviceDTO.getReason());
            if (reasonEnum == null) {
                throw validationService.badRequestExceptionThrow(INVALID_INPUT, swapDeviceDTO.getReason()).get();
            }
            fiscalization.setReason(reasonEnum);
        } else {
            throw validationService.requiredExceptionThrow(REASON).get();
        }

        if (fiscalization.getDevice().getSerialNumber().equals(swapDeviceDTO.getDeviceSerial())) {
            throw validationService.entityAlreadyExistExceptionThrow(DEVICE, SERIAL_NUMBER, swapDeviceDTO.getDeviceSerial()).get();
        }

        Device newDevice = deviceRepository.findFirstBySerialNumber(swapDeviceDTO.getDeviceSerial());
        if (newDevice == null) {
            throw validationService.entityNotFoundExceptionThrow(swapDeviceDTO.getDeviceSerial()).get();
        }

        Fiscalization fiscalizationNew = fiscalizationRepository.findFirstByDeviceIdAndFiscalModuleId(newDevice.getId(), fiscalization.getFiscalModuleId());
        if (fiscalizationNew != null) {
            throw validationService.badRequestExceptionThrow(DEVICE_AND_FISCAL_ALREADY_REGISTERED, newDevice.getSerialNumber(), fiscalization.getFiscalModule().getSerialNumber()).get();
        }

        //Remove old device from customer device
        customerDeviceService.deleteByDeviceId(fiscalization.getDeviceId());

        //Remove old from branch device
        branchDeviceService.deleteByDeviceId(fiscalization.getDeviceId());

        //Remove from company device
        companyDeviceService.deleteByDeviceId(fiscalization.getDeviceId());

        //Update device status
        deviceService.updateStatus(fiscalization.getDevice(), DeviceStatus.getByReason(Reason.get(swapDeviceDTO.getReason())));

        //----------------------------- REGISTER NEW  DEVICE -------------------------------------//
        //Add or update new device to company
        CompanyDevice companyDevice = companyDeviceRepository.findFirstByCompanyIdAndDeviceId(fiscalization.getBranch().getCompanyId(), newDevice.getId());
        companyDevice = Optional.ofNullable(companyDevice).orElse(new CompanyDevice());
        if (companyDevice.getId() == null) {
            companyDevice.setCompanyId(fiscalization.getBranch().getCompanyId());
            companyDevice.setCompany(fiscalization.getBranch().getCompany());
            companyDevice.setDeviceId(newDevice.getId());
            companyDeviceRepository.save(companyDevice);
            warehouseHistoryService.create(newDevice.getSerialNumber(), companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.REGISTERED);
        }
        companyDevice.setStatus(DeviceStatus.TRANSFERRED);
        companyDeviceRepository.save(companyDevice);
        warehouseHistoryService.create(newDevice.getSerialNumber(), companyDevice.getId(), EntityTypeEnum.COMPANY_DEVICE, DeviceStatus.TRANSFERRED);
        logHistoryService.logHistory(newDevice.getSerialNumber(), false, Action.TRANSFER, "Реестр", companyDevice.getCompany().getName());

        //Add or update new device to branch
        BranchDevice branchDevice = branchDeviceRepository.findFirstByBranchIdAndDeviceId(fiscalization.getBranchId(), newDevice.getId());
        branchDevice = Optional.ofNullable(branchDevice).orElse(new BranchDevice());
        if (branchDevice.getId() == null) {
            branchDevice.setBranchId(fiscalization.getBranchId());
            branchDevice.setBranch(fiscalization.getBranch());
            branchDevice.setDeviceId(newDevice.getId());
            branchDeviceRepository.save(branchDevice);
            warehouseHistoryService.create(newDevice.getSerialNumber(), branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.REGISTERED);
        }
        branchDevice.setStatus(DeviceStatus.TRANSFERRED);
        branchDeviceRepository.save(branchDevice);
        warehouseHistoryService.create(newDevice.getSerialNumber(), branchDevice.getId(), EntityTypeEnum.BRANCH_DEVICE, DeviceStatus.TRANSFERRED);
        logHistoryService.logHistory(newDevice.getSerialNumber(), false, Action.TRANSFER, companyDevice.getCompany().getName(), branchDevice.getBranch().getName());

        //Add or update new device to customer device
        CustomerDevice customerDevice = customerDeviceRepository.findFirstByCustomerIdAndDeviceId(fiscalization.getCustomerId(), newDevice.getId());
        customerDevice = Optional.ofNullable(customerDevice).orElse(new CustomerDevice());
        if (customerDevice.getId() == null) {
            customerDevice.setCustomerId(fiscalization.getCustomerId());
            customerDevice.setCustomer(fiscalization.getCustomer());
            customerDevice.setDeviceId(newDevice.getId());
            customerDeviceRepository.save(customerDevice);
            warehouseHistoryService.create(newDevice.getSerialNumber(), customerDevice.getId(), EntityTypeEnum.CUSTOMER_DEVICE, DeviceStatus.REGISTERED);
        }
        customerDevice.setStatus(DeviceStatus.TRANSFERRED);
        customerDeviceRepository.save(customerDevice);
        warehouseHistoryService.create(newDevice.getSerialNumber(), customerDevice.getId(), EntityTypeEnum.CUSTOMER_DEVICE, DeviceStatus.TRANSFERRED);

        logHistoryService.logHistory(newDevice.getSerialNumber(), false, Action.TRANSFER, branchDevice.getBranch().getName(), customerDevice.getCustomer().getName());


        //If there is customer branch device with new device, delete it. Because, while replacing old device to new device, new customer branch device should not be created
        CustomerBranchDevice customerBranchDeviceNew = customerBranchDeviceRepository.findFirstByCustomerBranchIdAndDeviceId(fiscalization.getCustomerBranchId(), newDevice.getId());
        if (customerBranchDeviceNew != null) {
            customerBranchDeviceNew.setDeleted(true);
            customerBranchDeviceNew.setStatus(DeviceStatus.DELETED);
            customerBranchDeviceRepository.save(customerBranchDeviceNew);
            warehouseHistoryService.create(newDevice.getSerialNumber(), customerBranchDeviceNew.getId(), EntityTypeEnum.CUSTOMER_BRANCH_DEVICE, DeviceStatus.DELETED);

            logHistoryService.logHistory(newDevice.getSerialNumber(), false, Action.DELETE, customerBranchDeviceNew.getCustomerBranch().getName(), null);
        }

        //Update old device from customer branch device
        //Note customer branch device should not be deleted, just update old device to a new one
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findFirstByCustomerBranchIdAndDeviceId(fiscalization.getCustomerBranchId(), fiscalization.getDeviceId());
        customerBranchDevice.setDeviceId(newDevice.getId());
        customerBranchDeviceRepository.save(customerBranchDevice);
        warehouseHistoryService.create(newDevice.getSerialNumber(), customerBranchDevice.getId(), EntityTypeEnum.CUSTOMER_BRANCH_DEVICE, DeviceStatus.REGISTERED);

        logHistoryService.logHistory(newDevice.getSerialNumber(), false, Action.TRANSFER, customerBranchDevice.getCustomerBranch().getCustomer().getName(), customerBranchDevice.getCustomerBranch().getName());

        //Update service payment history
        ServicePaymentHistory servicePaymentHistory = servicePaymentHistoryRepository.findFirstByCustomerBranchDeviceIdAndMonthAndYear(customerBranchDevice.getId(), LocalDate.now().getMonth(), LocalDate.now().getYear());
        if (servicePaymentHistory != null) {
            servicePaymentHistory.setDeviceSerialNumber(newDevice.getSerialNumber());
            servicePaymentHistoryRepository.save(servicePaymentHistory);
        }
        //Update New Device status
        newDevice.setStatus(DeviceStatus.REGISTERED);
        deviceRepository.save(newDevice);
        warehouseHistoryService.create(newDevice.getSerialNumber(), newDevice.getId(), EntityTypeEnum.DEVICE, DeviceStatus.REGISTERED);

        logHistoryService.logHistory(newDevice.getSerialNumber(), false, Action.UPDATE, null, null, DeviceStatus.NOT_REGISTERED.getCode(), DeviceStatus.REGISTERED.getCode());

        //Set new device
        fiscalization.setDeviceId(newDevice.getId());
        fiscalization.setHashKey(SecurityUtils.fiscalHashSha(newDevice.getSerialNumber(), fiscalization.getFiscalModule().getSerialNumber()));
        fiscalization.setStatus(DeviceStatus.FISCAL);
        fiscalizationRepository.save(fiscalization);

        logHistoryService.logHistory(newDevice.getSerialNumber(), false, Action.UPDATE, null, null, DeviceStatus.REGISTERED.getCode(), DeviceStatus.FISCAL.getCode());
        cacheService.evictFiscalCache(fiscalization.getHashKey());
    }


    @Override
    public void maskAsPaid(Long id) {
        Fiscalization fiscalization = fiscalizationRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));
        fiscalization.setPaid(true);
        fiscalizationRepository.save(fiscalization);
        cacheService.evictFiscalCache(fiscalization.getHashKey());
    }

    @Override
    public void mergeWithNIC(OfdApplicationMergeDTO ofdApplicationMergeDTO) {
        ofdApplicationService.merge(ofdApplicationMergeDTO);
    }

    @Override
    public void swapFiscalModule(FiscalSwapFiscalModuleDTO fiscalSwapFiscalModuleDTO) {
        Fiscalization fiscalization = fiscalizationRepository.findById(fiscalSwapFiscalModuleDTO.getFiscalizationId()).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));

        if (!(fiscalization.getOfdApplication() == null || ApplicationType.DEREGISTER_FISCAL_MODULE.equals(fiscalization.getOfdApplication().getApplicationType()))) {
            throw validationService.badRequestExceptionThrow(SWAP_MESSAGE, fiscalSwapFiscalModuleDTO.getFiscalModuleSerial()).get();
        }

        if (StringUtils.isNotEmpty(fiscalSwapFiscalModuleDTO.getOtherReason())) {
            fiscalization.setOtherReason(fiscalSwapFiscalModuleDTO.getOtherReason());
        } else if (fiscalSwapFiscalModuleDTO.getReason() != null) {
            Reason reasonEnum = Reason.get(fiscalSwapFiscalModuleDTO.getReason());
            if (reasonEnum == null) {
                throw validationService.badRequestExceptionThrow(INVALID_INPUT, fiscalSwapFiscalModuleDTO.getReason()).get();
            }
            fiscalization.setReason(reasonEnum);
        } else {
            throw validationService.requiredExceptionThrow(REASON).get();
        }

        if (fiscalization.getFiscalModule().getSerialNumber().equals(fiscalSwapFiscalModuleDTO.getFiscalModuleSerial())) {
            throw validationService.entityAlreadyExistExceptionThrow(FISCAL_MODULE, SERIAL_NUMBER, fiscalSwapFiscalModuleDTO.getFiscalModuleSerial()).get();
        }

        FiscalModule newFiscalModule = fiscalModuleRepository.findFirstBySerialNumber(fiscalSwapFiscalModuleDTO.getFiscalModuleSerial());
        if (newFiscalModule == null) {
            throw validationService.entityNotFoundExceptionThrow(fiscalSwapFiscalModuleDTO.getFiscalModuleSerial()).get();
        }

        Fiscalization fiscalizationNew = fiscalizationRepository.findFirstByDeviceIdAndFiscalModuleId(fiscalization.getDeviceId(), newFiscalModule.getId());
        if (fiscalizationNew != null) {
            throw validationService.badRequestExceptionThrow(DEVICE_AND_FISCAL_ALREADY_REGISTERED, fiscalization.getDevice().getSerialNumber(), newFiscalModule.getSerialNumber()).get();
        }

        //Remove old FM from customer branch fiscal module
        customerBranchFiscalModuleService.deleteByFiscalModuleId(fiscalization.getFiscalModuleId());

        //Remove old FM from customer fiscal module
        customerFiscalModuleService.deleteByFiscalModuleId(fiscalization.getFiscalModuleId());

        //Update FM status
        fiscalModuleService.updateStatusAndClear(fiscalization.getFiscalModule(), DeviceStatus.getByReason(Reason.get(fiscalSwapFiscalModuleDTO.getReason())));


        //// ------------------------------------------------- REGISTER NEW FM -----------------------------------/////////////

        //Add or update new FM to customer
        CustomerFiscalModule customerFiscalModule = customerFiscalModuleRepository.findByCustomerIdAndFiscalModuleId(fiscalization.getCustomerId(), newFiscalModule.getId());
        customerFiscalModule = Optional.ofNullable(customerFiscalModule).orElse(new CustomerFiscalModule());
        if (customerFiscalModule.getId() == null) {
            customerFiscalModule.setFiscalModuleId(newFiscalModule.getId());
            customerFiscalModule.setCustomerId(fiscalization.getCustomerId());
            customerFiscalModule.setCustomer(fiscalization.getCustomer());
        }
        customerFiscalModule.setStatus(DeviceStatus.FISCAL);
        customerFiscalModuleRepository.save(customerFiscalModule);
        warehouseHistoryService.create(newFiscalModule.getSerialNumber(), customerFiscalModule.getId(), EntityTypeEnum.CUSTOMER_FISCAL_MODULE, DeviceStatus.FISCAL);

        logHistoryService.logHistory(newFiscalModule.getSerialNumber(), true, Action.TRANSFER, "Реестр", fiscalization.getCustomer().getCompany().getName());
        logHistoryService.logHistory(newFiscalModule.getSerialNumber(), true, Action.TRANSFER, fiscalization.getCustomer().getCompany().getName(), fiscalization.getCustomer().getName());


        //Add or update new FM to customer branch
        CustomerBranchFiscalModule customerBranchFiscalModule = customerBranchFiscalModuleRepository.findFirstByCustomerBranchIdAndFiscalModuleId(fiscalization.getCustomerBranchId(), newFiscalModule.getId());
        customerBranchFiscalModule = Optional.ofNullable(customerBranchFiscalModule).orElse(new CustomerBranchFiscalModule());
        if (customerBranchFiscalModule.getId() == null) {
            customerBranchFiscalModule.setFiscalModuleId(newFiscalModule.getId());
            customerBranchFiscalModule.setCustomerBranchId(fiscalization.getCustomerBranchId());
            customerBranchFiscalModule.setCustomerBranch(fiscalization.getCustomerBranch());
        }
        customerBranchFiscalModule.setStatus(DeviceStatus.FISCAL);
        customerBranchFiscalModuleRepository.save(customerBranchFiscalModule);
        warehouseHistoryService.create(newFiscalModule.getSerialNumber(), customerBranchFiscalModule.getId(), EntityTypeEnum.CUSTOMER_BRANCH_FISCAL_MODULE, DeviceStatus.FISCAL);

        logHistoryService.logHistory(newFiscalModule.getSerialNumber(), true, Action.TRANSFER, fiscalization.getCustomer().getName(), fiscalization.getCustomerBranch().getName());

        //Update New FM status
        newFiscalModule.setStatus(DeviceStatus.REGISTERED);
        fiscalModuleRepository.save(newFiscalModule);
        warehouseHistoryService.create(newFiscalModule.getSerialNumber(), newFiscalModule.getId(), EntityTypeEnum.FISCAL_MODULE, DeviceStatus.REGISTERED);

        logHistoryService.logHistory(newFiscalModule.getSerialNumber(), true, Action.UPDATE, null, null, DeviceStatus.NOT_REGISTERED.getCode(), DeviceStatus.REGISTERED.getCode());

        //Set new FM
        String oldHashKey = fiscalization.getHashKey();
        fiscalization.setFiscalModuleId(newFiscalModule.getId());
        fiscalization.setHashKey(SecurityUtils.fiscalHashSha(fiscalization.getDevice().getSerialNumber(), newFiscalModule.getSerialNumber()));
        fiscalization.setStatus(DeviceStatus.FISCAL);

        logHistoryService.logHistory(newFiscalModule.getSerialNumber(), true, Action.UPDATE, null, null, DeviceStatus.REGISTERED.getCode(), DeviceStatus.FISCAL.getCode());

        fiscalizationRepository.save(fiscalization);
        cacheService.evictFiscalCache(oldHashKey);
        cacheService.evictFiscalCache(fiscalization.getHashKey());
    }

    @Override
    public void syncWithSmartpos(FiscalizationSyncDTO fiscalizationSyncDTO) {
        Fiscalization fiscalization = fiscalizationRepository.findById(fiscalizationSyncDTO.getFiscalizationId()).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));
        if (fiscalization.getOfdApplicationId() != null) {
            OfdApplication ofdApplication = ofdApplicationRepository.getReferenceById(fiscalization.getOfdApplicationId());
            if (OfdStatus.ACCEPT.equals(ofdApplication.getOfdStatus()) || (OfdStatus.REJECT.equals(ofdApplication.getOfdStatus()) && ApplicationType.DEREGISTER.equals(ofdApplication.getApplicationType()))
                //Bu degani ro'yxatdan chiqarishga ariza yuborilgan, lekin NIC rad etgan. Ro'yxatdan chiqarmagan. Kassa hali faol.
            ) {
                rabbitMqSmartPosProducer.updateCustomer(fiscalization.getId());
            }
        }
    }

    @Override
    public void syncAllWithSmartpos() {
        Long count = fiscalizationRepository.getCountByStatusForSmartpos(DeviceStatus.FISCAL_ACTIVE);
        int start = 0;
        int limit = 20;
        while (start <= count) {
            List<Fiscalization> fiscalizations = getTinListIntervalForSmartpos(start, limit);
            if (CollectionUtils.isNotEmpty(fiscalizations)) {
                for (Fiscalization fiscalization : fiscalizations) {
                    OfdApplication ofdApplication = ofdApplicationRepository.getReferenceById(fiscalization.getOfdApplicationId());
                    if (OfdStatus.ACCEPT.equals(ofdApplication.getOfdStatus()) || (OfdStatus.REJECT.equals(ofdApplication.getOfdStatus()) && ApplicationType.DEREGISTER.equals(ofdApplication.getApplicationType()))
                        //Bu degani ro'yxatdan chiqarishga ariza yuborilgan, lekin NIC rad etgan. Ro'yxatdan chiqarmagan. Kassa hali faol.
                    ) {
                        rabbitMqSmartPosProducer.updateCustomer(fiscalization.getId());
                    }
                }
            }
            start += limit;
        }
    }

    @Override
    public String downloadApplicationTemplate(ApplicationTemplateDTO applicationTemplateDTO) {
        Fiscalization fiscalization = fiscalizationRepository.findById(applicationTemplateDTO.getFiscalizationId()).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));
        return thymeleafService.generateApplicationTemplate(fiscalization, applicationTemplateDTO);
    }

    private List<Fiscalization> getTinListIntervalForSmartpos(int start, int limit) {
        TypedQuery<Fiscalization> query = entityManager.createQuery("select f from Fiscalization f where f.deleted=false and f.ofdApplicationId is not null and f.status='FISCAL_ACTIVE' and f.syncedSmartpos=false order by f.id", Fiscalization.class).setFirstResult(start).setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public void clearFiscalCache(FiscalClearCacheDTO fiscalClearCacheDTO) {
        List<Fiscalization> fiscalizations = null;
        if (fiscalClearCacheDTO.getFiscalModuleSerial() != null) {
            fiscalizations = fiscalizationRepository.findAllByFiscalModule_SerialNumber(fiscalClearCacheDTO.getFiscalModuleSerial());
        } else if (fiscalClearCacheDTO.getDeviceSerial() != null) {
            fiscalizations = fiscalizationRepository.findAllByDevice_SerialNumber(fiscalClearCacheDTO.getDeviceSerial());
        }

        if (CollectionUtils.isNotEmpty(fiscalizations)) {
            fiscalizations.forEach(fiscalization -> cacheService.evictFiscalCache(fiscalization.getHashKey()));
        }
    }

    @Override
    public void updateStatus(Long id, DeviceStatus status) {
        Fiscalization fiscalization = fiscalizationRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));
        fiscalization.setStatus(status);
        fiscalizationRepository.save(fiscalization);
        cacheService.evictFiscalCache(fiscalization.getHashKey());
    }

    @Override
    public Fiscalization findFirstByDeviceIdOrderByIdDesc(Long deviceId) {
        return fiscalizationRepository.findFirstByDeviceIdOrderByIdDesc(deviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));
    }
}
