package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.ContractForLeasingFilter;
import uz.uzkassa.cts.mappings.ContractFroLeasingMapper;
import uz.uzkassa.cts.repository.ContractForLeasingRepository;
import uz.uzkassa.cts.repository.DeviceRepository;
import uz.uzkassa.cts.repository.ModelPriceHistoryRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service("PRIME")
@Transactional(readOnly = true)
public class ContractPrimeLeasingStrategyServiceImpl extends BaseServiceImpl<ContractForLeasing, Long> implements ContractLeasingStrategyService {

    private final ContractForLeasingRepository repository;
    private final ModelPriceHistoryService modelPriceHistoryService;
    private final VatService vatService;
    private final DeviceRepository deviceRepository;
    private final ActOfTransmissionStrategyService actOfTransmissionService;
    private final ModelPriceHistoryRepository modelPriceHistoryRepository;
    private final ThymeleafService thymeleafService;
    private final CustomerService customerService;
    private final ContractFroLeasingMapper contractFroLeasingMapper;

    public ContractPrimeLeasingStrategyServiceImpl(ContractForLeasingRepository repository,
                                                   ModelPriceHistoryService modelPriceHistoryService,
                                                   VatService vatService, DeviceRepository deviceRepository,
                                                   ActOfTransmissionStrategyService actOfTransmissionService,
                                                   ModelPriceHistoryRepository modelPriceHistoryRepository,
                                                   ThymeleafService thymeleafService,
                                                   CustomerService customerService,
                                                   ContractFroLeasingMapper contractFroLeasingMapper) {
        super(repository);
        this.repository = repository;
        this.modelPriceHistoryService = modelPriceHistoryService;
        this.vatService = vatService;
        this.deviceRepository = deviceRepository;
        this.actOfTransmissionService = actOfTransmissionService;
        this.modelPriceHistoryRepository = modelPriceHistoryRepository;
        this.thymeleafService = thymeleafService;
        this.customerService = customerService;
        this.contractFroLeasingMapper = contractFroLeasingMapper;
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto create(ContractForLeasingRequestDto dto) {
        Customer customer = customerService.findById(dto.getCustomerId());
        if (customer.getAppType() != null && !CustomerAppType.SMARTPOS.equals(customer.getAppType())) {
            throw validationService.badRequestExceptionThrow(LEASING_PRIME_APP_TYPE).get();
        }
        ContractForLeasing leasing = new ContractForLeasing();
        leasing.setBranchId(SecurityUtils.getBranchId());
        leasing.setCompanyId(getCompanyId());
        leasing.setCustomerId(dto.getCustomerId());
        leasing.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        leasing.setCompanyType(CompanyType.PRIME);
        leasing.setTotalProductCount(dto.getTotalProductCount());
        ModelPriceHistory history = modelPriceHistoryService.findById(dto.getModelPriceHistoryId());
        leasing.setDeviceType(history.getDeviceType());
        leasing.setModelPriceHistoryId(history.getId());
        leasing.setPeriod(history.getPeriod());
        leasing.setFirstPayment1pc(history.getFirstPaymentForLeasing());

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
        leasing.setActivationCode(RandomStringUtils.randomNumeric(6));
        leasing = save(leasing);
//        smsService.send(new EtcRequestDto(leasing.getCustomer().getPhone(), String.format(Constants.LEASING_PRIME_OFFER, leasing.getActivationCode())));

        return contractFroLeasingMapper.toDto(leasing);
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto reject(Long id) {
        ContractForLeasing leasing = findById(id);
        checkRejectedStatus(leasing);
        leasing.setStatus(ContractStatus.REJECTED);
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

    @Override
    public String getContractHtmlTemplate(Long id) {
//        throw validationService.badRequestExceptionThrow("error.template.prime", "contract").get();
        return thymeleafService.generateContractForLeasingPrimeHTML();
    }

    @Override
    public String getWarrantyHtmlTemplate(Long id) {
        throw validationService.badRequestExceptionThrow(TEMPLATE_CREATION_NOT_ALLOWED, "warranty").get();
    }

    @Override
    public ContractForLeasingResponseDto uploadContractFile(Long id, MultipartFile file) {
        throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_UNEXPECTED_ERROR).get();
    }

    @Override
    public ContractForLeasingResponseDto uploadWarrantyFile(Long id, MultipartFile file) {
        throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_UNEXPECTED_ERROR).get();
    }

    @Override
    @Transactional
    public ContractForLeasingResponseDto accept(Long id) {
        ContractForLeasing leasing = findById(id);
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
            list.add(criteriaBuilder.equal(root.get(ContractForLeasing_.COMPANY_TYPE), CompanyType.PRIME));
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
        this.addAct(deviceRequestDto, leasing);
        leasing = save(leasing);

        return contractFroLeasingMapper.toDto(leasing);
    }

    @Override
    public ContractForLeasingDeviceCheckResponseDto getForDeviceAdd(Long id) {
        ContractForLeasing leasing = repository.findByStatusAndIdAndCompanyTypeOrderByIdDesc(ContractStatus.ACCEPTED, id, CompanyType.PRIME);
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
        List<ContractForLeasing> contractForLeasingList =
                repository.findByStatusAndCustomerIdAndCompanyTypeAndDeviceTypeOrderByIdDesc(ContractStatus.ACCEPTED, id, CompanyType.PRIME, DeviceType.valueOf(deviceType));
        if (CollectionUtils.isEmpty(contractForLeasingList)) {
            return leasingToShow;
        }

        for (ContractForLeasing leasing : contractForLeasingList) {
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

            if (act.getInvoice() != null)
                continue;

            //TODO when new billing starts - then create new logic for this
//            List<ActPaymentSchedule> schedules = new ArrayList<>();
//            act.getDetails().forEach(detail -> schedules.add(detail.getLastSchedule()));
//            for (ActPaymentSchedule schedule : schedules) {
//                if (schedule.getInvoiceId() == null) {
//                    paymentScheduleInvoiceService.createInvoiceAndChangeBalance(schedule);
//                }
//            }
            act.setStatus(ContractStatus.CLOSED);
            actOfTransmissionService.save(act);
//            actOfTransmissionInvoiceService.createInvoiceForCron(act);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommonCodeDTO> getItems() {
        List<ModelPriceHistory> modelPriceHistories = modelPriceHistoryRepository.findAllByOrderTypeAndCompanyType(OrderType.LEASING, CompanyType.PRIME);
        return modelPriceHistories
                .stream()
                .map(ModelPriceHistory::toCommonCodeDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String getPaymentQuote(Long id) {
        return thymeleafService.generatePrimePaymentQuoteHTML(findById(id));
    }

    private void addAct(ContractForLeasingDeviceRequestDto deviceRequestDto, ContractForLeasing leasing) {
        ActOfTransmission leasingAct = new ActOfTransmission();
        leasingAct.setCompanyType(leasing.getCompanyType());
        Vat vat = vatService.getActiveVat();
        BigDecimal vatPriceFor1 = BigDecimalUtil.calculateVatPriceFor1(leasing.getPriceFor1pcWithVat(), vat.getPercent());
        BigDecimal productPriceFor1 = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(leasing.getPriceFor1pcWithVat(), vatPriceFor1);

        leasingAct.setPeriod(leasing.getPeriod());
        leasingAct.setPriceFor1pc(productPriceFor1);

        Set<Device> deviceSet = deviceRepository.findByIdIn(deviceRequestDto.getDeviceIds());
        for (Device device : deviceSet) {
            int deviceSize = 1;
            leasingAct.setTotalProductCount(deviceSize);
            BigDecimal totalPriceWithVat = leasing.getPriceFor1pcWithVat().multiply(BigDecimal.valueOf(deviceSize));
            BigDecimal totalVatPrice = vatPriceFor1.multiply(BigDecimal.valueOf(deviceSize));

            leasingAct.setTotalPrice(totalPriceWithVat.subtract(totalVatPrice));
            leasingAct.setTotalVatPrice(totalVatPrice);
            leasingAct.setTotalPriceWithVat(totalPriceWithVat);
            leasingAct.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);


            ActOfTransmissionDetail detail = new ActOfTransmissionDetail();
            detail.setDeviceId(device.getId());
            detail.setVatFor1pc(vatPriceFor1);
            detail.setPriceFor1pc(productPriceFor1);
            detail.setPriceWithVatFor1pc(leasing.getPriceFor1pcWithVat());
            detail.setBonus(leasing.getModelPriceHistory().getBonus());
            /* Attach schedule */
            this.createSchedule(leasingAct, detail, leasing.getFirstPayment1pc());
            leasingAct.addDetail(detail);
        }
//        createSchedule(leasingAct, leasing.getFirstPayment1pc());
        leasing.addAct(leasingAct);
    }


    private void createSchedule(ActOfTransmission leasingAct, ActOfTransmissionDetail actDetail, BigDecimal firstPayment1pc) {
        log.info("Act:{} ", leasingAct);
        log.info("firstPayment1pc:{} ", firstPayment1pc);

        BigDecimal firstPayment = firstPayment1pc.multiply(BigDecimal.valueOf(leasingAct.getTotalProductCount()));
        BigDecimal amountForMonth = leasingAct.getTotalPriceWithVat().subtract(firstPayment);
        amountForMonth = amountForMonth.divide(BigDecimal.valueOf(leasingAct.getPeriod()), MathContext.DECIMAL64);

        BigDecimal balance = leasingAct.getTotalPriceWithVat();
        boolean firstTimeCalc = true;
        LocalDate date = LocalDate.now();
        for (int i = 0; i < leasingAct.getPeriod() + 1; i++) {
            ActPaymentSchedule schedule = new ActPaymentSchedule();
            if (firstTimeCalc) {
                firstTimeCalc = false;
                schedule.setPaymentDate(date);
                schedule.setAmount(firstPayment);
                balance = balance.subtract(firstPayment);
            } else {
                schedule.setPaymentDate(date.plusMonths(i));
                schedule.setAmount(amountForMonth);
                balance = balance.subtract(amountForMonth);
            }
            schedule.setTotalProductCount(leasingAct.getTotalProductCount());
            schedule.setBalance(balance);
            actDetail.addSchedule(schedule);
//            date = date.plusMonths(1);
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

    private void checkRejectedStatus(ContractForLeasing leasing) {
        if (ContractStatus.REJECTED.equals(leasing.getStatus()) ||
                ContractStatus.ACCEPTED.equals(leasing.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, leasing.getStatus().getName()).get();
    }
}
