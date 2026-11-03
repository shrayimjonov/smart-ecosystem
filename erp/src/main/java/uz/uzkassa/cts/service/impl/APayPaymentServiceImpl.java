package uz.uzkassa.cts.service.impl;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.apay.*;
import uz.uzkassa.cts.dto.apaypayment.request.*;
import uz.uzkassa.cts.dto.apaypayment.response.APayPaymentResponseDto;
import uz.uzkassa.cts.dto.onetimeorderinvoice.response.OneTimeOrderInvoice1cResponse;
import uz.uzkassa.cts.enums.CustomerAppType;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.enums.apay.APayPaymentStatus;
import uz.uzkassa.cts.enums.apay.APayStatus;
import uz.uzkassa.cts.filter.APayPaymentFilter;
import uz.uzkassa.cts.mappings.APayPaymentMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqDeviceSwitcherProducer;
import uz.uzkassa.cts.repository.APayPaymentRepository;
import uz.uzkassa.cts.repository.CustomerBranchDeviceRepository;
import uz.uzkassa.cts.repository.CustomerRepository;
import uz.uzkassa.cts.repository.PanBlackListRepository;
import uz.uzkassa.cts.service.APayPaymentService;
import uz.uzkassa.cts.service.APayService;
import uz.uzkassa.cts.service.LedgerService;
import uz.uzkassa.cts.utils.InvoiceCreateExcelUtil;
import uz.uzkassa.cts.web.rest.errors.ErrorDTO;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class APayPaymentServiceImpl extends BaseServiceImpl<APayPayment, Long> implements APayPaymentService {

    private final APayPaymentRepository repository;
    private final CustomerBranchDeviceRepository customerBranchDeviceRepository;
    private final APayService aPayService;
    private final LedgerService ledgerService;
    private final CustomerRepository customerRepository;
    private final PanBlackListRepository panBlackListRepository;
    private final APayPaymentMapper aPayPaymentMapper;
    private final RabbitMqDeviceSwitcherProducer rabbitMqDeviceSwitcherProducer;
    private static final String FROM_WEB = "FROM_WEB";

    public APayPaymentServiceImpl(APayPaymentRepository repository,
                                  CustomerBranchDeviceRepository customerBranchDeviceRepository,
                                  APayService aPayService,
                                  LedgerService ledgerService,
                                  CustomerRepository customerRepository,
                                  PanBlackListRepository panBlackListRepository,
                                  APayPaymentMapper aPayPaymentMapper,
                                  RabbitMqDeviceSwitcherProducer rabbitMqDeviceSwitcherProducer) {
        super(repository);
        this.repository = repository;
        this.customerBranchDeviceRepository = customerBranchDeviceRepository;
        this.aPayService = aPayService;
        this.ledgerService = ledgerService;
        this.customerRepository = customerRepository;
        this.panBlackListRepository = panBlackListRepository;
        this.aPayPaymentMapper = aPayPaymentMapper;
        this.rabbitMqDeviceSwitcherProducer = rabbitMqDeviceSwitcherProducer;
    }

    @Override
    public APayAddTransactionResponseDto createTransaction(APayPaymentAddRequestDto dto) {
        Customer customer;
        if (!dto.getDeviceSerialNumber().equals(FROM_WEB)) {
            Optional<Customer> optionalCustomer = customerBranchDeviceRepository.findOneByDeviceSerialNumber(dto.getDeviceSerialNumber());
            if (optionalCustomer.isEmpty()) {
                throw validationService.entityNotFoundExceptionThrow(DEVICE, SERIAL_NUMBER, dto.getDeviceSerialNumber()).get();
            }
            customer = optionalCustomer.get();
            if (!dto.getTin().equals(customer.getTin()))
                throw validationService.badRequestExceptionThrow(DEVICE_NOT_BELONGS, dto.getDeviceSerialNumber(), customer.getName(), customer.getTin()).get();
        } else {
            customer = customerRepository.findByTin(dto.getTin());
            if (customer == null) {
                throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();
            }
            dto.setDeviceSerialNumber(dto.getDeviceSerialNumber() + " TIN/PINFL " + dto.getTin());
        }
        if (customer.getAppType() == CustomerAppType.SMARTPOS) {
            throw validationService.badRequestExceptionThrow(PAYMENT_VIA_SMARTPOS).get();
        }

        Optional<PanBlackList> panBlackList = panBlackListRepository.findByPan(dto.getPan());
        if (panBlackList.isPresent()) {
            throw validationService.badRequestExceptionThrow(INVALID_INPUT, CARD).get();
        }

        APayPayment payment = new APayPayment();
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
        payment.setAPayPaymentStatus(APayPaymentStatus.CREATED);
        try {
            APayAddTransactionRequestDto requestDto = new APayAddTransactionRequestDto();
            requestDto.setAmount(dto.getAmount());
            requestDto.setPan(dto.getPan());
            requestDto.setExpiry(dto.getExpiry());
            requestDto.setTin(dto.getTin());

            APayAddTransactionResponseDto responseDto = aPayService.add(requestDto);
            payment.setBillId(responseDto.getId());
            payment.setMaskedPhoneNumber(responseDto.getMaskedPhoneNumber());

            repository.save(payment);
            responseDto.setLocalId(payment.getId().intValue());
            return responseDto;
        } catch (Exception e) {
            log.error("========================= APAY TRANSACTION ERROR =====================");
            log.error(e.getMessage(), e);

            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setTitle("A-pay create transaction");
            errorDTO.setMessage(e.getMessage());
            errorDTO.setStatus(400);
            throw new IntegrationException(e.getMessage(), errorDTO, HttpStatus.BAD_REQUEST);
        } finally {
            repository.save(payment);
        }
    }

    @Override
    public void acceptPayment(APayPaymentPayRequestDto dto) {
        Optional<APayPayment> optionalPayment = repository.findById(dto.getTransactionId().longValue());
        if (optionalPayment.isEmpty()) {
            throw validationService.entityNotFoundExceptionThrow(PAYMENT, BILL, dto.getBillId()).get();
        }

        APayPayment payment = optionalPayment.get();
        try {
            APayPayResponseDto responseDto = aPayService.pay(new APayPayRequestDto(payment.getBillId(), dto.getConfirmationKey()));
            payment.setPayStatus(responseDto.getStatus());
            payment.setAPayPaymentStatus(APayPaymentStatus.WAITING_FOR_APPROVAL);
            repository.save(payment);
        } catch (Exception e) {
            log.error(e.getMessage());
            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setTitle("A-pay confirmation");
            errorDTO.setStatus(400);
            errorDTO.setMessage(e.getMessage());
            throw new IntegrationException(e.getMessage(), errorDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional
    public void checkPayment(APayPayment payment) {
        log.info("apay checkPaymentStatus: {}", payment.toString());
        try {
            if (StringUtils.isEmpty(payment.getBillId())) {
                log.error("bill_id is null: {}", payment);
                return;
            }
            //CHECK STATUS SEND REQUEST TO KAPITALBANK
            APayCheckResponseDto responseDto = aPayService.check(new APayCheckRequestDto(payment.getBillId()));

            payment.setCheckStatus(responseDto.getStatus());
            payment.setCheckMessage(responseDto.getMessage());
            log.info("apay check status response: {}", responseDto);

            if (APayStatus.PAID == responseDto.getStatus()) {
                BankClientDetail detail = new BankClientDetail();
                detail.setPaymentDate(payment.getLastModifiedDate().toLocalDate());
                detail.setPayerAccountNumber(payment.getAccountNumber());
                detail.setPayerName(payment.getCompanyName());
                detail.setPayerBankCode(payment.getBankCode());
                detail.setPayerBankName(payment.getBankName());
                detail.setPayerIdentityNumber(payment.getTin());

                detail.setDocumentNo(payment.getBillId());
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
                detail.setSpecificationNumber(payment.getBillId());
                //UPDATE PAYMENT STATUS
                payment.setAPayPaymentStatus(APayPaymentStatus.CONFIRMED);

                ledgerService.importFromJournalDetail(detail);
                rabbitMqDeviceSwitcherProducer.deviceStatusSwitch(payment.getTin());

            } else if (APayStatus.CANCELED == responseDto.getStatus()) {
                payment.setAPayPaymentStatus(APayPaymentStatus.CANCELED);
            }
            save(payment);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    @Override
    public Page<APayPaymentResponseDto> list(APayPaymentFilter filter) {
        return repository.findAll(
                        Specification.where(getAPayPaymentSpecification(filter))
                                .and(getCreatedDate(filter.getFrom(), filter.getTo())), filter.getOrderedPageable())
                .map(aPayPaymentMapper::toDto);
    }

    private Specification<APayPayment> getAPayPaymentSpecification(APayPaymentFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                list.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(APayPayment_.BILL_ID)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(APayPayment_.TIN)), filter.getSearchForQuery()),
                        criteriaBuilder.like(root.get(APayPayment_.PAN), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(APayPayment_.COMPANY_NAME)), filter.getSearchForQuery()),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(APayPayment_.DEVICE_SERIAL_NUMBER)), filter.getSearchForQuery())));
            }

            if (filter.getAmount() != null) {
                list.add(criteriaBuilder.equal(root.get(APayPayment_.AMOUNT), filter.getId()));
            }
            if (filter.getId() != null) {
                list.add(criteriaBuilder.equal(root.get(APayPayment_.ID), filter.getId()));
            }
            if (filter.getAPayPaymentStatus() != null) {
                list.add(criteriaBuilder.equal(root.get(APayPayment_.A_PAY_PAYMENT_STATUS), filter.getAPayPaymentStatus()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public byte[] download(APayPaymentFilter filter) {
        List<APayPayment> aPayPayments = repository.findAll(getAPayPaymentSpecification(filter));
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("APayPayments");
            String[] headerNames = new String[]{"id", "ИНН", "серийный номер устройства", "Название компании",
                    "расчетный счет", "код банка", "Банк", "номер карты", "истечение срока карты", "сумма", "BillId транзакции",
                    "замаскированный номер телефона", "сообщение об ошибке", "статус", "Время создания", "payErrorMessage",
                    "статус оплаты", "Время оплаты", "статус у нас"};
            InvoiceCreateExcelUtil.createHeader(headerNames, sheet);
            InvoiceCreateExcelUtil.createAPayPayments(aPayPayments, sheet);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error occurred while creating excel file", e);
        }
    }

    @Override
    public BigDecimal getConfirmedAmount(APayPaymentFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        query.distinct(true);
        Root<APayPayment> root = query.from(APayPayment.class);

        ArrayList<Predicate> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(filter.getSearch())) {
            Path<String> billId = root.get(APayPayment_.BILL_ID);
            Path<String> pan = root.get(APayPayment_.PAN);
            Path<String> tin = root.get(APayPayment_.TIN);
            Path<String> companyName = root.get(APayPayment_.COMPANY_NAME);

            Predicate billIdPred = cb.like(cb.lower(billId), filter.getSearchForQuery());
            Predicate panPred = cb.like(pan, filter.getSearchForQuery());
            Predicate tinPred = cb.like(cb.lower(tin), filter.getSearchForQuery());
            Predicate companyNamePred = cb.like(cb.lower(companyName), filter.getSearchForQuery());

            list.add(cb.or(billIdPred, tinPred, panPred, companyNamePred));
        }
        if (filter.getFrom() != null && filter.getTo() != null) {
            Path<LocalDateTime> createDate = root.get(APayPayment_.CREATED_DATE);
            list.add(cb.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
        }

        if (filter.getId() != null) {
            Path<Long> id = root.get(APayPayment_.ID);
            list.add(cb.equal(id, filter.getId()));
        }

        if (filter.getAPayPaymentStatus() != null) {
            Path<APayPaymentStatus> status = root.get(APayPayment_.A_PAY_PAYMENT_STATUS);
            list.add(cb.equal(status, filter.getAPayPaymentStatus()));
        }
        query.where(cb.and(list.toArray(new Predicate[0])));
        query.select(cb.sum(root.get(APayPayment_.AMOUNT)));
        TypedQuery<BigDecimal> typedQuery = entityManager.createQuery(query);

        return typedQuery.getSingleResult();
    }

    @Override
    public Page<OneTimeOrderInvoice1cResponse> list1c(APayPaymentFilter filter) {
        Specification<APayPayment> paymentSpecification = getAPayPaymentSpecification(filter);
        return repository.findAll(
                Specification.where(paymentSpecification)
                        .and(getConfirmed()
                                .and(getLastModifiedDate(filter.getFrom(), filter.getTo()))),
                filter.getOrderedPageable()).map(aPayPaymentMapper::to1cDto);
    }

    @Override
    public APayPaymentResponseDto changeStatus(APayChangeStatusRequestDto dto) {
        APayPayment payment = findById(dto.getId());

        if (APayPaymentStatus.CONFIRMED == payment.getAPayPaymentStatus() ||
                APayPaymentStatus.WAITING_FOR_APPROVAL == payment.getAPayPaymentStatus() ||
                APayPaymentStatus.REJECTED == payment.getAPayPaymentStatus()) {
            throw validationService.badRequestExceptionThrow(STATUS_CANNOT_CHANGE, payment.getAPayPaymentStatus().getNameUzCyrillic()).get();
        }
        if (APayPaymentStatus.CREATED == payment.getAPayPaymentStatus() &&
                StringUtils.isEmpty(payment.getBillId())) {
            throw validationService.badRequestExceptionThrow(SMS_NOT_SENT).get();
        }
        payment.setAPayPaymentStatus(dto.getStatus());
        save(payment);
        return aPayPaymentMapper.toDto(payment);
    }

    public static Specification<APayPayment> getConfirmed() {
        return (root, query, cb) -> cb.equal(root.get(APayPayment_.A_PAY_PAYMENT_STATUS), APayPaymentStatus.CONFIRMED);
    }

    public static Specification<APayPayment> getCreatedDate(LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            return (root, query, cb) -> cb.between(root.get(APayPayment_.CREATED_DATE), from.atStartOfDay(), to.atTime(LocalTime.MAX));
        }
        return null;
    }

    public static Specification<APayPayment> getLastModifiedDate(LocalDate from, LocalDate to) {
        if (from != null && to != null) {
            return (root, query, cb) -> cb.between(root.get(APayPayment_.LAST_MODIFIED_DATE), from.atStartOfDay(), to.atTime(LocalTime.MAX));
        }
        return null;
    }

    @Transactional
    public APayAddTransactionResponseDto createTransactionFromApp(APayFromAppRequestDto dto) {
        log.info("Create Transaction From App: {}", dto);
        Customer customer = customerRepository.findByTin(dto.getTin());
        if (customer == null) {
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();
        }
        dto.setDeviceSerialNumber("APAY APPLICATION TIN/PINFL " + dto.getTin());

        APayPayment payment = repository.findFirstByBillId(dto.getBillId()).orElse(new APayPayment());
        if (APayPaymentStatus.CONFIRMED == payment.getAPayPaymentStatus()) {
            return null;
        }
        if (payment.getId() != null && APayPaymentStatus.CREATED == payment.getAPayPaymentStatus()) {
            payment.setAPayPaymentStatus(APayPaymentStatus.WAITING_FOR_APPROVAL);
        } else if (payment.getId() == null) {
            payment.setAPayPaymentStatus(APayPaymentStatus.WAITING_FOR_APPROVAL);
            payment.setDeviceSerialNumber(dto.getDeviceSerialNumber());
        }
        payment.setTin(dto.getTin());
        payment.setCompanyName(customer.getCompanyName());
        CustomerBank bank = customer.getCustomerBank();
        payment.setAccountNumber(bank.getAccountNumber());
        payment.setBankCode(customer.getOked());
        payment.setBankName(bank.getBank().getNameNotNullable());
        payment.setPan(dto.getPan());
        payment.setExpiry(dto.getExpiry());
        payment.setAmount(dto.getAmount());
        payment.setBillId(dto.getBillId());
        payment.setMaskedPhoneNumber(dto.getMaskedPhoneNumber());
        payment.setPayStatus(dto.getPayStatus());
        repository.saveAndFlush(payment);

        APayAddTransactionResponseDto res = new APayAddTransactionResponseDto();
        res.setId(payment.getBillId());
        res.setStatus(dto.getPayStatus());
        res.setLocalId(payment.getId().intValue());
        res.setMaskedPhoneNumber(dto.getMaskedPhoneNumber());
        return res;
    }

    @Transactional
    public APayAddTransactionResponseDto createQRTransaction(APayPaymentQRAddRequestDto dto) {
        Customer customer = customerRepository.findFirstByTinOrOldTin(dto.getTin(), dto.getTin());
        if (customer == null) {
            throw validationService.entityNotFoundExceptionThrow(CUSTOMER).get();
        }
        APayPayment payment = new APayPayment();
        payment.setTin(dto.getTin());
        payment.setCompanyName(customer.getCompanyName());
        CustomerBank bank = customer.getCustomerBank();
        payment.setAccountNumber(bank.getAccountNumber());
        payment.setBankCode(customer.getOked());
        payment.setBankName(bank.getBank().getNameNotNullable());
        payment.setAmount(dto.getAmount());
        payment.setAPayPaymentStatus(APayPaymentStatus.CREATED);
        try {
            APayAddTransactionRequestDto requestDto = new APayAddTransactionRequestDto();
            requestDto.setAmount(dto.getAmount());
            requestDto.setTin(dto.getTin());

            APayAddTransactionResponseDto responseDto = aPayService.add(requestDto);

            payment.setBillId(responseDto.getId());
            payment.setMaskedPhoneNumber(responseDto.getMaskedPhoneNumber());
            repository.save(payment);
            responseDto.setLocalId(payment.getId().intValue());
            return responseDto;
        } catch (Exception e) {
            log.error(e.getMessage());

            ErrorDTO errorDTO = new ErrorDTO();
            errorDTO.setTitle("A-pay QR transaction");
            errorDTO.setStatus(400);
            errorDTO.setMessage(e.getMessage());
            throw new IntegrationException(e.getMessage(), errorDTO, HttpStatus.BAD_REQUEST);
        } finally {
            repository.save(payment);
        }
    }

    @Override
    @Transactional
    public void checkPaymentStatusCron() {
        log.info(">>>>>>>>>> Start Cron A-PAY check <<<<<<<<<<<<");
        repository
                .findByAPayPaymentStatus(APayPaymentStatus.WAITING_FOR_APPROVAL)
                .forEach(this::checkPayment);
        log.info(">>>>>>>>>> End Cron A-PAY check <<<<<<<<<<<<");
    }
}
