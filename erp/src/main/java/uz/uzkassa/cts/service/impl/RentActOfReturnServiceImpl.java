package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.domain.base.BaseEntity;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentactofreturn.request.RentActOfReturnRequestDto;
import uz.uzkassa.cts.dto.rentactofreturn.request.RentActOfReturnUpdateRequestDto;
import uz.uzkassa.cts.dto.rentactofreturn.response.RentActOfReturnListResponseDto;
import uz.uzkassa.cts.dto.rentactofreturn.response.RentActOfReturnResponseDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.RentActOfReturnMapper;
import uz.uzkassa.cts.repository.RentActAccessoryRepository;
import uz.uzkassa.cts.repository.RentActOfReturnRepository;
import uz.uzkassa.cts.repository.RentPaymentHistoryRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RentActOfReturnServiceImpl extends BaseServiceImpl<RentActOfReturn, Long> implements RentActOfReturnService {

    private final RentActOfReturnRepository repository;
    private final RentActAccessoryRepository rentActAccessoryRepository;
    private final VatService vatService;
    private final FileUploadService fileUploadService;
    private final RentPaymentHistoryRepository rentPaymentHistoryRepository;
    private final ThymeleafService thymeleafService;
    private final LauncherService launcherService;
    private final RentInvoiceService rentInvoiceService;
    private final RentOfferService rentOfferService;
    private final RentActOfReturnMapper rentActOfReturnMapper;

    public RentActOfReturnServiceImpl(RentActOfReturnRepository repository,
                                      RentActAccessoryRepository rentActAccessoryRepository,
                                      VatService vatService,
                                      FileUploadService fileUploadService,
                                      RentPaymentHistoryRepository rentPaymentHistoryRepository,
                                      ThymeleafService thymeleafService,
                                      LauncherService launcherService,
                                      RentInvoiceService rentInvoiceService,
                                      RentOfferService rentOfferService,
                                      RentActOfReturnMapper rentActOfReturnMapper) {
        super(repository);
        this.repository = repository;
        this.rentActAccessoryRepository = rentActAccessoryRepository;
        this.vatService = vatService;
        this.fileUploadService = fileUploadService;
        this.rentPaymentHistoryRepository = rentPaymentHistoryRepository;
        this.thymeleafService = thymeleafService;
        this.launcherService = launcherService;
        this.rentInvoiceService = rentInvoiceService;
        this.rentOfferService = rentOfferService;
        this.rentActOfReturnMapper = rentActOfReturnMapper;
    }

    @Override
    public List<RentActOfReturnListResponseDto> getByCustomerId(Long id) {
        List<RentActOfReturn> acts = repository.findByCustomerId(id);
        if (acts.isEmpty())
            return new ArrayList<>();

        return acts.stream().map(rentActOfReturnMapper::toListDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RentActOfReturnListResponseDto> getList(BaseFilter filter) {
        Specification<RentActOfReturn> specification = getSpecification(filter);
        Page<RentActOfReturn> acts = findAll(specification, filter.getOrderedPageable());

        return acts.map(rentActOfReturnMapper::toListDto);
    }

    private Specification<RentActOfReturn> getSpecification(BaseFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<RentActOfReturn, Customer> customer = root.join(RentActOfReturn_.CUSTOMER, JoinType.INNER);
            Join<Customer, Branch> branchJoin = customer.join(Customer_.BRANCHES, JoinType.INNER);

            ArrayList<Predicate> list = new ArrayList<>();
            if (!SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode())) {
                Path<Long> branchId = branchJoin.get(Branch_.ID);
                list.add(criteriaBuilder.equal(branchId, SecurityUtils.getBranchId()));
            }
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> tin = customer.get(Customer_.TIN);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> contractStatusPath = root.get(RentActOfReturn_.STATUS);
                list.add(criteriaBuilder.equal(contractStatusPath, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(RentActOfReturn_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public Page<RentActOfReturnListResponseDto> listByTin(String tin, BaseFilter filter) {
        Specification<RentActOfReturn> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            Path<Customer> customer = root.join(RentOffer_.CUSTOMER, JoinType.INNER);
            Path<String> tinPath = customer.get(Customer_.TIN);
            list.add(criteriaBuilder.equal(tinPath, tin));

            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(RentOffer_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(RentOffer_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return findAll(specification, filter.getOrderedPageable())
                .map(rentActOfReturnMapper::toListDto);
    }

    @Override
    @Transactional
    public RentActOfReturnResponseDto create(RentActOfReturnRequestDto dto) {
        List<RentActAccessory> accessories =
                rentActAccessoryRepository.findByIdInAndCustomerId(dto.getAccessoryIds(), dto.getCustomerId());
        if (accessories.isEmpty())
            throw validationService.entityNotFoundExceptionThrow(ACCESSORIES).get();

        List<Long> actAccessoryIds = accessories.stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<RentPaymentHistory> histories = rentPaymentHistoryRepository.findByRentActAccessoryIdIn(actAccessoryIds);
        for (RentPaymentHistory history : histories) {
            if (!atLeastOneDay(history.getStartDate())) {
                throw validationService.badRequestExceptionThrow(RETURN_CONFIRMED).get();
            }
        }

        RentActOfReturn act = new RentActOfReturn();
        act.setCustomerId(dto.getCustomerId());
        act.setStatus(ContractStatus.CREATED);
        save(act);

        Integer productCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (RentActAccessory rentActAccessory : accessories) {
            rentActAccessory.setActOfReturnId(act.getId());
            productCount++;
            totalAmount = totalAmount.add(rentActAccessory.getAccessory().getPrice());
        }

        rentActAccessoryRepository.saveAll(accessories);

        Vat vat = vatService.getActiveVat();
        BigDecimal totalVat = BigDecimalUtil.calculateVatPriceFor1(totalAmount, vat.getPercent());
        BigDecimal totalProductWithoutVat = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(totalAmount, totalVat);

        act.setTotalProductCount(productCount);
        act.setTotalPriceWithVat(totalAmount);
        act.setPriceFor1pcWithVat(totalAmount);
        act.setPriceFor1pc(totalProductWithoutVat);
        act.setVat1pc(totalVat);
        save(act);

        return rentActOfReturnMapper.toDto(act);
    }

    @Override
    public RentActOfReturnResponseDto update(RentActOfReturnUpdateRequestDto dto) {
        List<RentActAccessory> accessories = rentActAccessoryRepository.findByIdIn(dto.getAccessoryIds());
        if (accessories.isEmpty())
            throw validationService.entityNotFoundExceptionThrow(ACCESSORIES).get();

        RentActOfReturn act = findById(dto.getId());

        Integer productCount = act.getTotalProductCount();
        BigDecimal totalAmount = act.getTotalPriceWithVat();
        for (RentActAccessory rentActAccessory : accessories) {
            rentActAccessory.setActOfReturnId(act.getId());
            productCount++;
            totalAmount = totalAmount.add(rentActAccessory.getAccessory().getPrice());
        }

        Vat vat = vatService.getActiveVat();
        BigDecimal totalVat = BigDecimalUtil.calculateVatPriceFor1(totalAmount, vat.getPercent());
        BigDecimal totalProductWithoutVat = BigDecimalUtil.calculateProductPriceFor1SubtractVatPrice(totalAmount, totalVat);

        act.setTotalProductCount(productCount);
        act.setTotalPriceWithVat(totalAmount);
        act.setPriceFor1pcWithVat(totalAmount);
        act.setPriceFor1pc(totalProductWithoutVat);
        act.setVat1pc(totalVat);
        save(act);

        return rentActOfReturnMapper.toDto(act);
    }

    @Override
    public RentActOfReturnResponseDto getById(Long id) {
        return rentActOfReturnMapper.toDto(findById(id));
    }

    @Override
    public String getHtmlTemplate(Long id) {
        RentActOfReturn act = repository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(RENT));
        return thymeleafService.generateRentActOfReturn(act);
    }

    @Override
    public RentActOfReturnResponseDto uploadFile(Long id, MultipartFile file) {
        RentActOfReturn act = findById(id);
        checkStatus(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.RENT_ACT_OF_RETURN.name(), DocumentType.RENT_ACT_OF_RETURN.name());
        act.setFileName(response.getFileName());
        act.setContentType(file.getContentType());
        act.setFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return rentActOfReturnMapper.toDto(act);
    }

    private void checkStatus(RentActOfReturn act) {
        if (ContractStatus.REJECTED.equals(act.getStatus()) ||
                ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, act.getStatus().getName()).get();
    }

    @Override
    public RentActOfReturnResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file) {
        RentActOfReturn act = findById(id);
        checkStatus(act);

        UploadFileResponse response = fileUploadService.upload(file, id,
                EntityTypeEnum.RENT_ACT_OF_RETURN_POWER_ATTORNEY.name(),
                DocumentType.RENT_ACT_OF_RETURN_POWER_ATTORNEY.name());

        act.setPowerAttorneyFileName(response.getFileName());
        act.setPowerAttorneyContentType(file.getContentType());
        act.setPowerAttorneyFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return rentActOfReturnMapper.toDto(act);
    }

    @Override
    @Transactional
    public RentActOfReturnResponseDto accept(Long id) {
        RentActOfReturn act = findById(id);
        if (ContractStatus.ACCEPTED == act.getStatus()) {
            throw validationService.badRequestExceptionThrow(ACT_ALREADY_CONFIRMED).get();
        }
        if (ContractStatus.REJECTED == act.getStatus()) {
            throw validationService.badRequestExceptionThrow(REJECTED_ACT).get();
        }
        if (StringUtils.isEmpty(act.getFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (StringUtils.isEmpty(act.getPowerAttorneyFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (CollectionUtils.isEmpty(act.getAccessories())) {
            throw validationService.requiredExceptionThrow(ACCESSORIES).get();
        }

        Set<RentActAccessory> accessories = act.getAccessories();
        List<Long> actAccessoryIds = accessories.stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<RentPaymentHistory> histories = rentPaymentHistoryRepository.findByRentActAccessoryIdIn(actAccessoryIds);
        LocalDate now = LocalDate.now();
        List<RentPaymentHistory> historiesForInvoice = new ArrayList<>();
        for (RentPaymentHistory history : histories) {
            if (history.getInvoiceId() != null) {
                continue;
            }
            if (!atLeastOneDay(history.getStartDate())) {
                throw validationService.badRequestExceptionThrow(RETURN_CONFIRMED).get();
            }
            BigDecimal calculatedAmount = BigDecimalUtil.calculatePrice(
                    history.getStartDate(), now,
                    history.getRentActAccessory().getAccessory().getRentPrice()
            );

            history.setAmount(BigDecimalUtil.setScaleByTwo(calculatedAmount));
            history.setDueDate(now);
            historiesForInvoice.add(history);
        }
        rentPaymentHistoryRepository.saveAll(histories);

        List<String> deviceList = new ArrayList<>();
        for (RentActAccessory accessory : accessories) {
            if (AccessoryType.DEVICE == accessory.getAccessory().getAccessoryType()) {
                deviceList.add(accessory.getDeviceSerialNumber());
            }
            accessory.setActive(false);
            accessory.setDueDate(now);
        }

        act.setStatus(ContractStatus.ACCEPTED);
        act.setAcceptDate(LocalDateTime.now());
        act = save(act);

        //CREATE INVOICE
        if (!historiesForInvoice.isEmpty()) {
            RentOffer offer = rentOfferService.getByCustomerId(act.getCustomerId());
            rentInvoiceService.createInvoice(offer.getId(), histories);
        }

        if (!CollectionUtils.isEmpty(deviceList)) {
            launcherService.block(deviceList);
        }

        return rentActOfReturnMapper.toDto(act);
    }

    private boolean atLeastOneDay(LocalDate startDate) {
        LocalDate date = startDate.plusDays(1);
        return date.isBefore(LocalDate.now()) || date.equals(LocalDate.now());
    }

    @Override
    public RentActOfReturnResponseDto changeToAttach(Long id) {
        RentActOfReturn act = findById(id);
        checkStatus(act);

        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);

        save(act);
        return rentActOfReturnMapper.toDto(act);
    }

    @Override
    public RentActOfReturnResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto) {
        RentActOfReturn act = findById(dto.getId());
        act.setDocFlowStatus(dto.getDocFlowStatus());
        act.setDocFlowReason(dto.getDocFlowReason());
        save(act);
        return rentActOfReturnMapper.toDto(act);
    }

    @Override
    public String generatePoaTemplate(TradeInActPoaRequestDto dto) {
        RentActOfReturn act = findById(dto.getActId());
        return thymeleafService.generateRentActOfReturnPoaHTML(act, dto);
    }

    public RentActOfReturnListResponseDto findByCustomerIdAndDeviceSerialNumber(Long customerId, String deviceSerialNumber) {
        return rentActOfReturnMapper.toListDto(repository.findByCustomerIdAndDeviceSerialNumber(customerId, deviceSerialNumber));
    }

    @Override
    public RentActOfReturnResponseDto uploadImage(Long id, MultipartFile file) {
        RentActOfReturn act = findById(id);
        checkStatus(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.RENT_ACT_OF_RETURN_IMAGE.name(), DocumentType.RENT_ACT_OF_RETURN_IMAGE.name());
        act.setImageFileName(response.getFileName());
        act.setImageContentType(file.getContentType());
        act.setImageFileUri(response.getFileDownloadUri());
        act = save(act);
        return rentActOfReturnMapper.toDto(act);
    }
}
