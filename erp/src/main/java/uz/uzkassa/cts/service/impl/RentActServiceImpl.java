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
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentact.request.RentActAccessoryRequestDto;
import uz.uzkassa.cts.dto.rentact.request.RentActCreateRequestDto;
import uz.uzkassa.cts.dto.rentact.request.RentActRequestDto;
import uz.uzkassa.cts.dto.rentact.request.RentActUpdateRequestDto;
import uz.uzkassa.cts.dto.rentact.response.RentActListResponseDto;
import uz.uzkassa.cts.dto.rentact.response.RentActResponseDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.RentActMapper;
import uz.uzkassa.cts.repository.AccessoryRepository;
import uz.uzkassa.cts.repository.RentActRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.BigDecimalUtil;
import uz.uzkassa.cts.utils.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RentActServiceImpl extends BaseServiceImpl<RentAct, Long> implements RentActService {

    private final RentOfferService rentOfferService;
    private final CustomerOldService customerOldService;
    private final RentActRepository repository;
    private final FileUploadService fileUploadService;
    private final AccessoryRepository accessoryRepository;
    private final VatService vatService;
    private final RentPaymentHistoryService rentPaymentHistoryService;
    private final ThymeleafService thymeleafService;
    private final LedgerService ledgerService;
    private final RentActMapper rentActMapper;

    public RentActServiceImpl(RentActRepository repository,
                              RentOfferService rentOfferService,
                              CustomerOldService customerOldService,
                              FileUploadService fileUploadService,
                              AccessoryRepository accessoryRepository,
                              VatService vatService,
                              RentPaymentHistoryService rentPaymentHistoryService,
                              ThymeleafService thymeleafService,
                              LedgerService ledgerService,
                              RentActMapper rentActMapper) {
        super(repository);
        this.repository = repository;
        this.rentOfferService = rentOfferService;
        this.customerOldService = customerOldService;
        this.accessoryRepository = accessoryRepository;
        this.fileUploadService = fileUploadService;
        this.vatService = vatService;
        this.rentPaymentHistoryService = rentPaymentHistoryService;
        this.thymeleafService = thymeleafService;
        this.ledgerService = ledgerService;
        this.rentActMapper = rentActMapper;
    }

    @Override
    public List<RentActListResponseDto> getByOfferId(Long id) {
        List<RentAct> acts = repository.findByOfferId(id);
        if (acts.isEmpty())
            return new ArrayList<>();

        return acts.stream().map(rentActMapper::toListDto).collect(Collectors.toList());
    }

    @Override
    public Page<RentActListResponseDto> getList(BaseFilter filter) {
        Specification<RentAct> specification = getSpecification(filter);
        Page<RentAct> acts = repository.findAll(specification, filter.getOrderedPageable());

        return acts.map(rentActMapper::toListDto);
    }

    private Specification<RentAct> getSpecification(BaseFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<RentAct, Customer> customer = root.join(RentAct_.CUSTOMER, JoinType.INNER);
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
                Path<ContractStatus> contractStatusPath = root.get(RentAct_.STATUS);
                list.add(criteriaBuilder.equal(contractStatusPath, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(RentAct_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public Page<RentActListResponseDto> listByTin(String tin, BaseFilter filter) {
        Specification<RentAct> specification = (root, criteriaQuery, criteriaBuilder) -> {
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
            .map(rentActMapper::toListDto);
    }

    @Override
    public RentActResponseDto create(RentActCreateRequestDto dto) {
        Customer customer = customerOldService.findById(dto.getCustomerId());
        RentOffer offer = rentOfferService.getByIdAndCustomerId(dto.getOfferId(), dto.getCustomerId());
        if (ContractStatus.ACCEPTED != offer.getStatus()) {
            throw validationService.entityNotFoundExceptionThrow(OFFER).get();
        }
        List<RentAct> acts = repository.findByCustomerIdAndStatusIn(dto.getCustomerId(),
                List.of(ContractStatus.CREATED, ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT));
        if (!acts.isEmpty()) {
            throw validationService.requiredExceptionThrow("Act confirmation").get();
        }

        RentAct act = new RentAct();
        act.setOfferId(offer.getId());
        act.setCustomerId(customer.getId());
        act.setStatus(ContractStatus.CREATED);
        save(act);
        return rentActMapper.toDto(act);
    }

    @Override
    public List<RentActResponseDto> getActsToAddDevices(Long customerId) {
        return repository.findByCustomerIdAndStatus(customerId, ContractStatus.CREATED)
            .stream().map(rentActMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public RentActResponseDto addDevice(RentActRequestDto dto) {
        RentAct act = repository.findByIdAndCustomerId(dto.getActId(), dto.getCustomerId())
            .orElseThrow(validationService.entityNotFoundExceptionThrow(ACT));

        if (ContractStatus.ACCEPTED == act.getStatus()) {
            throw validationService.badRequestExceptionThrow(INVALID_ACTION).get();
        }

        Map<Long, Accessory> accessories = accessoryRepository
            .findByIdIn(dto.getAccessories().stream().map(RentActAccessoryRequestDto::getId).collect(Collectors.toSet()))
            .stream().collect(Collectors.toMap(Accessory::getId, m -> m));

        //LOAD Accessories
        act.getAccessories();
        Integer productCount = act.getAccessories().size();
        BigDecimal totalAmount = act.getTotalPriceWithVat() != null ? act.getTotalPriceWithVat() : BigDecimal.ZERO;
        for (RentActAccessoryRequestDto accessory : dto.getAccessories()) {
            RentActAccessory actAccessory = new RentActAccessory();
            actAccessory.setAccessoryId(accessory.getId());
            actAccessory.setAccessory(accessories.get(accessory.getId()));
            actAccessory.setDeviceSerialNumber(accessory.getDeviceSerialNumber());
            actAccessory.setCustomerId(act.getCustomerId());
            if (act.getAccessories().contains(actAccessory)) {
                throw validationService.entityAlreadyExistExceptionThrow(DEVICE, SERIAL_NUMBER, accessory.getDeviceSerialNumber()).get();
            }
            act.addAccessory(actAccessory);
            productCount++;
            totalAmount = totalAmount.add(accessories.get(accessory.getId()).getRentPrice());
        }

        boolean isDeviceExist = false;
        for (RentActAccessory accessory : act.getAccessories()) {
            if (AccessoryType.DEVICE.equals(accessory.getAccessory().getAccessoryType())) {
                isDeviceExist = true;
                break;
            }
        }

        if (!isDeviceExist) {
            throw validationService.requiredExceptionThrow(DEVICE).get();
        }

        boolean isBalanceEnough = ledgerService.isBalanceEnoughForRent(act.getCustomer().getTin(), totalAmount);
        if (!isBalanceEnough) {
            throw validationService.badRequestExceptionThrow(BALANCE_NOT_ENOUGH).get();
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

        return rentActMapper.toDto(act);
    }

    @Override
    public RentActResponseDto update(RentActUpdateRequestDto dto) {
        RentAct act = findById(dto.getId());
        checkStatus(act);

//        Customer customer = customerService.findById(dto.getCustomerId());
//        RentOffer offer = rentOfferService.getByIdAndCustomerId(dto.getOfferId(), dto.getCustomerId());
//        act.setOfferId(offer.getId());
//        act.setCustomerId(customer.getId());
//        act.setStatus(ContractStatus.CREATED);
//        act.getAccessories().clear();

//        Set<Accessory> accessories = accessoryRepository.findByIdIn(dto.getAccessories().keySet());
//        Integer productCount = 0;
//        BigDecimal totalAmount = BigDecimal.ZERO;
//        for (Accessory accessory : accessories) {
//            RentActAccessory actAccessory = new RentActAccessory();
//            actAccessory.setAccessoryId(accessory.getId());
//            actAccessory.setDeviceSerialNumber(dto.getAccessories().get(accessory.getId()));
//            act.addAccessory(actAccessory);
//            productCount++;
//            totalAmount = totalAmount.add(accessory.getPrice());
//        }
//        act.setTotalProductCount(productCount);
//        act.setTotalPriceWithVat(totalAmount);
//        save(act);

        return rentActMapper.toDto(act);
    }


    public RentActResponseDto updateSerialNumber(RentActUpdateRequestDto dto) {
        RentAct act = findById(dto.getId());
        act.setStatus(ContractStatus.ACCEPTED);

        Accessory accessory = accessoryRepository.findById(1L).orElseThrow(validationService.entityNotFoundExceptionThrow(ACCESSORIES));
        Set<RentActAccessory> accessories = act.getAccessories();
        for (RentActAccessory rentActAccessory : accessories) {
            Accessory myAccessory = rentActAccessory.getAccessory();
            if (myAccessory.equals(accessory)) {
                rentActAccessory.setDeviceSerialNumber("sadas");
            }
        }
        save(act);

        return rentActMapper.toDto(act);
    }

    @Override
    public RentActResponseDto getById(Long id) {
        return rentActMapper.toDto(findById(id));
    }

    @Override
    public String getHtmlTemplate(Long id) {
        RentAct act = repository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(ACT));
        if (act.getAccessories().isEmpty()) {
            throw validationService.entityNotFoundExceptionThrow(ACCESSORIES).get();
        }
        return thymeleafService.generateRentAct(act);
    }

    @Override
    public RentActResponseDto uploadFile(Long id, MultipartFile file) {
        RentAct act = findById(id);
        if (CollectionUtils.isEmpty(act.getAccessories())) {
            throw validationService.requiredExceptionThrow(ACCESSORIES).get();
        }
        checkStatus(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.RENT_ACT.name(), DocumentType.RENT_ACT.name());
        act.setFileName(response.getFileName());
        act.setContentType(file.getContentType());
        act.setFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return rentActMapper.toDto(act);
    }

    @Override
    public RentActResponseDto uploadImage(Long id, MultipartFile file) {
        RentAct act = findById(id);
        checkStatus(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.RENT_ACT_IMAGE.name(), DocumentType.RENT_ACT_IMAGE.name());
        act.setImageFileName(response.getFileName());
        act.setImageContentType(file.getContentType());
        act.setImageFileUri(response.getFileDownloadUri());
        act = save(act);
        return rentActMapper.toDto(act);
    }

    private void checkStatus(RentAct act) {
        if (ContractStatus.REJECTED.equals(act.getStatus()) ||
            ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.statusChangeExceptionThrow(act.getStatus().getName()).get();
    }

    @Override
    public RentActResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file) {
        RentAct act = findById(id);
        if (CollectionUtils.isEmpty(act.getAccessories())) {
            throw validationService.requiredExceptionThrow(ACCESSORIES).get();
        }
        checkStatus(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.RENT_ACT_POWER_ATTORNEY.name(), DocumentType.RENT_ACT_POWER_ATTORNEY.name());
        act.setPowerAttorneyFileName(response.getFileName());
        act.setPowerAttorneyContentType(file.getContentType());
        act.setPowerAttorneyFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return rentActMapper.toDto(act);
    }

    @Override
    @Transactional
    public RentActResponseDto accept(Long id) {
        RentAct act = findById(id);
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

        act.setStatus(ContractStatus.ACCEPTED);
        act.setAcceptDate(LocalDateTime.now());

        List<RentPaymentHistory> histories = new ArrayList<>();
        for (RentActAccessory accessory : act.getAccessories()) {
            RentPaymentHistory history = new RentPaymentHistory();
            LocalDate now = LocalDate.now();
            LocalDate dueDate = DateUtil.calculateDueDate(now);
            BigDecimal calculatedAmount =
                BigDecimalUtil.calculatePrice(now,
                    dueDate,
                    accessory.getAccessory().getRentPrice());
            history.setAmount(BigDecimalUtil.setScaleByTwo(calculatedAmount));
            history.setId(new RentPaymentHistoryId(
                act.getCustomerId(),
                accessory.getDeviceSerialNumber(),
                now.getMonth(),
                now.getYear()
            ));
            history.setDueDate(dueDate);
            history.setRentActAccessoryId(accessory.getId());
            history.setCustomer(act.getCustomer());
            history.setOfferId(act.getOfferId());
            history.setStartDate(now);
            histories.add(history);

            accessory.setActive(true);
            accessory.setStartDate(now);
            accessory.setDueDate(dueDate);
        }
        act = save(act);
        rentPaymentHistoryService.saveAll(histories);

        return rentActMapper.toDto(act);
    }

    @Override
    public RentActResponseDto changeToAttach(Long id) {
        RentAct act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(act);
        return rentActMapper.toDto(act);
    }

    @Override
    public RentActResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto) {
        RentAct act = findById(dto.getId());
        act.setDocFlowStatus(dto.getDocFlowStatus());
        act.setDocFlowReason(dto.getDocFlowReason());
        save(act);
        return rentActMapper.toDto(act);
    }

    @Override
    public String generatePoaTemplate(TradeInActPoaRequestDto dto) {
        RentAct act = findById(dto.getActId());
        return thymeleafService.generateRentActPoaHTML(act, dto);
    }
}
