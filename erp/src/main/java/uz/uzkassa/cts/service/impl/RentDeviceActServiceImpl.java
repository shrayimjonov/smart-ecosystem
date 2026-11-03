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
import uz.uzkassa.cts.dto.rentDeviceAct.*;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.RentActFilter;
import uz.uzkassa.cts.mappings.RentDeviceActMapper;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class RentDeviceActServiceImpl extends BaseServiceImpl<RentDeviceAct, Long> implements RentDeviceActService {

    private final CustomerOldService customerOldService;
    private final RentDeviceActRepository repository;
    private final FileUploadService fileUploadService;
    private final VatService vatService;
    private final ThymeleafService thymeleafService;
    private final DeviceService deviceService;
    private final TradeInPriceHistoryRepository tradeInPriceHistoryRepository;
    private final UserRepository userRepository;
    private final RentDeviceActDetailRepository actDetailRepository;
    private final RentDeviceActMapper rentDeviceActMapper;
    private final CustomerEmployeeRepository customerEmployeeRepository;
    private final AddressService addressService;

    public RentDeviceActServiceImpl(RentDeviceActRepository repository,
                                    CustomerOldService customerOldService,
                                    FileUploadService fileUploadService,
                                    VatService vatService,
                                    ThymeleafService thymeleafService,
                                    DeviceService deviceService,
                                    TradeInPriceHistoryRepository tradeInPriceHistoryRepository,
                                    UserRepository userRepository, RentDeviceActDetailRepository actDetailRepository,
                                    RentDeviceActMapper rentDeviceActMapper, CustomerEmployeeRepository customerEmployeeRepository, AddressService addressService) {
        super(repository);
        this.repository = repository;
        this.customerOldService = customerOldService;
        this.fileUploadService = fileUploadService;
        this.vatService = vatService;
        this.thymeleafService = thymeleafService;
        this.deviceService = deviceService;
        this.tradeInPriceHistoryRepository = tradeInPriceHistoryRepository;
        this.userRepository = userRepository;
        this.actDetailRepository = actDetailRepository;
        this.rentDeviceActMapper = rentDeviceActMapper;
        this.customerEmployeeRepository = customerEmployeeRepository;
        this.addressService = addressService;
    }

    @Override
    public Page<RentDeviceActListDTO> getList(RentActFilter filter) {
        Specification<RentDeviceAct> specification = getSpecification(filter);
        Page<RentDeviceAct> acts = repository.findAll(specification, filter.getOrderedPageable());

        return acts.map(rentDeviceActMapper::toListDto);
    }

    private Specification<RentDeviceAct> getSpecification(RentActFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<RentDeviceAct, Customer> customer = root.join(RentDeviceAct_.CUSTOMER, JoinType.INNER);
            Join<Customer, Branch> branchJoin = customer.join(Customer_.BRANCHES, JoinType.INNER);
            Join<RentDeviceAct, RentDeviceActDetail> rentDeviceActDetailJoin = root.join(RentDeviceAct_.RENT_DEVICE_ACT_DETAIL, JoinType.INNER);
            Join<RentDeviceActDetail, Device> deviceJoin = rentDeviceActDetailJoin.join(RentDeviceActDetail_.DEVICE, JoinType.INNER);

            ArrayList<Predicate> list = new ArrayList<>();
            Path<Long> branchIdPath = branchJoin.get(Branch_.ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(), Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null) {
                    list.add(criteriaBuilder.equal(branchIdPath, filter.getBranchId()));
                }
            } else {
                list.add(criteriaBuilder.equal(branchIdPath, SecurityUtils.getBranchId()));
            }
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> tin = customer.get(Customer_.TIN);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);
                Path<String> deviceSerialNumber = deviceJoin.get(Device_.SERIAL_NUMBER);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                Predicate deviceSerialNumberPred = criteriaBuilder.like(criteriaBuilder.lower(deviceSerialNumber), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred, deviceSerialNumberPred));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> contractStatusPath = root.get(RentDeviceAct_.STATUS);
                list.add(criteriaBuilder.equal(contractStatusPath, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getActDocFlowStatus() != null) {
                Path<ActDocFlowStatus> actDocFlowStatusPath = root.get(RentDeviceAct_.DOC_FLOW_STATUS);
                list.add(criteriaBuilder.equal(actDocFlowStatusPath, ActDocFlowStatus.valueOf(filter.getActDocFlowStatus())));
            }
            if (filter.getInsuranceStatus() != null) {
                Path<ActInsuranceStatus> actInsuranceStatusPath = rentDeviceActDetailJoin.get(RentDeviceActDetail_.INSURANCE_STATUS);
                list.add(criteriaBuilder.equal(actInsuranceStatusPath, ActInsuranceStatus.valueOf(filter.getInsuranceStatus())));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(RentDeviceAct_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getCreatorId() != null) {
                Path<Long> creatorId = root.get(RentDeviceAct_.CREATOR_ID);
                list.add(criteriaBuilder.equal(creatorId, filter.getCreatorId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public void create(RentDeviceActPayload deviceActPayload) {
        Customer customer = customerOldService.findById(deviceActPayload.getCustomerId());
        List<RentDeviceAct> deviceActs = repository.findByCustomerIdAndStatusIn(deviceActPayload.getCustomerId(),
                List.of(ContractStatus.CREATED, ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT));
        if (!deviceActs.isEmpty()) {
            throw validationService.requiredExceptionThrow(ACT).get();
        }

        Device device = deviceService.findById(deviceActPayload.getDeviceId());
        TradeInPriceHistory tradeInPriceHistory = tradeInPriceHistoryRepository.findFirstByActiveTrueAndContractTypeAndDeviceModelId(ContractType.PAYMENT_CONTRACT, device.getModelId());
        Vat vat = vatService.getActiveVat();
        RentDeviceAct act = new RentDeviceAct();
//        act.setRentDeviceActDetail(deviceActDetail);
        act.setBranchId(SecurityUtils.getBranchId());
        act.setCompanyId(getCompanyId());
        act.setCustomerId(customer.getId());
        act.setStatus(ContractStatus.CREATED);
        act.setPriceFor1pc(tradeInPriceHistory.getPrice1pc());
        act.setPriceFor1pcWithVat(tradeInPriceHistory.getPrice1pcWithVat());
        act.setTotalProductCount(1);
        act.setVat1pc(vat.getPercent());
        act.setTotalPriceWithVat(tradeInPriceHistory.getPrice1pcWithVat());
        save(act);

        RentDeviceActDetail deviceActDetail = new RentDeviceActDetail();
        deviceActDetail.setDeviceId(deviceActPayload.getDeviceId());
        deviceActDetail.setCustomerId(deviceActPayload.getCustomerId());
        deviceActDetail.setTradeInPriceId(tradeInPriceHistory.getId());
        deviceActDetail.setDeviceAct(act);
        actDetailRepository.save(deviceActDetail);
    }

    @Override
    public RentDeviceActDTO getById(Long id) {
        return rentDeviceActMapper.toDto(findById(id));
    }

    @Override
    public String getHtmlTemplate(Long id) {
        RentDeviceAct act = repository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(RENT));
        if (act.getRentDeviceActDetail() == null) {
            throw validationService.entityNotFoundExceptionThrow(ACCESSORIES).get();
        }
        Customer customer = act.getCustomer();
        RentDeviceActTemplateDTO dto = new RentDeviceActTemplateDTO();
        dto.setCompanyName(customer.getCompanyName());
        dto.setModelName(act.getRentDeviceActDetail().getDevice().getModel().getName());
        dto.setSerialNumber(act.getRentDeviceActDetail().getDevice().getSerialNumber());
        dto.setPrice(act.getPriceFor1pcWithVat());
        dto.setCreatedDate(act.getCreatedDate());
        Optional.ofNullable(addressService.getLegalAddress(act.getCompanyId(), act.getCompany().getEntityType())).ifPresent(address -> dto.setAddress(address.getAddress()));
        customerEmployeeRepository.findFirstByCustomerIdAndRole(act.getCustomerId(), Role.DIRECTOR).ifPresent(customerEmployee -> {
            dto.setOwner(customerEmployee.getName());
            dto.setLogin(customerEmployee.getCustomer().getPhone());
        });
        return thymeleafService.generateRentDeviceAct(dto);
    }

    @Override
    public RentDeviceActDTO uploadFile(Long id, MultipartFile file) {
        RentDeviceAct act = findById(id);
        if (act.getRentDeviceActDetail() == null) {
            throw validationService.requiredExceptionThrow(ACCESSORIES).get();
        }
        checkStatus(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.RENT_DEVICE_ACT.name(), DocumentType.RENT_DEVICE_ACT.name());
        act.setFileName(response.getFileName());
        act.setFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return rentDeviceActMapper.toDto(act);
    }

    @Override
    @Transactional
    public RentDeviceActDTO accept(Long id) {
        RentDeviceAct rentDeviceAct = findById(id);
        if (ContractStatus.ACCEPTED == rentDeviceAct.getStatus()) {
            throw validationService.badRequestExceptionThrow(ACT_ALREADY_CONFIRMED).get();
        }
        if (ContractStatus.REJECTED == rentDeviceAct.getStatus()) {
            throw validationService.badRequestExceptionThrow(REJECTED_ACT).get();
        }
        if (StringUtils.isEmpty(rentDeviceAct.getFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (rentDeviceAct.getRentDeviceActDetail() == null) {
            throw validationService.requiredExceptionThrow(ACCESSORIES).get();
        }
        rentDeviceAct.setStatus(ContractStatus.ACCEPTED);
        rentDeviceAct.setAcceptDate(LocalDateTime.now());
        save(rentDeviceAct);

//        customerOldService.attachmentBankTariff(rentDeviceAct.getCustomerId(), null);
        return rentDeviceActMapper.toDto(rentDeviceAct);
    }

    @Override
    @Transactional
    public RentDeviceActDTO reject(Long id) {
        RentDeviceAct rentDeviceAct = findById(id);
        if (ContractStatus.REJECTED.equals(rentDeviceAct.getStatus()) || ContractStatus.ACCEPTED.equals(rentDeviceAct.getStatus())) {
            throw validationService.badRequestExceptionThrow(STATUS_CANNOT_CHANGE, rentDeviceAct.getStatus().getName()).get();
        }
        rentDeviceAct.setStatus(ContractStatus.REJECTED);
        save(rentDeviceAct);
        return rentDeviceActMapper.toDto(rentDeviceAct);
    }

    @Override
    public RentDeviceActDTO changeToAttach(Long id) {
        RentDeviceAct act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(act);
        return rentDeviceActMapper.toDto(act);
    }

    @Override
    public RentDeviceActDTO changeDocFlowStatus(ActChangeDocFlowRequestDto dto) {
        RentDeviceAct act = findById(dto.getId());
        act.setDocFlowStatus(dto.getDocFlowStatus());
        act.setDocFlowReason(dto.getDocFlowReason());
        save(act);
        return rentDeviceActMapper.toDto(act);
    }

    @Override
    public void updateInsuranceStatus(Long id, ActInsuranceStatus insuranceStatus) {
        RentDeviceAct act = findById(id);
        if (Objects.isNull(act.getRentDeviceActDetail())) {
            throw validationService.entityNotFoundExceptionThrow(RENT).get();
        }
        act.getRentDeviceActDetail().setInsuranceStatus(insuranceStatus);
        save(act);
    }

    @Override
    public String generatePowerOfAttorneyTemplate(RentDeviceActPowerOfAttorneyDTO dto) {
        RentDeviceAct rentDeviceAct = findById(dto.getActId());
        return thymeleafService.generateRentDeviceActPowerOfAttorneyHTML(rentDeviceAct, dto);
    }

    @Override
    public RentDeviceActDTO uploadPowerAttorneyFile(Long id, MultipartFile file) {
        RentDeviceAct rentDeviceAct = findById(id);
        checkStatus(rentDeviceAct);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.RENT_DEVICE_ACT_POWER_ATTORNEY.name(), DocumentType.RENT_DEVICE_ACT_POWER_ATTORNEY.name());
        rentDeviceAct.setPowerAttorneyFileName(response.getFileName());
        rentDeviceAct.setPowerAttorneyFileUri(response.getFileDownloadUri());
        rentDeviceAct.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        rentDeviceAct = save(rentDeviceAct);
        return rentDeviceActMapper.toDto(rentDeviceAct);
    }

    private void checkStatus(RentDeviceAct act) {
        if (ContractStatus.REJECTED.equals(act.getStatus()) ||
                ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.statusChangeExceptionThrow(act.getStatus().getName()).get();
    }
}
