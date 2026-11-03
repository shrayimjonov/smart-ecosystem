package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.contractforleasing.request.ContractForLeasingDeviceRequestDto;
import uz.uzkassa.cts.dto.contractforleasing.request.ContractForLeasingRequestDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingDeviceCheckResponseDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingListResponseDto;
import uz.uzkassa.cts.dto.contractforleasing.response.ContractForLeasingResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.ContractForLeasingFilter;
import uz.uzkassa.cts.mappings.ContractFroLeasingMapper;
import uz.uzkassa.cts.repository.ContractForLeasingRepository;
import uz.uzkassa.cts.repository.DeviceRepository;
import uz.uzkassa.cts.repository.ModelPriceHistoryRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.DateUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Primary
@Service("STS")
@Transactional(readOnly = true)
public class ContractSTSLeasingStrategyServiceImpl extends BaseServiceImpl<ContractForLeasing, Long> implements ContractLeasingStrategyService {

    private final ContractForLeasingRepository repository;
    private final ModelPriceHistoryService modelPriceHistoryService;
    private final VatService vatService;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final DeviceRepository deviceRepository;
    private final PaymentScheduleInvoiceService paymentScheduleInvoiceService;
    private final ActOfTransmissionInvoiceOldService actOfTransmissionInvoiceOldService;
    private final ActOfTransmissionStrategyService actOfTransmissionService;
    private final ModelPriceHistoryRepository modelPriceHistoryRepository;
    private final ContractFroLeasingMapper contractFroLeasingMapper;

    public ContractSTSLeasingStrategyServiceImpl(ContractForLeasingRepository repository,
                                                 ModelPriceHistoryService modelPriceHistoryService,
                                                 VatService vatService, ThymeleafService thymeleafService,
                                                 FileUploadService fileUploadService,
                                                 DeviceRepository deviceRepository,
                                                 PaymentScheduleInvoiceService paymentScheduleInvoiceService,
                                                 ActOfTransmissionInvoiceOldService actOfTransmissionInvoiceOldService,
                                                 ActOfTransmissionStrategyService actOfTransmissionService,
                                                 ModelPriceHistoryRepository modelPriceHistoryRepository,
                                                 ContractFroLeasingMapper contractFroLeasingMapper) {
        super(repository);
        this.repository = repository;
        this.modelPriceHistoryService = modelPriceHistoryService;
        this.vatService = vatService;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.deviceRepository = deviceRepository;
        this.paymentScheduleInvoiceService = paymentScheduleInvoiceService;
        this.actOfTransmissionInvoiceOldService = actOfTransmissionInvoiceOldService;
        this.actOfTransmissionService = actOfTransmissionService;
        this.modelPriceHistoryRepository = modelPriceHistoryRepository;
        this.contractFroLeasingMapper = contractFroLeasingMapper;
    }


    @Override
    @Transactional
    public ContractForLeasingResponseDto create(ContractForLeasingRequestDto dto) {
        ContractForLeasing leasing = new ContractForLeasing();
        leasing.setBranchId(SecurityUtils.getBranchId());
        leasing.setCompanyId(getCompanyId());
        leasing.setCustomerId(dto.getCustomerId());
        leasing.setCompanyType(CompanyType.STS);
        leasing.setTotalProductCount(dto.getTotalProductCount());
        ModelPriceHistory history = modelPriceHistoryService.findById(dto.getModelPriceHistoryId());
        leasing.setDeviceType(history.getDeviceType());
        leasing.setModelPriceHistoryId(history.getId());
        leasing.setPeriod(history.getPeriod());
        leasing.setFirstPayment1pc(history.getFirstPaymentForLeasing());
        leasing.setEndDate(LocalDate.now().plusYears(1));

        Vat vat = vatService.getActiveVat();
        BigDecimal totalPriceWithVat = history.getPrice().multiply(BigDecimal.valueOf(dto.getTotalProductCount()));
        BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(history.getPrice(), vat.getPercent());
        BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(history.getPrice(), vatPriceFor1);
        BigDecimal totalVatPrice = vatPriceFor1.multiply(BigDecimal.valueOf(dto.getTotalProductCount()));
        leasing.setPriceFor1pc(productPriceFor1);
        leasing.setPriceFor1pcWithVat(history.getPrice());
        leasing.setTotalVatPrice(totalVatPrice);
        BigDecimal totalPrice = totalPriceWithVat.subtract(totalVatPrice);
        leasing.setTotalPrice(totalPrice);
        leasing.setTotalPriceWithVat(totalPriceWithVat);
        leasing.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        leasing = save(leasing);
        return contractFroLeasingMapper.toDto(leasing);
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto reject(Long id) {
        ContractForLeasing leasing = changeStatus(id, ContractStatus.REJECTED);
        return contractFroLeasingMapper.toDto(leasing);
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto rejectWithActs(Long id) {
        ContractForLeasing leasing = findById(id);
        Set<ActOfTransmission> actOfTransmissions = leasing.getActOfTransmissions();
        for (ActOfTransmission act : actOfTransmissions) {
            List<ActPaymentSchedule> schedules = new ArrayList<>();
            act.getDetails().forEach(detail -> schedules.add(detail.getLastSchedule()));
            for (ActPaymentSchedule schedule : schedules) {
                schedule.setStatus(ContractStatus.REJECTED);
            }
            act.setStatus(ContractStatus.REJECTED);
        }
        leasing.setStatus(ContractStatus.REJECTED);
        save(leasing);

        return contractFroLeasingMapper.toDto(leasing);
    }

    private ContractForLeasing changeStatus(Long id, ContractStatus contractStatus) {
        ContractForLeasing leasing = findById(id);
        CheckRejectedStatus(leasing);
        if (!leasing.getActOfTransmissions().isEmpty() && ContractStatus.REJECTED.equals(contractStatus))
            throw validationService.badRequestExceptionThrow(CONTRACT_CANNOT_BE_REJECTED_IF_ACTS).get();

        leasing.setStatus(contractStatus);
        save(leasing);
        return leasing;
    }

    @Override
    public String getContractHtmlTemplate(Long id) {
        ContractForLeasing contract = findById(id);
        CustomerBank bank = contract.getCustomer().getCustomerBank();
        if (bank == null) {
            throw validationService.entityNotFoundExceptionThrow(BANK).get();
        }
        if (bank.getAccountNumber() == null) {
            throw validationService.entityNotFoundExceptionThrow(ACCOUNT_NUMBER).get();
        }
        return thymeleafService.generateContractForLeasingHTML(contract);
    }

    @Override
    public String getWarrantyHtmlTemplate(Long id) {
        return thymeleafService.generateWarrantyForLeasingHTML(findById(id));
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto uploadContractFile(Long id, MultipartFile file) {
        ContractForLeasing leasing = findById(id);
        CheckRejectedStatus(leasing);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.CONTRACT_FOR_LEASING.name(), DocumentType.CONTRACT_FOR_LEASING.name());
        leasing.setFileName(response.getFileName());
        leasing.setContentType(file.getContentType());
        leasing.setFileUri(response.getFileDownloadUri());
        if (!ContractStatus.ACCEPTED.equals(leasing.getStatus()))
            if (StringUtils.isNotEmpty(leasing.getWarrantyFileName()))
                leasing.setStatus(ContractStatus.DOCUMENT_ATTACHED);
            else
                leasing.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(leasing);
        return contractFroLeasingMapper.toDto(leasing);
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto uploadWarrantyFile(Long id, MultipartFile file) {
        ContractForLeasing leasing = findById(id);
        CheckRejectedStatus(leasing);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.WARRANTY_FOR_LEASING.name(), DocumentType.WARRANTY_FOR_LEASING.name());
        leasing.setWarrantyFileName(response.getFileName());
        leasing.setWarrantyContentType(file.getContentType());
        leasing.setWarrantyFileUri(response.getFileDownloadUri());
        if (!ContractStatus.ACCEPTED.equals(leasing.getStatus()))
            if (StringUtils.isNotEmpty(leasing.getFileName()))
                leasing.setStatus(ContractStatus.DOCUMENT_ATTACHED);
            else
                leasing.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(leasing);
        return contractFroLeasingMapper.toDto(leasing);
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto accept(Long id) {
        ContractForLeasing leasing = findById(id);
        if (!ContractStatus.DOCUMENT_ATTACHED.equals(leasing.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.DOCUMENT_ATTACHED.getName()).get();
        }
        leasing.setStatus(ContractStatus.ACCEPTED);
        save(leasing);
        return contractFroLeasingMapper.toDto(leasing);
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto changeToAttach(Long id) {
        ContractForLeasing leasing = findById(id);
        if (!ContractStatus.ACCEPTED.equals(leasing.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();

        }
        leasing.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(leasing);
        return contractFroLeasingMapper.toDto(leasing);
    }

    @Override
    public Page<ContractForLeasingListResponseDto> listByTin(ContractForLeasingFilter filter, String tin) {
        Specification<ContractForLeasing> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);
            ArrayList<Predicate> list = new ArrayList<>();
            list.add(criteriaBuilder.equal(root.get(ContractForLeasing_.COMPANY_TYPE), CompanyType.STS));
            Path<Customer> customer = root.join("customer", JoinType.INNER);
            Path<String> tinPath = customer.get("tin");
            list.add(criteriaBuilder.equal(tinPath, tin));
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get("createdDate");
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get("status");
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            Path<Long> companyIdPath = root.get(ContractForLeasing_.COMPANY_ID);
            if (filter.getCompanyId() != null) {
                list.add(criteriaBuilder.equal(companyIdPath, filter.getCompanyId()));
            }
            if (!isSuperAdmin()) {
                list.add(criteriaBuilder.equal(companyIdPath, getCompanyId()));
            }

            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Page<ContractForLeasing> contracts = repository.findAll(specification, filter.getOrderedPageable());

        return contracts.map(contractFroLeasingMapper::toListDto);
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto addDevice(Long id, ContractForLeasingDeviceRequestDto deviceRequestDto) {
        if (CollectionUtils.isEmpty(deviceRequestDto.getDeviceIds())) {
            throw validationService.badRequestExceptionThrow(CONTRACT_CANNOT_BE_CREATED).get();
        }
        ContractForLeasing leasing = findById(id);
        if (!ContractStatus.ACCEPTED.equals(leasing.getStatus())) {
            throw validationService.badRequestExceptionThrow(ACCEPT_CONTRACT, leasing.getNumber()).get();
        }

        Set<Long> deviceIds = getContractDeviceIds(leasing);
        for (Long deviceId : deviceRequestDto.getDeviceIds()) {
            if (deviceIds.contains(deviceId)) {
                throw validationService.entityAlreadyExistExceptionThrow(DEVICE, ID, deviceId.toString()).get();
            }
            deviceIds.add(deviceId);
        }

        int count = deviceIds.size();
        if (count > leasing.getTotalProductCount()) {
            throw validationService.badRequestExceptionThrow(DEVICE_ADD, leasing.getTotalProductCount().toString()).get();
        }
        addAct(deviceRequestDto, leasing);
        leasing = save(leasing);

        return contractFroLeasingMapper.toDto(leasing);
    }

    @Override
    public ContractForLeasingDeviceCheckResponseDto getForDeviceAdd(Long id) {
        ContractForLeasing leasing = repository.findByStatusAndIdAndCompanyTypeOrderByIdDesc(ContractStatus.ACCEPTED, id, CompanyType.STS);
        if (leasing == null) {
            throw validationService.entityNotFoundExceptionThrow(CONTRACT, ID, id.toString()).get();
        }

        ContractForLeasingDeviceCheckResponseDto dto = new ContractForLeasingDeviceCheckResponseDto();
        dto.setDeviceTotal(leasing.getTotalProductCount());
        int deviceCount = getContractDeviceIds(leasing).size();
        dto.setExistDevices(deviceCount);
        dto.setDeviceCanBeAdded(leasing.getTotalProductCount() - deviceCount);
        dto.setId(leasing.getId());
        dto.setCreateDate(leasing.getCreatedDate().toLocalDate());
        dto.setEndDate(leasing.getEndDate());
        dto.setNumber(leasing.getNumber());
        return dto;
    }

    @Override
    public List<ContractForLeasingDeviceCheckResponseDto> getForDeviceAddByCustomerId(Long id, String deviceType) {
        List<ContractForLeasingDeviceCheckResponseDto> leasingToShow = new ArrayList<>();
        List<ContractForLeasing> leasings =
                repository.findByStatusAndCustomerIdAndCompanyTypeAndDeviceTypeOrderByIdDesc(ContractStatus.ACCEPTED, id, CompanyType.STS, DeviceType.valueOf(deviceType));
        if (CollectionUtils.isEmpty(leasings))
            return leasingToShow;

        for (ContractForLeasing leasing : leasings) {
            int deviceCount = getContractDeviceIds(leasing).size();
            if (deviceCount < leasing.getTotalProductCount()) {
                ContractForLeasingDeviceCheckResponseDto dto = new ContractForLeasingDeviceCheckResponseDto();
                dto.setDeviceTotal(leasing.getTotalProductCount());
                dto.setDeviceModel(leasing.getModelPriceHistory().getDeviceModel().toCommonCodeDTO());
                dto.setExistDevices(deviceCount);
                dto.setDeviceCanBeAdded(leasing.getTotalProductCount() - deviceCount);
                dto.setId(leasing.getId());
                dto.setCreateDate(leasing.getCreatedDate().toLocalDate());
                dto.setEndDate(leasing.getEndDate());
                dto.setNumber(leasing.getNumber());
                dto.setFirstPaymentForLeasing(leasing.getModelPriceHistory().getFirstPaymentForLeasing());

                leasingToShow.add(dto);
            }
        }
        return leasingToShow;
    }

    @Override
    @Transactional
    public void earlyCloseContract(Long contractId) {
        ContractForLeasing leasing = findById(contractId);
        if (!ContractStatus.ACCEPTED.equals(leasing.getStatus())) {
            throw validationService.badRequestExceptionThrow(ACCEPT_CONTRACT, leasing.getNumber()).get();
        }

        Set<ActOfTransmission> acts = leasing.getActOfTransmissions();
        for (ActOfTransmission act : acts) {
            if (!ContractStatus.ACCEPTED.equals(act.getStatus()))
                throw validationService.badRequestExceptionThrow(ACCEPT_CONTRACT, act.getNumber()).get();

            if (act.getInvoice() != null) continue;

            List<ActPaymentSchedule> schedules = new ArrayList<>();
            act.getDetails().forEach(detail -> schedules.add(detail.getLastSchedule()));
            for (ActPaymentSchedule schedule : schedules) {
                if (schedule.getInvoiceId() == null) {
                    paymentScheduleInvoiceService.createInvoiceAndChangeBalance(schedule);
                }
            }
            act.setStatus(ContractStatus.CLOSED);
            actOfTransmissionService.save(act);
            actOfTransmissionInvoiceOldService.createInvoiceForCron(act);
        }

    }

    @Transactional(readOnly = true)
    @Override
    public List<CommonCodeDTO> getItems() {
        List<ModelPriceHistory> modelPriceHistories = modelPriceHistoryRepository.findAllByOrderTypeAndCompanyType(OrderType.LEASING, CompanyType.STS);
        return modelPriceHistories
                .stream()
                .map(ModelPriceHistory::toCommonCodeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String getPaymentQuote(Long id) {
        return null;
    }

    private void addAct(ContractForLeasingDeviceRequestDto deviceRequestDto, ContractForLeasing leasing) {
        ActOfTransmission leasingAct = new ActOfTransmission();
        leasingAct.setCompanyType(leasing.getCompanyType());
        Vat vat = vatService.getActiveVat();
        BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(leasing.getPriceFor1pcWithVat(), vat.getPercent());
        BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(leasing.getPriceFor1pcWithVat(), vatPriceFor1);

        Set<Device> deviceSet = deviceRepository.findByIdIn(deviceRequestDto.getDeviceIds());
        for (Device device : deviceSet) {
            ActOfTransmissionDetail detail = new ActOfTransmissionDetail();
            detail.setDeviceId(device.getId());
            detail.setVatFor1pc(vatPriceFor1);
            detail.setPriceFor1pc(productPriceFor1);
            detail.setPriceWithVatFor1pc(leasing.getPriceFor1pcWithVat());
            detail.setBonus(leasing.getModelPriceHistory().getBonus());
            createSchedule(leasingAct, detail, leasing.getFirstPayment1pc());
            leasingAct.addDetail(detail);
        }
        leasingAct.setPeriod(leasing.getPeriod());
        leasingAct.setPriceFor1pc(productPriceFor1);

        int deviceSize = leasingAct.getDetails().size();
        leasingAct.setTotalProductCount(deviceSize);
        BigDecimal totalPriceWithVat = leasing.getPriceFor1pcWithVat().multiply(BigDecimal.valueOf(deviceSize));
        BigDecimal totalVatPrice = vatPriceFor1.multiply(BigDecimal.valueOf(deviceSize));

        leasingAct.setTotalPrice(totalPriceWithVat.subtract(totalVatPrice));
        leasingAct.setTotalVatPrice(totalVatPrice);
        leasingAct.setTotalPriceWithVat(totalPriceWithVat);
        leasingAct.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        leasing.addAct(leasingAct);
    }

    private void createSchedule(ActOfTransmission act, ActOfTransmissionDetail actDetail, BigDecimal firstPayment1pc) {
        log.info("Act:{} ", act);
        log.info("firstPayment1pc:{} ", firstPayment1pc);

        BigDecimal firstPayment = firstPayment1pc.multiply(BigDecimal.valueOf(act.getTotalProductCount()));
        BigDecimal amountForMonth = act.getTotalPriceWithVat().subtract(firstPayment);
        amountForMonth = amountForMonth.divide(BigDecimal.valueOf(act.getPeriod()), MathContext.DECIMAL64);

        BigDecimal balance = act.getTotalPriceWithVat();
        boolean firstTimeCalc = true;
        LocalDate date = LocalDate.now();
        for (int i = 0; i < act.getPeriod() + 1; i++) {
            ActPaymentSchedule schedule = new ActPaymentSchedule();
            if (firstTimeCalc) {
                firstTimeCalc = false;
                schedule.setPaymentDate(date);
                schedule.setAmount(firstPayment);
                balance = balance.subtract(firstPayment);
            } else {
                schedule.setPaymentDate(date);
                schedule.setAmount(amountForMonth);
                balance = balance.subtract(amountForMonth);
            }
            schedule.setTotalProductCount(act.getTotalProductCount());
            schedule.setBalance(balance);
            actDetail.addSchedule(schedule);
            date = DateUtil.getMonthLastDate(date.plusMonths(1));
        }
    }

    private Set<Long> getContractDeviceIds(ContractForLeasing leasing) {
        Set<Long> deviceIds = new HashSet<>();
        Set<ActOfTransmission> actOfTransmissions = leasing.getActOfTransmissions();
        for (ActOfTransmission act : actOfTransmissions) {
            Set<ActOfTransmissionDetail> details = act.getDetails();
            for (ActOfTransmissionDetail detail : details) {
                deviceIds.add(detail.getDeviceId());
            }
        }
        return deviceIds;
    }

    private void CheckRejectedStatus(ContractForLeasing leasing) {
        if (ContractStatus.REJECTED.equals(leasing.getStatus()) ||
                ContractStatus.ACCEPTED.equals(leasing.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, leasing.getStatus().getName()).get();
    }
}
