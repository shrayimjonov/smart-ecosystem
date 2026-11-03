package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.billing.BillingInvoiceDTO;
import uz.uzkassa.cts.dto.billing.BillingInvoiceDetailDTO;
import uz.uzkassa.cts.dto.billing.BillingInvoicePayloadDTO;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;
import uz.uzkassa.cts.dto.payload.ContractReportPayload;
import uz.uzkassa.cts.dto.payment.PaymentActDetailDTO;
import uz.uzkassa.cts.dto.payment.PaymentActPoaRequestDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.enums.apay.APayStatus;
import uz.uzkassa.cts.enums.billing.BillingAppType;
import uz.uzkassa.cts.enums.billing.BillingDocumentType;
import uz.uzkassa.cts.filter.PaymentActFilter;
import uz.uzkassa.cts.mappings.PaymentActMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqBillingInvoiceProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqContractReportProducer;
import uz.uzkassa.cts.repository.BaseRepository;
import uz.uzkassa.cts.repository.PaymentActRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentActServiceImpl extends BaseServiceImpl<PaymentAct, Long> implements PaymentActService {

    private final PaymentActRepository paymentActRepository;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final RabbitMqContractReportProducer rabbitMqContractReportProducer;
    private final CustomerRequestService customerRequestService;
    private final PaymentActMapper paymentActMapper;
    private final BillingInvoiceService billingInvoiceService;
    private final RabbitMqBillingInvoiceProducer rabbitMqBillingInvoiceProducer;
    private final BillingClientService billingClientService;
    private final BillingTransactionService billingTransactionService;

    public PaymentActServiceImpl(BaseRepository<PaymentAct, Long> repository,
                                 PaymentActRepository paymentActRepository,
                                 ThymeleafService thymeleafService,
                                 FileUploadService fileUploadService,
                                 RabbitMqContractReportProducer rabbitMqContractReportProducer,
                                 CustomerRequestService customerRequestService,
                                 PaymentActMapper paymentActMapper,
                                 BillingInvoiceService billingInvoiceService,
                                 RabbitMqBillingInvoiceProducer rabbitMqBillingInvoiceProducer,
                                 BillingClientService billingClientService, BillingTransactionService billingTransactionService) {
        super(repository);
        this.paymentActRepository = paymentActRepository;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.rabbitMqContractReportProducer = rabbitMqContractReportProducer;
        this.customerRequestService = customerRequestService;
        this.paymentActMapper = paymentActMapper;
        this.billingInvoiceService = billingInvoiceService;
        this.rabbitMqBillingInvoiceProducer = rabbitMqBillingInvoiceProducer;
        this.billingClientService = billingClientService;
        this.billingTransactionService = billingTransactionService;
    }

    @Override
    public List<PaymentActDetailDTO> getByContractId(Long id) {
        return paymentActRepository.findByContract_Id(id).stream().map(paymentActMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<PaymentActDetailDTO> getList(PaymentActFilter filter) {
        Specification<PaymentAct> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<PaymentAct, PaymentContract> contractJoin = root.join(PaymentAct_.CONTRACT, JoinType.INNER);
            Join<PaymentAct, Device> deviceJoin = root.join(PaymentAct_.DEVICE, JoinType.INNER);
            Join<PaymentAct, User> creatorJoin = root.join(PaymentAct_.CREATOR, JoinType.INNER);
            ArrayList<Predicate> list = new ArrayList<>();
            Path<Long> branchIdPath = contractJoin.get(PaymentContract_.BRANCH_ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.SUPER_ADMIN.getRoleCode(), Role.ADMIN.getRoleCode(), Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null)
                    list.add(criteriaBuilder.equal(branchIdPath, filter.getBranchId()));
            } else {
                list.add(criteriaBuilder.equal(branchIdPath, SecurityUtils.getBranchId()));
            }


            if (StringUtils.isNotEmpty(filter.getSearch())) {

                Path<Customer> customer = contractJoin.join(PaymentContract_.CUSTOMER, JoinType.INNER);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);
                Path<String> tin = customer.get(Customer_.TIN);
                Path<String> serialNumber = deviceJoin.get(Device_.SERIAL_NUMBER);
                Path<String> creatorLogin = creatorJoin.get(User_.LOGIN);
                Path<Long> paymentActId = root.get(PaymentAct_.ID);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                Predicate serialNumberPred = criteriaBuilder.like(criteriaBuilder.lower(serialNumber), filter.getSearchForQuery());
                Predicate creatorLoginPred = criteriaBuilder.like(criteriaBuilder.lower(creatorLogin), filter.getSearchForQuery());
                Predicate paymentActIdPred = criteriaBuilder.like(paymentActId.as(String.class), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred, serialNumberPred, creatorLoginPred, paymentActIdPred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(PaymentAct_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }

            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(PaymentAct_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }

            if (filter.getActDocFlowStatus() != null) {
                Path<ActDocFlowStatus> docFlowStatusPath = root.get(PaymentAct_.DOC_FLOW_STATUS);
                list.add(criteriaBuilder.equal(docFlowStatusPath, filter.getActDocFlowStatus()));
            }
            if (filter.getCreatorId() != null) {
                Path<Long> userId = creatorJoin.get(User_.ID);
                list.add(criteriaBuilder.equal(userId, filter.getCreatorId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return paymentActRepository.findAll(specification, filter.getOrderedPageable()).map(paymentActMapper::toDto);
    }

    @Override
    public PaymentActDetailDTO getById(Long id) {
        return paymentActMapper.toDto(findById(id));
    }

    @Override
    public String getHtmlTemplate(Long id) {
        return thymeleafService.generatePaymentActHTML(findById(id));
    }

    @Override
    public PaymentActDetailDTO uploadFile(Long id, MultipartFile file) {
        PaymentAct act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.PAYMENT_ACT.name(), DocumentType.PAYMENT_ACT.name());
        act.setFileName(response.getFileName());
        act.setFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return paymentActMapper.toDto(act);
    }

    @Override
    public PaymentActDetailDTO uploadPowerAttorneyFile(Long id, MultipartFile file) {
        PaymentAct act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.PAYMENT_ACT_POWER_ATTORNEY.name(), DocumentType.PAYMENT_ACT_POWER_ATTORNEY.name());
        act.setPowerAttorneyFileName(response.getFileName());
        act.setPowerAttorneyFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return paymentActMapper.toDto(act);
    }

    @Override
    public PaymentActDetailDTO accept(Long id) {
        PaymentAct paymentAct = findById(id);
        if (ContractStatus.REJECTED.equals(paymentAct.getStatus())) {
            throw validationService.badRequestExceptionThrow(ACT_NOT_ACCEPTED).get();
        }
        if (StringUtils.isEmpty(paymentAct.getFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (StringUtils.isEmpty(paymentAct.getPowerAttorneyFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        Customer customer = paymentAct.getContract().getCustomer();
        /*Check customer in Smartpos Billing*/
        billingClientService.checkingCustomer(customer);

        /*Get transaction history from Billing and calculate it*/
        billingTransactionService.create(customer.getId());
        boolean isBalance = billingTransactionService.calculateBalance(paymentAct.getTotalPriceWithVat(), customer.getId());
        if (isBalance) {
            paymentAct.setPaymentStatus(APayStatus.PAID);
        }
        paymentAct.setStatus(ContractStatus.ACCEPTED);
        paymentAct.setAcceptDate(LocalDateTime.now());
        paymentAct = save(paymentAct);

        /*Create invoice in Billing*/
        this.createInvoiceInBilling(paymentAct);

        /*
         * Create
         * In Customer Request to show call center
         * */
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setCustomerId(customer.getId());
        customerRequestDTO.setRequestType(CustomerRequestType.PRIME_PURCHASE);
        customerRequestService.create(customerRequestDTO);

        /*Create device in Contract report*/
        rabbitMqContractReportProducer.createContractReport(new ContractReportPayload(paymentAct.getId(), EntityTypeEnum.PAYMENT_ACT));

        return paymentActMapper.toDto(paymentAct);
    }

    private void createInvoiceInBilling(PaymentAct paymentAct) {
        PaymentContract contract = paymentAct.getContract();
        BillingInvoicePayloadDTO resultDTO = new BillingInvoicePayloadDTO();
        resultDTO.setTin(paymentAct.getContract().getCustomer().getTin());
        resultDTO.setCustomerId(paymentAct.getContract().getCustomer().getId());
        resultDTO.setAppType(BillingAppType.PRIME_APP.getCode());
        List<BillingInvoiceDetailDTO> detailList = new ArrayList<>();
        BillingInvoiceDetailDTO detailDTO = new BillingInvoiceDetailDTO();
        detailDTO.setXizmatId(703998L);
        detailDTO.setPrimeScheduleId(contract.getId());
        detailDTO.setPrice1pcWithVat(paymentAct.getTotalPriceWithVat());
        detailDTO.setProductCount(paymentAct.getTotalProductCount());
        detailDTO.setServiceName(paymentAct.getDevice().getModel().getName());
        detailDTO.setOverallPriceWithVat(paymentAct.getPriceFor1pcWithVat().multiply(BigDecimal.valueOf(paymentAct.getTotalProductCount())));
        detailList.add(detailDTO);
        resultDTO.setTotalProductCount(paymentAct.getTotalProductCount());
        resultDTO.setTotalPriceWithVat(paymentAct.getTotalPriceWithVat());
        resultDTO.setDetails(detailList);
        resultDTO.setDocumentType(BillingDocumentType.CONTRACT);
        resultDTO.setContractDate(contract.getCreatedDate().toLocalDate());
        resultDTO.setContractNumber(contract.getNumber());

        BillingInvoiceDTO billingInvoiceDTO = new BillingInvoiceDTO();
        billingInvoiceDTO.setEntityId(paymentAct.getEntityId());
        billingInvoiceDTO.setEntityType(paymentAct.getEntityType());
        Long billingInvoiceId = billingInvoiceService.create(billingInvoiceDTO);
        resultDTO.setBillingInvoiceId(billingInvoiceId);

        rabbitMqBillingInvoiceProducer.sendToQueue(resultDTO);
    }

    @Override
    @Transactional
    public void calculateCustomerBalance(Long customerId) {
        if (customerId == null) return;
        billingTransactionService.create(customerId);
        paymentActRepository.findAllByContract_CustomerIdAndPaymentStatus(customerId, APayStatus.PENDING).forEach(paymentAct -> {
            boolean isBalance = billingTransactionService.calculateBalance(paymentAct.getTotalPriceWithVat(), customerId);
            if (isBalance) {
                paymentAct.setPaymentStatus(APayStatus.PAID);
                paymentActRepository.saveAndFlush(paymentAct);
            }
        });
    }

    @Override
    public PaymentActDetailDTO changeToAttach(Long id) {
        PaymentAct act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(act);
        return paymentActMapper.toDto(act);
    }

    @Override
    public PaymentActDetailDTO defect(Long id) {
        PaymentAct act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.DEFECT);
        save(act);
        return paymentActMapper.toDto(act);
    }

    @Override
    public PaymentActDetailDTO changeDocFlowStatus(ActChangeDocFlowRequestDto dto) {
        PaymentAct paymentAct = findById(dto.getId());
        paymentAct.setDocFlowStatus(dto.getDocFlowStatus());
        paymentAct.setDocFlowReason(dto.getDocFlowReason());
        save(paymentAct);
        return paymentActMapper.toDto(paymentAct);
    }

    @Override
    public String generatePoaTemplate(PaymentActPoaRequestDTO dto) {
        PaymentAct paymentAct = findById(dto.getActId());
        return thymeleafService.generatePaymentActPoaHTML(paymentAct, dto);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentAct getByDeviceIdAndCustomerId(Long deviceId, Long customerId) {
        return paymentActRepository
                .findFirstByDeviceIdAndContract_CustomerId(deviceId, customerId).orElse(null);
    }

    private void checkStatusForUpload(PaymentAct act) {
        if (ContractStatus.REJECTED.equals(act.getStatus()) ||
                ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, act.getStatus().getName()).get();
    }
}
