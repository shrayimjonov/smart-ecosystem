package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.domain.BankClientDetail;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.domain.CustomerBank;
import uz.uzkassa.cts.domain.KapitalbankPayment;
import uz.uzkassa.cts.dto.kapitalbank.request.*;
import uz.uzkassa.cts.dto.kapitalbank.response.*;
import uz.uzkassa.cts.dto.ledger.response.CurrentStatementResponseDto;
import uz.uzkassa.cts.enums.KapitalbankPaymentStatus;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.KapitalbankPaymentRepository;
import uz.uzkassa.cts.service.KapitalBankService;
import uz.uzkassa.cts.service.LauncherService;
import uz.uzkassa.cts.service.LedgerService;
import uz.uzkassa.cts.service.ValidationService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.validator.LocalizationMessage;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static uz.uzkassa.cts.security.SecurityUtils.getHeader;

@Service
@Slf4j
public class KapitalBankServiceImpl implements KapitalBankService, MessageConstants {

    final ApplicationProperties applicationProperties;
    private final RestTemplate restTemplate;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final LedgerService ledgerService;
    private final KapitalbankPaymentRepository kapitalbankPaymentRepository;
    private final RabbitMqDeviceSwitcherProducer rabbitMqDeviceSwitcherProducer;
    private final LauncherService launcherService;
    private final CustomerRepository customerRepository;
    private final ValidationService validationService;
    private final LocalizationMessage localizationMessage;

    public KapitalBankServiceImpl(ApplicationProperties applicationProperties,
                                  RestTemplate restTemplate,
                                  CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                  LedgerService ledgerService,
                                  KapitalbankPaymentRepository kapitalbankPaymentRepository,
                                  RabbitMqDeviceSwitcherProducer rabbitMqDeviceSwitcherProducer,
                                  LauncherService launcherService, CustomerRepository customerRepository, ValidationService validationService, LocalizationMessage localizationMessage) {
        this.applicationProperties = applicationProperties;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.ledgerService = ledgerService;
        this.kapitalbankPaymentRepository = kapitalbankPaymentRepository;
        this.rabbitMqDeviceSwitcherProducer = rabbitMqDeviceSwitcherProducer;
        this.launcherService = launcherService;
        this.restTemplate = restTemplate;
        this.customerRepository = customerRepository;
        this.validationService = validationService;
        this.localizationMessage = localizationMessage;
    }

    @Override
    public TransactionCreateResponseDto createTransaction(TransactionCreateRequestDto dto) {
        requestLog(applicationProperties.getKapitalBankProperty().getCreateTransaction(), HttpMethod.POST, dto.toString());
        dto.setCashId(applicationProperties.getKapitalBankProperty().getCashId());

        HttpEntity<TransactionCreateRequestDto> entity = new HttpEntity<>(dto, createHeader());
        ResponseEntity<TransactionCreateResponseDto> response = restTemplate.exchange(
                applicationProperties.getKapitalBankProperty().getCreateTransaction(), HttpMethod.POST, entity, TransactionCreateResponseDto.class);
        responseLog(HttpStatus.valueOf(response.getStatusCode().value()), response.getHeaders(), response.getBody().toString());

        if (response.getStatusCode() != HttpStatus.OK)
            throw new IntegrationException("Kapitalbank create transaction exception: " + response.getStatusCode());

        return response.getBody();
    }


    private HttpHeaders createHeader() {
        HttpHeaders headers = getHeader();
        headers.setBasicAuth(applicationProperties.getKapitalBankProperty().getUsername(), applicationProperties.getKapitalBankProperty().getPassword());
        return headers;
    }

    private void requestLog(String uri, HttpMethod method, String dtoToString) {
        String builder = System.lineSeparator() +
                "===============================Kapitalbank  request=================================" +
                System.lineSeparator() +
                "URI         : " + uri +
                System.lineSeparator() +
                "Method      : " + method.toString() +
                System.lineSeparator() +
                "Request body: " + dtoToString +
                System.lineSeparator() +
                "==================================request end=======================================";
        log.info(builder);
    }

    private void responseLog(HttpStatus statusCode, HttpHeaders headers, String responseToString) {
        String builder = System.lineSeparator() +
                "===============================Kapitalbank response=================================" +
                System.lineSeparator() +
                "Status code  : " + statusCode +
                System.lineSeparator() +
                "Headers      : " + headers +
                System.lineSeparator() +
                "Response body: " + responseToString +
                System.lineSeparator() +
                "==================================response end=======================================";
        log.info(builder);
    }


    @Override
    public PayResponseDto pay(PayRequestDto dto) {
        requestLog(applicationProperties.getKapitalBankProperty().getPay(), HttpMethod.POST, dto.toString());
        dto.setCashId(applicationProperties.getKapitalBankProperty().getCashId());

        HttpEntity<PayRequestDto> entity = new HttpEntity<>(dto, createHeader());
        ResponseEntity<PayResponseDto> response =
                restTemplate.exchange(applicationProperties.getKapitalBankProperty().getPay(), HttpMethod.POST,
                        entity, PayResponseDto.class);

        responseLog(HttpStatus.valueOf(response.getStatusCode().value()), response.getHeaders(), response.getBody().toString());

        if (response.getStatusCode() != HttpStatus.OK)
            throw new IntegrationException("Kapitalbank pay exception: " + response.getStatusCode());

        return response.getBody();
    }

    @Override
    public InfoResponseDto getInfo(InfoRequestDto dto) {
        String url = applicationProperties.getKapitalBankProperty().getInfo() + "?pan=" + dto.getPan() + "&expiry=" + dto.getExpiry();
        requestLog(url, HttpMethod.GET, dto.toString());

        HttpEntity<InfoRequestDto> entity = new HttpEntity<>(createHeader());
        ResponseEntity<InfoResponseDto> response = restTemplate.exchange(url,
                HttpMethod.GET, entity, InfoResponseDto.class);

        responseLog(HttpStatus.valueOf(response.getStatusCode().value()), response.getHeaders(), response.getBody().toString());

        if (response.getStatusCode() != HttpStatus.OK)
            throw new IntegrationException("Kapitalbank get info exception: " + response.getStatusCode());

        return response.getBody();
    }

    @Override
    public CheckStatusResponseDto check(CheckStatusRequestDto dto) {
        requestLog(applicationProperties.getKapitalBankProperty().getCheck(), HttpMethod.POST, dto.toString());
        dto.setCashId(applicationProperties.getKapitalBankProperty().getCashId());
        HttpEntity<CheckStatusRequestDto> entity = new HttpEntity<>(dto, createHeader());
        ResponseEntity<CheckStatusResponseDto> response = restTemplate.exchange(applicationProperties.getKapitalBankProperty().getCheck(),
                HttpMethod.POST, entity, CheckStatusResponseDto.class);

        responseLog(HttpStatus.valueOf(response.getStatusCode().value()), response.getHeaders(), response.getBody().toString());

        if (response.getStatusCode() != HttpStatus.OK)
            throw new IntegrationException("Kapitalbank check exception: " + response.getStatusCode());

        return response.getBody();
    }

    @Override
    public CustomerCheckResponseDto getClientInfoByDeviceSerial(String serialNumber) {
        Optional<Customer> optionalCustomer = customerBranchDeviceRepository.findOneByDeviceSerialNumber(serialNumber);
        if (optionalCustomer.isEmpty())
            throw validationService.entityNotFoundExceptionThrowWithCode(DEVICE_NOT_REGISTER, DEVICE, serialNumber).get();

        Customer customer = optionalCustomer.get();

        return getCustomerBalance(customer);
    }

    @Override
    public TransactionCreateResponseDto createPayment(CreatePaymentRequestDto dto) {
        Optional<Customer> optionalCustomer = customerBranchDeviceRepository.findOneByDeviceSerialNumber(dto.getDeviceSerialNumber());
        if (optionalCustomer.isEmpty())
            throw validationService.entityNotFoundExceptionThrow(DEVICE, SERIAL_NUMBER, dto.getDeviceSerialNumber()).get();

        Customer customer = optionalCustomer.get();
        if (!dto.getTin().equals(customer.getTin()))
            throw new BadRequestException(localizationMessage.localize("this.device.is.not.belongs.to.this.customer", new Object[]{dto.getDeviceSerialNumber(), customer.getName(), customer.getTin()}));

        TransactionCreateRequestDto transactionCreateRequestDto = new TransactionCreateRequestDto();
        transactionCreateRequestDto.setCashId(applicationProperties.getKapitalBankProperty().getCashId());
        transactionCreateRequestDto.setAmount(convertToInteger(dto.getAmount()));
        transactionCreateRequestDto.setExpireDate(dto.getExpiry());
        transactionCreateRequestDto.setPan(dto.getPan());
        log.info("create payment TransactionCreateRequestDto : {}", transactionCreateRequestDto);
        TransactionCreateResponseDto responseDto = createTransaction(transactionCreateRequestDto);


        KapitalbankPayment payment = new KapitalbankPayment();
        payment.setDeviceSerialNumber(dto.getDeviceSerialNumber());
        payment.setTin(dto.getTin());
        payment.setCompanyName(customer.getCompanyName());
        CustomerBank bank = customer.getCustomerBank();
        payment.setAccountNumber(bank.getAccountNumber());
        payment.setBankCode(customer.getOked());
        payment.setBankName(bank.getBank().getNameNotNullable());
        payment.setPan(dto.getPan());
        payment.setExpiry(dto.getExpiry());
        payment.setAmount(dto.getAmount());
        payment.setKapitalbankPaymentStatus(KapitalbankPaymentStatus.CREATED);

        payment.setErrorMessage(responseDto.getErrorMessage());
        payment.setStatus(responseDto.getStatus());
        payment.setTimestamp(DateUtil.longToLocalDateTime(responseDto.getTimestamp()));
        if (responseDto.getData() != null) {
            payment.setMaskedPhoneNumber(responseDto.getData().getMaskedPhoneNumber());
            payment.setTransactionId(responseDto.getData().getId());
        }
        kapitalbankPaymentRepository.save(payment);

        return responseDto;
    }

    @Override
    public void acceptPayment(PayRequestDto dto) {
        log.info("Received message kapitalbank accept payment: {}", dto.toString());

        Optional<KapitalbankPayment> optionalPayment = kapitalbankPaymentRepository.findByTransactionId(dto.getTransactionId());
        if (optionalPayment.isEmpty()) {
            throw validationService.entityNotFoundExceptionThrow(PAYMENT, ID, dto.getTransactionId().toString()).get();
        }

        KapitalbankPayment payment = optionalPayment.get();
        try {
            PayResponseDto responseDto = pay(dto);
            payment.setPayData(responseDto.getData());
            payment.setPayErrorMessage(responseDto.getErrorMessage());
            payment.setPayTimestamp(DateUtil.longToLocalDateTime(responseDto.getTimestamp()));
            payment.setPayStatus(responseDto.getStatus());
            payment.setKapitalbankPaymentStatus(KapitalbankPaymentStatus.WAITING_FOR_APPROVAL);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            payment.setPayErrorMessage("there are exception");
            payment.setPayTimestamp(LocalDateTime.now());
            payment.setPayStatus("OK");
            payment.setKapitalbankPaymentStatus(KapitalbankPaymentStatus.WAITING_FOR_APPROVAL);
        }

        kapitalbankPaymentRepository.save(payment);
    }

    private Long convertToInteger(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkPaymentStatus(KapitalbankPayment payment) {

        log.info("Kapitalbank checkPaymentStatus get payment for check status: {}", payment.toString());
        //CHECK STATUS SEND REQUEST TO KAPITALBANK
        CheckStatusRequestDto requestDto = new CheckStatusRequestDto();
        requestDto.setTransactionId(payment.getTransactionId());
        CheckStatusResponseDto responseDto = check(requestDto);
        log.info("Kapitalbank check status response: {}", responseDto.toString());

        payment.setCheckData(responseDto.getData());
        payment.setCheckErrorMessage(responseDto.getErrorMessage());
        payment.setCheckStatus(responseDto.getStatus());
        payment.setCheckTimestamp(DateUtil.longToLocalDateTime(responseDto.getTimestamp()));

        //AUTH_FAILED
        if (responseDto.getData().equalsIgnoreCase("success")) {
            BankClientDetail detail = new BankClientDetail();
            detail.setPaymentDate(payment.getLastModifiedDate().toLocalDate());
            detail.setPayerAccountNumber(payment.getAccountNumber());
            detail.setPayerName(payment.getCompanyName());
            detail.setPayerBankCode(payment.getBankCode());
            detail.setPayerBankName(payment.getBankName());
            detail.setPayerIdentityNumber(payment.getTin());

            detail.setDocumentNo(payment.getTransactionId().toString());
            detail.setTransactionNumber(payment.getId().toString());
            detail.setAmount(payment.getAmount());
            detail.setDescription("Payment made by device:" + payment.getDeviceSerialNumber());
            detail.setServiceType(ServiceType.SERVICE);

            String BANK_SRVC = "BANK_SRVC";
            detail.setReceiverIdentityNumber(BANK_SRVC);
            detail.setReceiverAccountNumber("20208000905065950001");
            detail.setReceiverName("ООО \"SPECIAL TEXNO SOFT\"");
            detail.setReceiverBankCode("01088");
            detail.setReceiverBankName("Головной Офис");
            detail.setApproved(false);

            //UPDATE PAYMENT STATUS
            payment.setKapitalbankPaymentStatus(KapitalbankPaymentStatus.CONFIRMED);
            kapitalbankPaymentRepository.save(payment);
            log.info("KapitalbankPaymentStatus changed to: {}", KapitalbankPaymentStatus.CONFIRMED);

            ledgerService.importFromJournalDetail(detail);
            log.info("Import to Ledger is success");
            //Switch device
            rabbitMqDeviceSwitcherProducer.deviceStatusSwitch(payment.getTin());
            log.info("Device switch query send");
            sendNotificationToLauncher("Оплата прошла успешно. Сумма: " + payment.getAmount() + " сум", payment.getDeviceSerialNumber());

        } else if (responseDto.getData().equalsIgnoreCase("auth_failed")) {
            log.info("KapitalBank check request get response auth_failed");
            sendNotificationToLauncher("Оплата прошла не успешно.", payment.getDeviceSerialNumber());
            payment.setKapitalbankPaymentStatus(KapitalbankPaymentStatus.REJECTED);
        } else if (responseDto.getData().equalsIgnoreCase("reversed")) {
            log.info("KapitalBank check request get response REVERSED");
            sendNotificationToLauncher("Платеж был отменен.", payment.getDeviceSerialNumber());
            payment.setKapitalbankPaymentStatus(KapitalbankPaymentStatus.REJECTED);
        }
        kapitalbankPaymentRepository.save(payment);
    }

    @Override
    public CustomerCheckResponseDto getClientInfoByTin(String tin) {
        Customer customer = customerRepository.findByTin(tin);
        if (customer == null)
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER, TIN, tin).get();

        return getCustomerBalance(customer);
    }

    private CustomerCheckResponseDto getCustomerBalance(Customer customer) {
        Long devicesCount = customerBranchDeviceRepository.countByCustomerId(customer.getId());

        CurrentStatementResponseDto statementResponseDto = ledgerService.getNeedAmountAndStatement(customer.getTin(), ServiceType.SERVICE);

        CustomerCheckResponseDto responseDto = new CustomerCheckResponseDto();
        responseDto.setCustomerName(customer.getCompanyName());
        responseDto.setDeviceCount(devicesCount);
        responseDto.setTin(customer.getTin());
        responseDto.setBalance(statementResponseDto.getBalance());
        responseDto.setNeedAmount(statementResponseDto.getNeedAmount());
        responseDto.setCustomerAppType(customer.getAppType());
        return responseDto;
    }

    private void sendNotificationToLauncher(String content, String deviceSerialNumber) {
        log.info(">>>>>>>>>> Start Sending Notification To Launcher <<<<<<<<<<<<");
        launcherService.sendNotification(content, deviceSerialNumber);
        log.info(">>>>>>>>>> End Sending Notification To Launcher <<<<<<<<<<<<");
    }

}
