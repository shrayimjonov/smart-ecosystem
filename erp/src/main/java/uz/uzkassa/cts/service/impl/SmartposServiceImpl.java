package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.payload.SmartPosRemoveDevicePayload;
import uz.uzkassa.cts.dto.smartpos.*;
import uz.uzkassa.cts.dto.smartpos.company.CustomerCtoDTO;
import uz.uzkassa.cts.dto.sms.ReceiptSmsRequestDTO;
import uz.uzkassa.cts.dto.user.UserShortDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.DeviceModelFilter;
import uz.uzkassa.cts.integrations.SmartposClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqSmartPosProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.ActOfTransmissionFactoryService;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.PaymentActService;
import uz.uzkassa.cts.service.SmartposService;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
@Slf4j
public class SmartposServiceImpl extends BaseServiceImpl<Device, Long> implements SmartposService {
    private final DeviceRepository deviceRepository;
    private final FiscalizationRepository fiscalizationRepository;
    private final CustomerRepository customerRepository;
    private final CustomerBranchRepository customerBranchRepository;
    private final UserRepository userRepository;
    private final OfdApplicationRepository ofdApplicationRepository;
    private final RabbitMqSmartPosProducer rabbitMqSmartPosProducer;
    private final SmartposClient smartposClient;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final CustomerEmployeeRepository customerEmployeeRepository;
    private final ActOfTransmissionFactoryService actOfTransmissionService;
    private final PaymentActService paymentActService;
    private final CacheService cacheService;

    public SmartposServiceImpl(DeviceRepository deviceRepository,
                               FiscalizationRepository fiscalizationRepository,
                               CustomerRepository customerRepository,
                               CustomerBranchRepository customerBranchRepository,
                               UserRepository userRepository,
                               OfdApplicationRepository ofdApplicationRepository,
                               RabbitMqSmartPosProducer rabbitMqSmartPosProducer,
                               SmartposClient smartposClient,
                               CustomerBranchDeviceRepository customerBranchDeviceRepository,
                               CustomerEmployeeRepository customerEmployeeRepository,
                               ActOfTransmissionFactoryService actOfTransmissionService,
                               PaymentActService paymentActService,
                               CacheService cacheService
    ) {
        super(deviceRepository);
        this.deviceRepository = deviceRepository;
        this.fiscalizationRepository = fiscalizationRepository;
        this.customerRepository = customerRepository;
        this.customerBranchRepository = customerBranchRepository;
        this.userRepository = userRepository;
        this.ofdApplicationRepository = ofdApplicationRepository;
        this.rabbitMqSmartPosProducer = rabbitMqSmartPosProducer;
        this.smartposClient = smartposClient;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.customerEmployeeRepository = customerEmployeeRepository;
        this.actOfTransmissionService = actOfTransmissionService;
        this.paymentActService = paymentActService;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional(readOnly = true)
    public void fiscalSendSms(ReceiptSmsRequestDTO receiptSmsRequestDTO) {
        smartposClient.fiscalSmsSend(receiptSmsRequestDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public SmartposFiscalizationDetailDTO getFiscalizationDetail(DeviceModelFilter deviceModelFilter) {


        Device device = deviceRepository.findFirstBySerialNumber(deviceModelFilter.getDeviceSerial());
        if (device == null) {
            throw validationService.entityNotFoundExceptionThrow(DEVICE).get();
        }

        Fiscalization fiscalization = fiscalizationRepository.findFirstByDeviceIdOrderByIdDesc(device.getId()).orElse(null);
        if (fiscalization == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCALIZATION).get();
        }

        CustomerEmployee customerEmployee = customerEmployeeRepository.findFirstByCustomerIdAndRole(fiscalization.getCustomerId(), Role.DIRECTOR).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE));
        SmartposOwnerDTO smartposOwnerDTO = new SmartposOwnerDTO();
        smartposOwnerDTO.setId(customerEmployee.getId());
        smartposOwnerDTO.setFirstName(customerEmployee.getFirstName());
        smartposOwnerDTO.setLastName(customerEmployee.getLastName());
        smartposOwnerDTO.setPatronymic(customerEmployee.getPatronymic());
        smartposOwnerDTO.setPhone(customerEmployee.getCustomer().getPhone());

        SmartposCustomerDetailDTO customerDetailDTO = new SmartposCustomerDetailDTO();
        customerDetailDTO.setId(fiscalization.getCustomerId());
        customerDetailDTO.setName(fiscalization.getCustomer().getCompanyName());
        customerDetailDTO.setTin(fiscalization.getCustomer().getTin());
        customerDetailDTO.setOldTin(fiscalization.getCustomer().getOldTin());
        customerDetailDTO.setAppType(fiscalization.getCustomer().getAppType().getCode());
        customerDetailDTO.setOwner(smartposOwnerDTO);

        if (fiscalization.getCustomer().getVat() != null) {
            customerDetailDTO.setVat(fiscalization.getCustomer().getVat().getPercent());
        }
        customerDetailDTO.setBusinessType(fiscalization.getCustomer().getBusinessType());
        customerDetailDTO.setActivityTypeCode(fiscalization.getCustomer().getActivityType().getCode());
        if (fiscalization.getCustomer().getActivityType().getParent() != null) {
            customerDetailDTO.setActivityTypeParentCode(fiscalization.getCustomer().getActivityType().getParent().getCode());
        }

        SmartPosAddressDTO customerAddress = new SmartPosAddressDTO();
        customerAddress.setHouse(fiscalization.getCustomer().getAddress().getHouse());
        customerAddress.setStreet(fiscalization.getCustomer().getAddress().getStreet());
        customerAddress.setApartment(fiscalization.getCustomer().getAddress().getApartment());
        if (fiscalization.getCustomer().getAddress().getRegion() != null) {
            customerAddress.setRegionCode(fiscalization.getCustomer().getAddress().getRegion().getCode());
        }
        if (fiscalization.getCustomer().getAddress().getDistrict() != null) {
            customerAddress.setDistrictCode(fiscalization.getCustomer().getAddress().getDistrict().getCode());
        }
        customerAddress.setLongitude(fiscalization.getCustomer().getAddress().getLongitude());
        customerAddress.setLatitude(fiscalization.getCustomer().getAddress().getLatitude());

        customerDetailDTO.setAddress(customerAddress);


        SmartposCustomerBranchDetailDTO customerBranchDTO = new SmartposCustomerBranchDetailDTO();
        customerBranchDTO.setId(fiscalization.getCustomerBranchId());
        customerBranchDTO.setName(fiscalization.getCustomerBranch().getName());
        customerBranchDTO.setBusinessType(fiscalization.getCustomer().getBusinessType());
        if (fiscalization.getCustomerBranch().getActivityType() != null) {
            customerBranchDTO.setActivityTypeCode(fiscalization.getCustomerBranch().getActivityType().getCode());
            if (fiscalization.getCustomerBranch().getActivityType().getParent() != null) {
                customerBranchDTO.setActivityTypeParentCode(fiscalization.getCustomerBranch().getActivityType().getParent().getCode());
            }
        }
        customerBranchDTO.setOfdId(fiscalization.getCustomerBranch().getOfdId());
        customerBranchDTO.setLongitude(fiscalization.getCustomerBranch().getAddress().getLongitude());
        customerBranchDTO.setLatitude(fiscalization.getCustomerBranch().getAddress().getLatitude());

        SmartPosAddressDTO customerBranchAddress = new SmartPosAddressDTO();
        customerBranchAddress.setHouse(fiscalization.getCustomerBranch().getAddress().getHouse());
        customerBranchAddress.setStreet(fiscalization.getCustomerBranch().getAddress().getStreet());
        customerBranchAddress.setApartment(fiscalization.getCustomerBranch().getAddress().getApartment());

        customerBranchAddress.setLatitude(fiscalization.getCustomerBranch().getAddress().getLatitude());
        customerBranchAddress.setLongitude(fiscalization.getCustomerBranch().getAddress().getLongitude());

        if (fiscalization.getCustomerBranch().getAddress().getRegion() != null) {
            customerBranchAddress.setRegionCode(fiscalization.getCustomerBranch().getAddress().getRegion().getCode());
        }
        if (fiscalization.getCustomerBranch().getAddress().getDistrict() != null) {
            customerBranchAddress.setDistrictCode(fiscalization.getCustomerBranch().getAddress().getDistrict().getCode());
        }

        customerBranchDTO.setAddress(customerBranchAddress);

        SmartposFiscalizationDetailDTO fiscalizationDetailSmartposDTO = new SmartposFiscalizationDetailDTO();
        fiscalizationDetailSmartposDTO.setCustomer(customerDetailDTO);
        fiscalizationDetailSmartposDTO.setCustomerBranch(customerBranchDTO);
        fiscalizationDetailSmartposDTO.setDeviceSerial(fiscalization.getDevice().getSerialNumber());

        return fiscalizationDetailSmartposDTO;
    }

    @Override
    public SmartposFiscalizationDetailDTO update(Customer customer, CustomerBranch customerBranch) {
        log.info(">>>>>>>>>> Start Update Customer To SmartPos  <<<<<<<<<<<<");

        Fiscalization fiscalization = fiscalizationRepository.findFirstByCustomerIdAndCustomerBranchIdAndStatus(customer.getId(), customerBranch.getId(), DeviceStatus.FISCAL_ACTIVE);
        if (fiscalization == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCALIZATION).get();
        }

        CustomerEmployee customerEmployee = customerEmployeeRepository.findFirstByCustomerIdAndRole(fiscalization.getCustomerId(), Role.DIRECTOR).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE));
        SmartposOwnerDTO smartposOwnerDTO = new SmartposOwnerDTO();
        smartposOwnerDTO.setId(customerEmployee.getId());
        smartposOwnerDTO.setFirstName(customerEmployee.getFirstName());
        smartposOwnerDTO.setLastName(customerEmployee.getLastName());
        smartposOwnerDTO.setPatronymic(customerEmployee.getPatronymic());
        smartposOwnerDTO.setPhone(customerEmployee.getCustomer().getPhone());

        SmartposCustomerDetailDTO customerDetailDTO = new SmartposCustomerDetailDTO();
        customerDetailDTO.setId(customer.getId());
        customerDetailDTO.setName(customer.getCompanyName());
        customerDetailDTO.setTin(customer.getTin());
        customerDetailDTO.setOldTin(customer.getOldTin());
        customerDetailDTO.setOwner(smartposOwnerDTO);
        if (customer.getVat() != null) {
            customerDetailDTO.setVat(customer.getVat().getPercent());
        }
        customerDetailDTO.setBusinessType(customer.getBusinessType());
        customerDetailDTO.setActivityTypeCode(customer.getActivityType().getCode());
        if (customer.getActivityType().getParent() != null) {
            customerDetailDTO.setActivityTypeParentCode(customer.getActivityType().getParent().getCode());
        }

        SmartPosAddressDTO customerAddress = new SmartPosAddressDTO();
        customerAddress.setHouse(customer.getAddress().getHouse());
        customerAddress.setStreet(customer.getAddress().getStreet());
        customerAddress.setApartment(customer.getAddress().getApartment());
        if (customer.getAddress().getRegion() != null) {
            customerAddress.setRegionCode(customer.getAddress().getRegion().getCode());
        }
        if (customer.getAddress().getDistrict() != null) {
            customerAddress.setDistrictCode(customer.getAddress().getDistrict().getCode());
        }

        customerDetailDTO.setAddress(customerAddress);


        SmartposCustomerBranchDetailDTO customerBranchDTO = new SmartposCustomerBranchDetailDTO();
        customerBranchDTO.setId(customerBranch.getId());
        customerBranchDTO.setName(customerBranch.getName());
        customerBranchDTO.setBusinessType(customerBranch.getCustomer().getBusinessType());
        customerBranchDTO.setActivityTypeCode(customerBranch.getActivityType().getCode());
        if (customerBranch.getActivityType().getParent() != null) {
            customerBranchDTO.setActivityTypeParentCode(customerBranch.getActivityType().getParent().getCode());
        }
        SmartPosAddressDTO customerBranchAddress = new SmartPosAddressDTO();
        customerBranchAddress.setHouse(customerBranch.getAddress().getHouse());
        customerBranchAddress.setStreet(customerBranch.getAddress().getStreet());
        customerBranchAddress.setApartment(customerBranch.getAddress().getApartment());
        customerBranchAddress.setLatitude(customerBranch.getAddress().getLatitude());
        customerBranchAddress.setLongitude(customerBranch.getAddress().getLongitude());
        if (customerBranch.getAddress().getRegion() != null) {
            customerBranchAddress.setRegionCode(customerBranch.getAddress().getRegion().getCode());
        }
        if (customerBranch.getAddress().getDistrict() != null) {
            customerBranchAddress.setDistrictCode(customerBranch.getAddress().getDistrict().getCode());
        }

        if (Objects.nonNull(fiscalization.getCustomerBranch()) && Objects.nonNull(fiscalization.getCustomerBranch().getOfdId()))
            customerBranchDTO.setOfdId(fiscalization.getCustomerBranch().getOfdId());
        customerBranchDTO.setAddress(customerBranchAddress);

        SmartposFiscalizationDetailDTO fiscalizationDetailSmartposDTO = new SmartposFiscalizationDetailDTO();
        fiscalizationDetailSmartposDTO.setCustomer(customerDetailDTO);
        fiscalizationDetailSmartposDTO.setCustomerBranch(customerBranchDTO);
        fiscalizationDetailSmartposDTO.setDeviceSerial(fiscalization.getDevice().getSerialNumber());
        smartposClient.updateCustomer(fiscalizationDetailSmartposDTO);
        cacheService.evictFiscalCache(fiscalization.getHashKey());
        return fiscalizationDetailSmartposDTO;
    }

    @Override
    public SmartposFiscalizationDetailDTO updateCustomer(Long customerId, Long customerBranchId) {
        return this.update(customerRepository.getReferenceById(customerId), customerBranchRepository.getReferenceById(customerBranchId));
    }

    @Override
    public void updateCustomer(Long fiscalizationId) {

        log.info(">>>>>>>>>> Start Cron Update Customer To SmartPos  <<<<<<<<<<<<");
        Fiscalization fiscalization = fiscalizationRepository.findById(fiscalizationId).orElseThrow(validationService.entityNotFoundExceptionThrow(FISCALIZATION));
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findFirstByCustomerBranchIdAndDeviceId(fiscalization.getCustomerBranchId(), fiscalization.getDeviceId());
        if (fiscalization == null) {
            throw validationService.entityNotFoundExceptionThrow(FISCALIZATION).get();
        }

        if (fiscalization.getOfdApplication() == null) {
            throw validationService.entityNotFoundExceptionThrow(APPLICATION).get();
        }
        if (OfdStatus.ACCEPT.equals(fiscalization.getOfdApplication().getOfdStatus()) ||
                (OfdStatus.REJECT.equals(fiscalization.getOfdApplication().getOfdStatus()) && ApplicationType.DEREGISTER.equals(fiscalization.getOfdApplication().getApplicationType()))
            //Bu degani ro'yxatdan chiqarishga ariza yuborilgan, lekin NIC rad etgan. Ro'yxatdan chiqarmagan. Kassa hali faol.
        ) {
            CustomerEmployee customerEmployee = customerEmployeeRepository.findFirstByCustomerIdAndRole(fiscalization.getCustomerId(), Role.DIRECTOR).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE));
            SmartposOwnerDTO smartposOwnerDTO = new SmartposOwnerDTO();
            smartposOwnerDTO.setId(customerEmployee.getId());
            smartposOwnerDTO.setFirstName(customerEmployee.getFirstName());
            smartposOwnerDTO.setLastName(customerEmployee.getLastName());
            smartposOwnerDTO.setPatronymic(customerEmployee.getPatronymic());
            smartposOwnerDTO.setPhone(customerEmployee.getCustomer().getPhone());

            SmartposCustomerDetailDTO customerDetailDTO = new SmartposCustomerDetailDTO();
            customerDetailDTO.setId(fiscalization.getCustomerId());
            customerDetailDTO.setName(fiscalization.getCustomer().getCompanyName());
            customerDetailDTO.setTin(fiscalization.getCustomer().getTin());
            customerDetailDTO.setOldTin(fiscalization.getCustomer().getOldTin());
            customerDetailDTO.setAppType(fiscalization.getCustomer().getAppType().getCode());
            customerDetailDTO.setOwner(smartposOwnerDTO);
            if (fiscalization.getCustomer().getVat() != null) {
                customerDetailDTO.setVat(fiscalization.getCustomer().getVat().getPercent());
            }
            customerDetailDTO.setBusinessType(fiscalization.getCustomer().getBusinessType());
            customerDetailDTO.setActivityTypeCode(fiscalization.getCustomer().getActivityType().getCode());
            if (fiscalization.getCustomer().getActivityType().getParent() != null) {
                customerDetailDTO.setActivityTypeParentCode(fiscalization.getCustomer().getActivityType().getParent().getCode());
            }

            SmartPosAddressDTO customerAddress = new SmartPosAddressDTO();
            customerAddress.setHouse(fiscalization.getCustomer().getAddress().getHouse());
            customerAddress.setStreet(fiscalization.getCustomer().getAddress().getStreet());
            customerAddress.setApartment(fiscalization.getCustomer().getAddress().getApartment());
            if (fiscalization.getCustomer().getAddress().getRegion() != null) {
                customerAddress.setRegionCode(fiscalization.getCustomer().getAddress().getRegion().getCode());
            }
            if (fiscalization.getCustomer().getAddress().getDistrict() != null) {
                customerAddress.setDistrictCode(fiscalization.getCustomer().getAddress().getDistrict().getCode());
            }
            customerDetailDTO.setAddress(customerAddress);
            if (fiscalization.getCustomer().getManagerId() != null) {
                User manager = fiscalization.getCustomer().getManager();
                UserShortDTO managerDTO = new UserShortDTO();
                managerDTO.setId(manager.getId());
                managerDTO.setLogin(manager.getLogin());
                managerDTO.setLastName(manager.getLastName());
                managerDTO.setFirstName(manager.getFirstName());
                managerDTO.setPatronymic(manager.getPatronymic());
                customerDetailDTO.setManager(managerDTO);
            }

            SmartposCustomerBranchDetailDTO customerBranchDTO = new SmartposCustomerBranchDetailDTO();
            customerBranchDTO.setId(fiscalization.getCustomerBranchId());
            customerBranchDTO.setName(fiscalization.getCustomerBranch().getName());
            customerBranchDTO.setBusinessType(fiscalization.getCustomer().getBusinessType());
            if (fiscalization.getCustomerBranch().getActivityType() != null) {
                customerBranchDTO.setActivityTypeCode(fiscalization.getCustomerBranch().getActivityType().getCode());
                if (fiscalization.getCustomerBranch().getActivityType().getParent() != null) {
                    customerBranchDTO.setActivityTypeParentCode(fiscalization.getCustomerBranch().getActivityType().getParent().getCode());
                }
            }
            customerBranchDTO.setOfdId(fiscalization.getCustomerBranch().getOfdId());

            SmartPosAddressDTO customerBranchAddress = new SmartPosAddressDTO();
            customerBranchAddress.setHouse(fiscalization.getCustomerBranch().getAddress().getHouse());
            customerBranchAddress.setStreet(fiscalization.getCustomerBranch().getAddress().getStreet());
            customerBranchAddress.setApartment(fiscalization.getCustomerBranch().getAddress().getApartment());
            customerBranchAddress.setLatitude(fiscalization.getCustomerBranch().getAddress().getLatitude());
            customerBranchAddress.setLongitude(fiscalization.getCustomerBranch().getAddress().getLongitude());
            if (fiscalization.getCustomerBranch().getAddress().getRegion() != null) {
                customerBranchAddress.setRegionCode(fiscalization.getCustomerBranch().getAddress().getRegion().getCode());
            }
            if (fiscalization.getCustomerBranch().getAddress().getDistrict() != null) {
                customerBranchAddress.setDistrictCode(fiscalization.getCustomerBranch().getAddress().getDistrict().getCode());
            }
            if (Objects.nonNull(fiscalization.getCustomerBranch()) && Objects.nonNull(fiscalization.getCustomerBranch().getOfdId()))
                customerBranchDTO.setOfdId(fiscalization.getCustomerBranch().getOfdId());
            customerBranchDTO.setAddress(customerBranchAddress);

            SmartposFiscalizationDetailDTO fiscalizationDetailSmartposDTO = new SmartposFiscalizationDetailDTO();
            fiscalizationDetailSmartposDTO.setCustomer(customerDetailDTO);
            fiscalizationDetailSmartposDTO.setCustomerBranch(customerBranchDTO);
            fiscalizationDetailSmartposDTO.setDeviceSerial(fiscalization.getDevice().getSerialNumber());
            if (customerBranchDevice != null && customerBranchDevice.getDeviceType() != null) {
                fiscalizationDetailSmartposDTO.setDeviceType(customerBranchDevice.getDeviceType().getCode());
            }
            smartposClient.updateCustomer(fiscalizationDetailSmartposDTO);
            fiscalization.setSyncedSmartpos(true);
            fiscalizationRepository.save(fiscalization);
            cacheService.evictFiscalCache(fiscalization.getHashKey());
        }
    }

    @Override
    public void updateCustomerByType(Long customerBranchDeviceId) {
        CustomerBranchDevice customerBranchDevice = customerBranchDeviceRepository.findById(customerBranchDeviceId).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));
        if (customerBranchDevice.getDeviceType() != null && !EnumSet.of(DeviceType.TERMINAL, DeviceType.SOFTWARE).contains(customerBranchDevice.getDeviceType())) {
            throw validationService.badRequestExceptionThrow(DEVICE_TYPE, customerBranchDevice.getDeviceType().getName()).get();
        }
        Customer customer = customerBranchDevice.getCustomerBranch().getCustomer();

        ActOfTransmission actOfTransmission = actOfTransmissionService.getByDeviceIdAndCustomerId(customerBranchDevice.getDeviceId(), customer.getId());
        if (actOfTransmission != null) {
            if (!(ContractStatus.ACCEPTED.equals(actOfTransmission.getStatus()) || ContractStatus.CLOSED.equals(actOfTransmission.getStatus()))) {
                throw validationService.badRequestExceptionThrow(ACT_TRANSMISSION_APPROVE, actOfTransmission.getNumber()).get();
            }
        }
        PaymentAct paymentAct = paymentActService.getByDeviceIdAndCustomerId(customerBranchDevice.getDeviceId(), customer.getId());
        if (paymentAct != null) {
            if (!(ContractStatus.ACCEPTED.equals(paymentAct.getStatus()) || ContractStatus.CLOSED.equals(paymentAct.getStatus()))) {
                throw validationService.badRequestExceptionThrow(PAYMENT_ACT, paymentAct.getNumber()).get();
            }
        }

        CustomerBranch customerBranch = customerBranchDevice.getCustomerBranch();
        CustomerEmployee customerEmployee = customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE));
        SmartposOwnerDTO smartposOwnerDTO = new SmartposOwnerDTO();
        smartposOwnerDTO.setId(customerEmployee.getId());
        smartposOwnerDTO.setFirstName(customerEmployee.getFirstName());
        smartposOwnerDTO.setLastName(customerEmployee.getLastName());
        smartposOwnerDTO.setPatronymic(customerEmployee.getPatronymic());
        smartposOwnerDTO.setPhone(customer.getPhone());
        /*CUSTOMER*/
        SmartposCustomerDetailDTO customerDetailDTO = new SmartposCustomerDetailDTO();
        customerDetailDTO.setId(customer.getId());
        customerDetailDTO.setName(customer.getCompanyName());
        customerDetailDTO.setTin(customer.getTin());
        customerDetailDTO.setOldTin(customer.getOldTin());
        customerDetailDTO.setAppType(customer.getAppType().getCode());
        customerDetailDTO.setOwner(smartposOwnerDTO);
        if (customer.getVat() != null) {
            customerDetailDTO.setVat(customer.getVat().getPercent());
        }
        customerDetailDTO.setBusinessType(customer.getBusinessType());
        customerDetailDTO.setActivityTypeCode(customer.getActivityType().getCode());
        if (customer.getActivityType().getParent() != null) {
            customerDetailDTO.setActivityTypeParentCode(customer.getActivityType().getParent().getCode());
        }

        SmartPosAddressDTO customerAddress = new SmartPosAddressDTO();
        customerAddress.setHouse(customer.getAddress().getHouse());
        customerAddress.setStreet(customer.getAddress().getStreet());
        customerAddress.setApartment(customer.getAddress().getApartment());
        if (customer.getAddress().getRegion() != null) {
            customerAddress.setRegionCode(customer.getAddress().getRegion().getCode());
        }
        if (customer.getAddress().getDistrict() != null) {
            customerAddress.setDistrictCode(customer.getAddress().getDistrict().getCode());
        }
        customerDetailDTO.setAddress(customerAddress);
        if (customer.getManagerId() != null) {
            User manager = customer.getManager();
            UserShortDTO managerDTO = new UserShortDTO();
            managerDTO.setId(manager.getId());
            managerDTO.setLogin(manager.getLogin());
            managerDTO.setLastName(manager.getLastName());
            managerDTO.setFirstName(manager.getFirstName());
            managerDTO.setPatronymic(manager.getPatronymic());
            customerDetailDTO.setManager(managerDTO);
        }
        /*CUSTOMER BRANCH*/
        SmartposCustomerBranchDetailDTO customerBranchDTO = new SmartposCustomerBranchDetailDTO();
        customerBranchDTO.setId(customerBranch.getId());
        customerBranchDTO.setName(customerBranch.getName());
        customerBranchDTO.setBusinessType(customer.getBusinessType());
        if (customerBranch.getActivityType() != null) {
            customerBranchDTO.setActivityTypeCode(customerBranch.getActivityType().getCode());
            if (customerBranch.getActivityType().getParent() != null) {
                customerBranchDTO.setActivityTypeParentCode(customerBranch.getActivityType().getParent().getCode());
            }
        }

        SmartPosAddressDTO customerBranchAddress = new SmartPosAddressDTO();
        customerBranchAddress.setHouse(customerBranch.getAddress().getHouse());
        customerBranchAddress.setStreet(customerBranch.getAddress().getStreet());
        customerBranchAddress.setApartment(customerBranch.getAddress().getApartment());
        customerBranchAddress.setLatitude(customerBranch.getAddress().getLatitude());
        customerBranchAddress.setLongitude(customerBranch.getAddress().getLongitude());
        if (customerBranch.getAddress().getRegion() != null) {
            customerBranchAddress.setRegionCode(customerBranch.getAddress().getRegion().getCode());
        }
        if (customerBranch.getAddress().getDistrict() != null) {
            customerBranchAddress.setDistrictCode(customerBranch.getAddress().getDistrict().getCode());
        }
        customerBranchDTO.setAddress(customerBranchAddress);
        SmartposFiscalizationDetailDTO fiscalizationDetailSmartposDTO = new SmartposFiscalizationDetailDTO();
        fiscalizationDetailSmartposDTO.setCustomer(customerDetailDTO);
        fiscalizationDetailSmartposDTO.setCustomerBranch(customerBranchDTO);
        fiscalizationDetailSmartposDTO.setDeviceSerial(customerBranchDevice.getDevice().getSerialNumber());
        if (customerBranchDevice.getDeviceType() != null) {
            fiscalizationDetailSmartposDTO.setDeviceType(customerBranchDevice.getDeviceType().getCode());
        }
        smartposClient.syncCustomer(fiscalizationDetailSmartposDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public CustomerCtoDTO getCustomerDetails(Long customerId) {
        log.info(">>>>>>>>>> Start Get Customer Detail From SmartPos  <<<<<<<<<<<<");
        Customer customer = customerRepository.findById(customerId).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        List<Fiscalization> fiscalizations = fiscalizationRepository.findByCustomerIdAndStatusForSmartpos(customerId, DeviceStatus.FISCAL_ACTIVE);
        fiscalizations.forEach(fiscalization -> {
            if (fiscalization.getOfdApplicationId() != null) {
                OfdApplication ofdApplication = ofdApplicationRepository.getReferenceById(fiscalization.getOfdApplicationId());
                if (OfdStatus.ACCEPT.equals(ofdApplication.getOfdStatus()) ||
                        (OfdStatus.REJECT.equals(ofdApplication.getOfdStatus()) && ApplicationType.DEREGISTER.equals(ofdApplication.getApplicationType()))
                    //Bu degani ro'yxatdan chiqarishga ariza yuborilgan, lekin NIC rad etgan. Ro'yxatdan chiqarmagan. Kassa hali faol.
                ) {
                    rabbitMqSmartPosProducer.updateCustomer(fiscalization.getId());
                }
            }
        });

        return smartposClient.getCustomerByTin(customer.getTin());
    }

    @Override
    public boolean getDeviceStatusBySerialNumber(String serialNumber) {
        Device device = deviceRepository.findBySerialNumber(serialNumber);
        if (device == null) {
            return true;
        }

        return fiscalizationRepository
                .findFirstByDeviceIdAndStatusInAndDeletedIsFalse(device.getId(), List.of(DeviceStatus.FISCAL, DeviceStatus.FISCAL_ACTIVE))
                .isEmpty();
    }

    @Override
    public void removeDevice(SmartPosRemoveDevicePayload smartPosRemoveDevicePayload) {
        smartposClient.removeDevice(smartPosRemoveDevicePayload);
    }

    @Override
    public void syncCustomerBranch(Long customerBranchId) {
        CustomerBranch customerBranch = customerBranchRepository.findById(customerBranchId).orElseThrow(validationService.entityNotFoundExceptionThrow(BRANCH));

        User customerOwner = userRepository.findByLoginAndDeletedIsFalse(customerBranch.getCustomer().getPhone());
        if (customerOwner == null) {
            throw validationService.entityNotFoundExceptionThrow(USER).get();
        }


        CustomerEmployee customerEmployee = customerEmployeeRepository.findFirstByCustomerIdAndRole(customerBranch.getCustomerId(), Role.DIRECTOR).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE));
        SmartposOwnerDTO smartposOwnerDTO = new SmartposOwnerDTO();
        smartposOwnerDTO.setId(customerEmployee.getId());
        smartposOwnerDTO.setFirstName(customerEmployee.getFirstName());
        smartposOwnerDTO.setLastName(customerEmployee.getLastName());
        smartposOwnerDTO.setPatronymic(customerEmployee.getPatronymic());
        smartposOwnerDTO.setPhone(customerEmployee.getCustomer().getPhone());

        SmartposCustomerDetailDTO customerDetailDTO = new SmartposCustomerDetailDTO();
        customerDetailDTO.setId(customerBranch.getCustomerId());
        customerDetailDTO.setName(customerBranch.getCustomer().getCompanyName());
        customerDetailDTO.setTin(customerBranch.getCustomer().getTin());
        customerDetailDTO.setOldTin(customerBranch.getCustomer().getOldTin());
        customerDetailDTO.setAppType(customerBranch.getCustomer().getAppType().getCode());

        customerDetailDTO.setOwner(smartposOwnerDTO);
        if (customerBranch.getCustomer().getVat() != null) {
            customerDetailDTO.setVat(customerBranch.getCustomer().getVat().getPercent());
        }
        customerDetailDTO.setBusinessType(customerBranch.getCustomer().getBusinessType());
        customerDetailDTO.setActivityTypeCode(customerBranch.getCustomer().getActivityType().getCode());
        if (customerBranch.getCustomer().getActivityType().getParent() != null) {
            customerDetailDTO.setActivityTypeParentCode(customerBranch.getCustomer().getActivityType().getParent().getCode());
        }

        SmartPosAddressDTO customerAddress = new SmartPosAddressDTO();
        customerAddress.setHouse(customerBranch.getCustomer().getAddress().getHouse());
        customerAddress.setStreet(customerBranch.getCustomer().getAddress().getStreet());
        customerAddress.setApartment(customerBranch.getCustomer().getAddress().getApartment());
        if (customerBranch.getCustomer().getAddress().getRegion() != null) {
            customerAddress.setRegionCode(customerBranch.getCustomer().getAddress().getRegion().getCode());
        }
        if (customerBranch.getCustomer().getAddress().getDistrict() != null) {
            customerAddress.setDistrictCode(customerBranch.getCustomer().getAddress().getDistrict().getCode());
        }
        customerDetailDTO.setAddress(customerAddress);
        if (customerBranch.getCustomer().getManager() != null) {
            User manager = customerBranch.getCustomer().getManager();
            UserShortDTO managerDTO = new UserShortDTO();
            managerDTO.setId(manager.getId());
            managerDTO.setLogin(manager.getLogin());
            managerDTO.setLastName(manager.getLastName());
            managerDTO.setFirstName(manager.getFirstName());
            managerDTO.setPatronymic(manager.getPatronymic());
            customerDetailDTO.setManager(managerDTO);
        }

        SmartposSyncCustomerBranchDTO smartposSyncCustomerBranchDTO = new SmartposSyncCustomerBranchDTO();
        smartposSyncCustomerBranchDTO.setCustomer(customerDetailDTO);
        smartposSyncCustomerBranchDTO.setId(customerBranch.getId());
        smartposSyncCustomerBranchDTO.setName(customerBranch.getName());
        smartposSyncCustomerBranchDTO.setActivityTypeCode(customerBranch.getActivityType().getCode());
        if (customerBranch.getActivityType().getParent() != null) {
            smartposSyncCustomerBranchDTO.setActivityTypeParentCode(customerBranch.getActivityType().getParent().getCode());
        }

        if (Objects.nonNull(customerBranch.getOfdId()))
            smartposSyncCustomerBranchDTO.setOfdId(customerBranch.getOfdId());
        smartposSyncCustomerBranchDTO.setLatitude(customerBranch.getAddress().getLatitude());
        smartposSyncCustomerBranchDTO.setLongitude(customerBranch.getAddress().getLongitude());
        SmartPosAddressDTO smartPosAddressDTO = new SmartPosAddressDTO();
        smartPosAddressDTO.setHouse(customerBranch.getAddress().getHouse());
        smartPosAddressDTO.setStreet(customerBranch.getAddress().getStreet());
        smartPosAddressDTO.setApartment(customerBranch.getAddress().getApartment());
        if (customerBranch.getAddress().getRegion() != null) {
            smartPosAddressDTO.setRegionCode(customerBranch.getAddress().getRegion().getCode());
        }
        if (customerBranch.getAddress().getDistrict() != null) {
            smartPosAddressDTO.setDistrictCode(customerBranch.getAddress().getDistrict().getCode());
        }
        smartposSyncCustomerBranchDTO.setAddress(smartPosAddressDTO);
        smartposClient.syncCustomerBranch(smartposSyncCustomerBranchDTO);
    }

    @Override
    public void attachManager(List<String> tins, User user) {
        SmartposManagerDTO managerDTO = new SmartposManagerDTO();
        managerDTO.setTins(tins);
        managerDTO.setManagerLogin(user.getPhone());
        managerDTO.setManagerFirstName(user.getFirstName());
        managerDTO.setManagerLastName(user.getLastName());
        managerDTO.setManagerPatronymic(user.getPatronymic());
        smartposClient.attachManager(managerDTO);
    }
}
