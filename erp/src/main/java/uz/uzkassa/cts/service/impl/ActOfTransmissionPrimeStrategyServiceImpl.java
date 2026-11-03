package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.ActOfTransmission;
import uz.uzkassa.cts.domain.ContractForLeasing;
import uz.uzkassa.cts.domain.Customer;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionFullResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionResponseDto;
import uz.uzkassa.cts.dto.billing.BillingInvoiceDTO;
import uz.uzkassa.cts.dto.billing.BillingInvoiceDetailDTO;
import uz.uzkassa.cts.dto.billing.BillingInvoicePayloadDTO;
import uz.uzkassa.cts.dto.contractforleasing.request.GeneratePoaTemplateDTO;
import uz.uzkassa.cts.dto.customerRequest.CustomerRequestDTO;
import uz.uzkassa.cts.dto.payload.ContractReportPayload;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.CustomerRequestType;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.billing.BillingAppType;
import uz.uzkassa.cts.enums.billing.BillingDocumentType;
import uz.uzkassa.cts.mappings.ActOfTransmissionMapper;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqBillingInvoiceProducer;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqContractReportProducer;
import uz.uzkassa.cts.repository.ActOfTransmissionRepository;
import uz.uzkassa.cts.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("PRIME_ACT")
@Transactional(readOnly = true)
public class ActOfTransmissionPrimeStrategyServiceImpl extends BaseServiceImpl<ActOfTransmission, Long> implements ActOfTransmissionStrategyService {

    private final ActOfTransmissionRepository repository;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final CustomerRequestService customerRequestService;
    private final ActOfTransmissionMapper actOfTransmissionMapper;
    private final BillingInvoiceService billingInvoiceService;
    private final RabbitMqBillingInvoiceProducer rabbitMqBillingInvoiceProducer;
    private final BillingClientService billingClientService;
    private final ActPaymentScheduleService actPaymentScheduleService;
    private final RabbitMqContractReportProducer rabbitMqContractReportProducer;

    public ActOfTransmissionPrimeStrategyServiceImpl(ActOfTransmissionRepository repository,
                                                     ThymeleafService thymeleafService,
                                                     FileUploadService fileUploadService,
                                                     CustomerRequestService customerRequestService,
                                                     ActOfTransmissionMapper actOfTransmissionMapper,
                                                     BillingInvoiceService billingInvoiceService,
                                                     RabbitMqBillingInvoiceProducer rabbitMqBillingInvoiceProducer,
                                                     BillingClientService billingClientService,
                                                     ActPaymentScheduleService actPaymentScheduleService,
                                                     RabbitMqContractReportProducer rabbitMqContractReportProducer) {
        super(repository);
        this.repository = repository;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.customerRequestService = customerRequestService;
        this.actOfTransmissionMapper = actOfTransmissionMapper;
        this.billingInvoiceService = billingInvoiceService;
        this.rabbitMqBillingInvoiceProducer = rabbitMqBillingInvoiceProducer;
        this.billingClientService = billingClientService;
        this.actPaymentScheduleService = actPaymentScheduleService;
        this.rabbitMqContractReportProducer = rabbitMqContractReportProducer;
    }


    @Override
    public List<ActOfTransmissionResponseDto> getByContractId(Long id) {
        List<ActOfTransmission> actOfTransmissions = repository.findByContract_IdOrderByIdDesc(id);
        return actOfTransmissions.stream().map(actOfTransmissionMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public String getHtmlTemplate(Long id) {
        return thymeleafService.generateActForTransmissionPrimeHTML(findById(id));
    }

    @Override
    public String getScheduleHtmlTemplate(Long id) {
        throw validationService.badRequestExceptionThrow(TEMPLATE_CREATION_NOT_ALLOWED, SCHEDULE).get();
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto uploadFile(Long id, MultipartFile file) {
        ActOfTransmission act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.ACT_OF_TRANSMISSION.name(), DocumentType.ACT_OF_TRANSMISSION.name());
        act.setFileName(response.getFileName());
        act.setContentType(file.getContentType());
        act.setFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto accept(Long id, LocalDate date) {
        ActOfTransmission act = findById(id);

        if (StringUtils.isEmpty(act.getFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (StringUtils.isEmpty(act.getPowerAttorneyFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        Customer customer = act.getContract().getCustomer();
        /*CHECK customer in Smartpos Billing*/
        billingClientService.checkingCustomer(customer);

        /*Get transaction history from Billing and calculate it*/
        actPaymentScheduleService.calculateBalanceByAct(act);

        act.setPaidDate(date);
        act.setStatus(ContractStatus.ACCEPTED);
        act.setAcceptDate(LocalDateTime.now());
        act = save(act);

        /*CREATE invoice in Billing*/
        this.createInvoiceInBilling(act);

        /*
         * CREATE
         * IN CUSTOMER REQUEST TO SHOW CALL CENTER
         * */
        CustomerRequestDTO customerRequestDTO = new CustomerRequestDTO();
        customerRequestDTO.setCustomerId(customer.getId());
        customerRequestDTO.setRequestType(CustomerRequestType.PRIME_LEASING);
        customerRequestService.create(customerRequestDTO);

        /*Create device in Contract report*/
        rabbitMqContractReportProducer.createContractReport(new ContractReportPayload(act.getId(), EntityTypeEnum.ACT_OF_TRANSMISSION));

        return actOfTransmissionMapper.toFullDto(act);
    }


    private void createInvoiceInBilling(ActOfTransmission actOfTransmission) {
        ContractForLeasing contractForLeasing = actOfTransmission.getContract();
        Customer customer = actOfTransmission.getContract().getCustomer();
        BillingInvoicePayloadDTO resultDTO = new BillingInvoicePayloadDTO();
        resultDTO.setCustomerId(customer.getId());
        resultDTO.setTin(customer.getTin());
        resultDTO.setAppType(BillingAppType.PRIME_APP.getCode());
        List<BillingInvoiceDetailDTO> detailList = new ArrayList<>();
        BillingInvoiceDetailDTO detailDTO = new BillingInvoiceDetailDTO();
        detailDTO.setXizmatId(703998L);
        detailDTO.setPrimeScheduleId(contractForLeasing.getId());
        detailDTO.setPrice1pcWithVat(actOfTransmission.getTotalPriceWithVat());
        detailDTO.setProductCount(actOfTransmission.getTotalProductCount());
        detailDTO.setServiceName(actOfTransmission.getDeviceModelName());
        detailDTO.setOverallPriceWithVat(actOfTransmission.getTotalPriceWithVat());
        detailList.add(detailDTO);
        resultDTO.setDocumentType(BillingDocumentType.PUBLIC_OFFER);
        resultDTO.setContractDate(actOfTransmission.getPaidDate());
        resultDTO.setTotalProductCount(actOfTransmission.getTotalProductCount());
        resultDTO.setTotalPriceWithVat(actOfTransmission.getTotalPriceWithVat());
        resultDTO.setDetails(detailList);

        BillingInvoiceDTO billingInvoiceDTO = new BillingInvoiceDTO();
        billingInvoiceDTO.setEntityId(contractForLeasing.getEntityId());
        billingInvoiceDTO.setEntityType(contractForLeasing.getEntityType());
        Long billingInvoiceId = billingInvoiceService.create(billingInvoiceDTO);
        resultDTO.setBillingInvoiceId(billingInvoiceId);

        log.info("dto send to queue -------- {}", resultDTO);
        rabbitMqBillingInvoiceProducer.sendToQueue(resultDTO);
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto changeToAttach(Long id) {
        ActOfTransmission act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto defect(Long id) {
        ActOfTransmission act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.DEFECT);
        save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }

    @Override
    public ActOfTransmissionFullResponseDto uploadScheduleFile(Long id, MultipartFile file) {
        throw validationService.entityNotFoundExceptionThrow(SCHEDULE).get();
    }

    @Override
    public String generatePoaTemplate(GeneratePoaTemplateDTO dto) {
        ActOfTransmission actOfTransmission = repository.findById(dto.getActOfTransmissionId()).orElseThrow(validationService.entityNotFoundExceptionThrow(ACT));
        return thymeleafService.generateActOfTransmissionPrimePoaHTML(actOfTransmission, dto);
    }

    @Override
    @Transactional
    public ActOfTransmissionFullResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file) {
        ActOfTransmission act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.ACT_POWER_ATTORNEY.name(), DocumentType.ACT_POWER_ATTORNEY.name());
        act.setPowerAttorneyFileName(response.getFileName());
        act.setPowerAttorneyContentType(file.getContentType());
        act.setPowerAttorneyFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return actOfTransmissionMapper.toFullDto(act);
    }

//    @Override
//    public List<ActOfTransmission> findForInvoiceCreate() {
//        return repository.findByStatusAndCompanyTypeAndInvoiceIsNull(ContractStatus.CLOSED, CompanyTypeEnum.PRIME);
//    }

    private void checkStatusForUpload(ActOfTransmission act) {
        if (ContractStatus.REJECTED.equals(act.getStatus()) ||
                ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, act.getStatus().getName()).get();
    }
}
