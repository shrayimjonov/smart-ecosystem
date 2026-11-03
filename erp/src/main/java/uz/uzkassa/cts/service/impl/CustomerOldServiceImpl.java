package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.constants.SmsConstants;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBranchDevice;
import uz.uzkassa.cts.domain.RentDeviceAct;
import uz.uzkassa.cts.domain.Tariff;
import uz.uzkassa.cts.dto.apay.ApayCompanyEnableDTO;
import uz.uzkassa.cts.dto.auth.ActivateDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.cases.CaseSmartposAppTypeChange;
import uz.uzkassa.cts.dto.customer.*;
import uz.uzkassa.cts.dto.payload.CustomerPayload;
import uz.uzkassa.cts.dto.payload.Payload;
import uz.uzkassa.cts.dto.sms.EtcRequestDTO;
import uz.uzkassa.cts.dto.soliq.SoliqCompanyDTO;
import uz.uzkassa.cts.dto.sync.SyncCustomerListDTO;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.integrations.SmartposClient;
import uz.uzkassa.cts.integrations.SoliqClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqCustomerProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.RentDeviceActRepository;
import uz.uzkassa.cts.repository.UserRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Transactional
@Service
@Slf4j
public class CustomerOldServiceImpl extends BaseServiceImpl<Customer, Long> implements CustomerOldService {

    private final CustomerRepository customerRepository;
    private final SoliqClient soliqClient;
    private final SmartposClient smartposClient;
    private final RabbitMqDeviceSwitcherProducer rabbitMqDeviceSwitcherProducer;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final LauncherService launcherService;
    private final RabbitMqCustomerProducer rabbitMqCustomerProducer;
    private final SmsService smsService;
    private final BankTariffHistoryService bankTariffHistoryService;
    private final BankTariffService bankTariffService;
    private final RentDeviceActRepository rentDeviceActRepository;
    private final BillingClientService billingClientService;
    private final LedgerService ledgerService;

    public CustomerOldServiceImpl(CustomerRepository customerRepository,
                                  UserRepository userRepository,
                                  SoliqClient soliqClient,
                                  SmartposClient smartposClient,
                                  RabbitMqDeviceSwitcherProducer rabbitMqDeviceSwitcherProducer,
                                  CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                  LauncherService launcherService,
                                  RabbitMqCustomerProducer rabbitMqCustomerProducer,
                                  SmsService smsService,
                                  BankTariffHistoryService bankTariffHistoryService,
                                  BankTariffService bankTariffService,
                                  RentDeviceActRepository rentDeviceActRepository,
                                  BillingClientService billingClientService,
                                  LedgerService ledgerService) {
        super(customerRepository);
        this.customerRepository = customerRepository;
        this.billingClientService = billingClientService;
        this.ledgerService = ledgerService;
        this.userRepository = userRepository;
        this.soliqClient = soliqClient;
        this.smartposClient = smartposClient;
        this.rabbitMqDeviceSwitcherProducer = rabbitMqDeviceSwitcherProducer;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.launcherService = launcherService;
        this.rabbitMqCustomerProducer = rabbitMqCustomerProducer;
        this.smsService = smsService;
        this.bankTariffHistoryService = bankTariffHistoryService;
        this.bankTariffService = bankTariffService;
        this.rentDeviceActRepository = rentDeviceActRepository;
    }

    @Override
    public Page<CustomerShortDTO> listByEmployee(BaseFilter filter) {
        ResultList<CustomerShortDTO> resultList = customerRepository.getCustomerListByEmployee(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public void activate(ActivateDTO activateDTO) {

        Customer customer = customerRepository.findByPhone(activateDTO.getLogin()).orElse(null);
        if (customer == null) {
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();
        }
        customer.setStatus(CustomerStatus.ACTIVE);
        customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompanyCustomerListDTO> getAll(CustomerFilter filter) {
        if (SecurityUtils.isCurrentUserInRole(Role.CALL_CENTER.getRoleCode()) && (StringUtils.isEmpty(filter.getSearch()) || filter.getSearch().length() < 8)) {
            List<CompanyCustomerListDTO> result = new ArrayList<>();
            return new PageImpl<>(result, filter.getOrderedPageable(), 0);
        }
        boolean isOfdStatusSelected = StringUtils.isNotEmpty(filter.getOfdStatus()) && OfdStatus.get(filter.getOfdStatus()) != null && !OfdStatus.NOT_SEND.equals(OfdStatus.get(filter.getOfdStatus()));

        ResultList<CompanyCustomerListDTO> resultList = customerRepository.getCustomerListByEmployee(filter, isOfdStatusSelected);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<String> getTinListInterval(int start, int limit) {
        return customerRepository.getTinListInterval(start, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getPhoneNumberListInterval(int start, int limit) {
        return customerRepository.getPhoneNumberListInterval(start, limit);
    }

    @Override
    public void whiteList(WhiteListDTO whiteListDTO) {
        Customer customer = customerRepository.findById(whiteListDTO.getId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        customer.setWhiteList(whiteListDTO.isWhiteList());
        customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Long getListCountToSync() {
        if (!isSuperAdmin() && getCompanyId() != null)
            return customerRepository.countByCompanyId(getCompanyId());
        else
            return customerRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SyncCustomerListDTO> getListToSync(BaseFilter filter) {

        return new PageImpl<>
                (customerRepository.getCustomerListToSync(filter),
                        filter.getOrderedPageable(), 0L);
    }

    public void syncApay(ApayCompanyEnableDTO apayCompanyEnableDTO) {
        Customer customer = customerRepository.findByTin(apayCompanyEnableDTO.getTin());
        if (customer != null) {
            customer.setApayId(apayCompanyEnableDTO.getId());
            customerRepository.save(customer);
        }
    }

    @Override
    public void updateAppType(ChangeAppTypeRequestDto dto) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(dto.getTin(), dto.getTin());
        CustomerAppType appType = CustomerAppType.valueOf(dto.getAppType());
        customer.setAppType(appType);
        customerRepository.save(customer);

        List<CustomerBranchDevice> devices = customerBranchDeviceRepository.findLaucherBankTypeChangeByCustomerId(customer.getId());
        if (appType == CustomerAppType.CTS) {
            for (CustomerBranchDevice device : devices) {
                Tariff tariff = device.getTariff();
                if (tariff == null) {
                    launcherService.block(Collections.singletonList(device.getDevice().getSerialNumber()));
                } else if (tariff.getPeriodType() != PeriodType.YEARLY) {
                    device.setStartDate(LocalDate.now());
                    device.setDueDate(DateUtil.getMonthLastDate());
                }
            }
            customerBranchDeviceRepository.saveAll(devices);
            rabbitMqDeviceSwitcherProducer.paymentHistoryCreate(customer.getTin());
        }


        smartposClient.changeAppType(new CaseSmartposAppTypeChange(
                customer.getId(),
                customer.getTin(),
                customer.getOldTin(),
                customer.getAppType().getCode())
        );

        rabbitMqCustomerProducer.recalculation(new Payload(customer.getId()));
    }

    @Override
    public void updateAppType(Long id, String appType) {
        Customer customer = customerRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        ChangeAppTypeRequestDto dto = new ChangeAppTypeRequestDto();
        dto.setAppType(appType);
        dto.setTin(customer.getTin());
        updateAppType(dto);
    }

    @Override
    public void updateCustomerStatus(Long id, CustomerLegalStatus status) {
        Customer customer = findById(id);
        customer.setLegalStatus(status);
        customerRepository.save(customer);
    }

    @Override
    public void updateCustomerStatusByPayment(String tin) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(tin, tin);
        if (customer != null) {
            BigDecimal customerBalance = ledgerService.getCustomerBalanceByCustomerId(customer.getId());
            customer.setStatus(customerBalance.compareTo(BigDecimal.ZERO) >= 0 ? CustomerStatus.ACTIVE : CustomerStatus.DEBTOR);
            customer.setPaymentDate(LocalDate.now());
            customerRepository.save(customer);
        }
    }

    @Override
    public Page<String> getAllToSync(BaseFilter filter) {
        ResultList<String> resultList = customerRepository.getAllToSync(filter);
        return new PageImpl<>(resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional(readOnly = true)
    public SoliqCompanyDTO check(String tin) {
        return soliqClient.getCustomerByTin(tin);
    }

    @Override
    public void syncTax(CustomerPayload payload) {
        SoliqCompanyDTO soliqCompanyDTO = soliqClient.getCustomerByTin(payload.getTin());
        if (soliqCompanyDTO != null && StringUtils.isNotEmpty(soliqCompanyDTO.getTin()) && StringUtils.isNotEmpty(soliqCompanyDTO.getPersonalNum())) {
            Customer customer = customerRepository.findByTin(payload.getTin());
            customer.setOldTin(soliqCompanyDTO.getTin());
            save(customer);
        }
    }

    @Override
    public void syncByCustomerTin() {
        customerRepository.getTinsOldTinNull()
                .forEach(tin -> rabbitMqCustomerProducer.syncCustomerTin(new CustomerPayload(tin)));
    }

    @Override
    public void updateAPayTariff(UpdateCustomerAPayTariffDTO dto) {
        Customer customer = customerRepository.findById(dto.customerId()).orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        customer.setApayTariffId(dto.tariffId());
        customerRepository.save(customer);
    }

    @Override
    public void sendPublicOffer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        customer.setOfferActivationCode(RandomStringUtils.randomNumeric(6));
        customerRepository.save(customer);
        smsService.send(new EtcRequestDTO(customer.getPhone(), String.format(SmsConstants.PUBLIC_OFFER, customer.getOfferActivationCode())));
    }

    @Override
    public void verifyPublicOffer(PublicOfferVerifyRequestDto dto) {
        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(CUSTOMER));
        if (!Objects.equals(customer.getOfferActivationCode(), dto.code())) {
            throw validationService.badRequestExceptionThrow(INCORRECT_ACTIVATION_CODE).get();
        }
        customer.setOfferActivationDate(LocalDateTime.now());
        customerRepository.save(customer);
        billingClientService.createCustomer(customer);
    }

    @Override
    public void attachmentBankTariff(Long id, String serialNumber) {
        Customer customer = findById(id);
        if (StringUtils.isNotEmpty(serialNumber)) {
            Long bankTariffId = bankTariffService.getId(customer.getAppType(), customer.getTin(), serialNumber);
            if (bankTariffId != null) {
                customer.setBankTariffId(bankTariffId);
                save(customer);
                bankTariffHistoryService.create(customer.getId(), bankTariffId);
            }
        } else {
            Long bankTariffId = bankTariffService.getId(customer.getTin());
            if (bankTariffId != null) {
                customer.setBankTariffId(bankTariffId);
                save(customer);
                bankTariffHistoryService.create(customer.getId(), bankTariffId);
            }
        }
    }

    @Override
    public CustomerResponseDTO customerStatus(String tin) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(tin, tin);
        if (customer != null) {
            CustomerResponseDTO responseDTO = new CustomerResponseDTO();
            responseDTO.setBankTariff(customer.getBankTariffId() != null);
            List<RentDeviceAct> rentDeviceActList = rentDeviceActRepository.findByCustomerIdAndStatusIn(customer.getId(), List.of(ContractStatus.ACCEPTED));
            responseDTO.setRentDevice(CollectionUtils.isNotEmpty(rentDeviceActList));
            return responseDTO;
        }
        return new CustomerResponseDTO();
    }
}
