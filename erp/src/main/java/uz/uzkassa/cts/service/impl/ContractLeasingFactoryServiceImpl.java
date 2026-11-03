package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
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
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.ContractLeasingStrategyService;
import uz.uzkassa.cts.service.ContractLeasingFactoryService;
import uz.uzkassa.cts.service.ModelPriceHistoryService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ContractLeasingFactoryServiceImpl extends BaseServiceImpl<ContractForLeasing, Long> implements ContractLeasingFactoryService {

    private final ModelPriceHistoryService modelPriceHistoryService;
    private final ContractForLeasingRepository comContractForLeasingRepository;
    private final Map<String, ContractLeasingStrategyService> contractForLeasingService;
    private final ContractFroLeasingMapper contractFroLeasingMapper;

    public ContractLeasingFactoryServiceImpl(ModelPriceHistoryService modelPriceHistoryService,
                                             ContractForLeasingRepository comContractForLeasingRepository,
                                             Map<String, ContractLeasingStrategyService> contractForLeasingService,
                                             ContractFroLeasingMapper contractFroLeasingMapper) {
        super(comContractForLeasingRepository);
        this.modelPriceHistoryService = modelPriceHistoryService;
        this.comContractForLeasingRepository = comContractForLeasingRepository;
        this.contractForLeasingService = contractForLeasingService;
        this.contractFroLeasingMapper = contractFroLeasingMapper;
    }

    @Override
    public ContractForLeasingResponseDto create(ContractForLeasingRequestDto dto) {
        ModelPriceHistory modelPriceHistory = modelPriceHistoryService.findById(dto.getModelPriceHistoryId());
        return contractForLeasingService.get(modelPriceHistory.getCompanyType().name()).create(dto);
    }

    @Override
    public ContractForLeasingResponseDto addDevice(Long id, ContractForLeasingDeviceRequestDto deviceRequestDto) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).addDevice(id, deviceRequestDto);
    }

    @Override
    public String getContractHtmlTemplate(Long id) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).getContractHtmlTemplate(id);
    }

    @Override
    public String getOfferHtmlTemplate() {
        return contractForLeasingService.get(CompanyType.PRIME.name()).getContractHtmlTemplate(null);
    }

    @Override
    public String getWarrantyHtmlTemplate(Long id) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).getWarrantyHtmlTemplate(id);
    }

    @Override
    public ContractForLeasingResponseDto uploadContractFile(Long id, MultipartFile file) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).uploadContractFile(id, file);
    }

    @Override
    public ContractForLeasingResponseDto uploadWarrantyFile(Long id, MultipartFile file) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).uploadWarrantyFile(id, file);
    }

    @Override
    public ContractForLeasingResponseDto accept(Long id) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).accept(id);
    }

    @Override
    public ContractForLeasingResponseDto changeToAttach(Long id) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).changeToAttach(id);
    }

    @Override
    public ContractForLeasingDeviceCheckResponseDto getForDeviceAdd(Long id) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).getForDeviceAdd(id);
    }

    @Override
    public List<ContractForLeasingDeviceCheckResponseDto> getForDeviceAddByCustomerId(Long customerId, String companyType, String deviceType) {
        if (StringUtils.isEmpty(companyType)) return null;
        return contractForLeasingService.get(companyType).getForDeviceAddByCustomerId(customerId, deviceType);
    }

    @Override
    public void earlyCloseContract(Long id) {
        ContractForLeasing contractForLeasing = findById(id);
        contractForLeasingService.get(contractForLeasing.getCompanyType().name()).earlyCloseContract(id);
    }

    @Override
    public List<CommonCodeDTO> getItems(String type) {
        if (StringUtils.isEmpty(type)) return null;
        return contractForLeasingService.get(type).getItems();
    }

    @Override
    public ContractForLeasingResponseDto reject(Long id) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).reject(id);
    }

    @Override
    public ContractForLeasingResponseDto rejectWithActs(Long id) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).rejectWithActs(id);
    }

    @Override
    public String getPaymentQuote(Long id) {
        ContractForLeasing contractForLeasing = findById(id);
        return contractForLeasingService.get(contractForLeasing.getCompanyType().name()).getPaymentQuote(id);
    }

    @Override
    public void verifyCode(Long id, String code) {
        ContractForLeasing leasing = findById(id);
        if (!Objects.equals(leasing.getActivationCode(), code)) {
            throw validationService.badRequestExceptionThrow(INCORRECT_ACTIVATION_CODE).get();
        }
        leasing.setActivationDate(LocalDateTime.now());
        leasing.setStatus(ContractStatus.ACCEPTED);
        save(leasing);
    }

    @Override
    public Page<ContractForLeasingListResponseDto> listByTin(ContractForLeasingFilter filter, String tin) {
        return null;
    }

    @Override
    public Page<ContractForLeasingListResponseDto> list(ContractForLeasingFilter filter) {
        Specification<ContractForLeasing> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            Path<Long> branchIdPath = root.get(ContractForLeasing_.BRANCH_ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(), Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null) {
                    list.add(criteriaBuilder.equal(branchIdPath, filter.getBranchId()));
                }
            } else {
                list.add(criteriaBuilder.equal(branchIdPath, SecurityUtils.getBranchId()));
            }
            Path<Customer> customer = root.join(ContractForLeasing_.CUSTOMER, JoinType.INNER);
            Path<String> tin = customer.get(Customer_.TIN);
            if (StringUtils.isNotEmpty(filter.getTin())) {
                list.add(criteriaBuilder.equal(tin, filter.getTin()));
            }

            Path<Long> companyIdPath = root.get(ContractForLeasing_.COMPANY_ID);
            if (filter.getCompanyId() != null) {
                list.add(criteriaBuilder.equal(companyIdPath, filter.getCompanyId()));
            }
            if (filter.getBranchId() != null) {
                list.add(criteriaBuilder.equal(branchIdPath, filter.getBranchId()));
            }
            if (!isSuperAdmin()) {
                list.add(criteriaBuilder.equal(companyIdPath, getCompanyId()));
            }
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(ContractForLeasing_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(ContractForLeasing_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getCompanyType() != null) {
                Path<CompanyType> companyType = root.get(ContractForLeasing_.COMPANY_TYPE);
                list.add(criteriaBuilder.equal(companyType, filter.getCompanyType()));
            }
            if (filter.getDeviceType() != null) {
                Path<DeviceType> deviceType = root.get(ContractForLeasing_.DEVICE_TYPE);
                list.add(criteriaBuilder.equal(deviceType, filter.getDeviceType()));
            }
            if (filter.getCreatorId() != null) {
                Path<User> user = root.join(ContractForLeasing_.CREATOR, JoinType.INNER);
                Path<Long> userId = user.get(User_.ID);
                list.add(criteriaBuilder.equal(userId, filter.getCreatorId()));
            }
            if (filter.getTariffId() != null) {
                Path<Long> tariffPath = root.get(ContractForLeasing_.MODEL_PRICE_HISTORY_ID);
                list.add(criteriaBuilder.equal(tariffPath, filter.getTariffId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Specification<ContractForLeasing> spec = specification;
        if (ActExistEnum.WITH_ACT.equals(filter.getActExistEnum()))
            spec = Specification.where(specification).and(getExistAct());
        if (ActExistEnum.WITHOUT_ACT.equals(filter.getActExistEnum()))
            spec = Specification.where(specification).and(getNotExistAct());
        Page<ContractForLeasing> contracts = comContractForLeasingRepository.findAll(spec, filter.getOrderedPageable());

        return contracts.map(contractFroLeasingMapper::toListDto);
    }

    public static Specification<ContractForLeasing> getExistAct() {
        return (root, query, cb) -> {
            Subquery<ActOfTransmission> sq = query.subquery(ActOfTransmission.class);
            Root<ActOfTransmission> act = sq.from(ActOfTransmission.class);
            sq.where(cb.equal(act.get(ActOfTransmission_.CONTRACT), root)).select(act);
            return cb.exists(sq);
        };
    }

    public static Specification<ContractForLeasing> getNotExistAct() {
        return (root, query, cb) -> {
            Subquery<ActOfTransmission> sq = query.subquery(ActOfTransmission.class);
            Root<ActOfTransmission> act = sq.from(ActOfTransmission.class);
            sq.where(cb.equal(act.get(ActOfTransmission_.CONTRACT), root)).select(act);
            return cb.not(cb.exists(sq));
        };
    }

    @Override
    public ContractForLeasingResponseDto getById(Long id) {
        return contractFroLeasingMapper.toDto(findById(id));
    }
}
