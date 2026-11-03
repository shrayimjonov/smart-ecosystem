package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInContractRequestDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInDeviceAddRequestDto;
import uz.uzkassa.cts.dto.tradeincontract.response.TradeInContractResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.TradeInContractFilter;
import uz.uzkassa.cts.mappings.TradeInContractMapper;
import uz.uzkassa.cts.repository.CustomerDeviceRepository;
import uz.uzkassa.cts.repository.TradeInContractRepository;
import uz.uzkassa.cts.repository.TradeInLetterOfGuaranteeRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uz.uzkassa.cts.utils.PredicateUtil.between;
import static uz.uzkassa.cts.utils.PredicateUtil.with;

@Transactional
@Service
@Slf4j
public class TradeInContractServiceImpl extends BaseServiceImpl<TradeInContract, Long> implements TradeInContractService {

    private final TradeInContractRepository tradeInContractRepository;
    private final TradeInPriceHistoryService tradeInPriceHistoryService;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final DeviceService deviceService;
    private final CustomerDeviceRepository customerDeviceRepository;
    private final TradeInLetterOfGuaranteeRepository tradeInLetterOfGuaranteeRepository;
    private final TradeInContractMapper tradeInContractMapper;

    public TradeInContractServiceImpl(TradeInContractRepository tradeInContractRepository,
                                      TradeInPriceHistoryService tradeInPriceHistoryService,
                                      ThymeleafService thymeleafService,
                                      FileUploadService fileUploadService,
                                      DeviceService deviceService,
                                      CustomerDeviceRepository customerDeviceRepository,
                                      TradeInLetterOfGuaranteeRepository tradeInLetterOfGuaranteeRepository,
                                      TradeInContractMapper tradeInContractMapper) {
        super(tradeInContractRepository);
        this.tradeInContractRepository = tradeInContractRepository;
        this.tradeInPriceHistoryService = tradeInPriceHistoryService;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.deviceService = deviceService;
        this.customerDeviceRepository = customerDeviceRepository;
        this.tradeInLetterOfGuaranteeRepository = tradeInLetterOfGuaranteeRepository;
        this.tradeInContractMapper = tradeInContractMapper;
    }

    @Override
    @Transactional
    public TradeInContractResponseDto create(TradeInContractRequestDto tradeInContractDTO) {
        TradeInPriceHistory history = tradeInPriceHistoryService.findById(tradeInContractDTO.getTradeInPriceHistoryId());
        if (!history.isActive()) {
            throw validationService.entityNotFoundExceptionThrow(CONTRACT).get();
        }

        List<TradeInContract> contracts = tradeInContractRepository.findByCustomerId(tradeInContractDTO.getCustomerId());
        for (TradeInContract con : contracts) {
            if (!ContractStatus.REJECTED.equals(con.getStatus())) {
                if (con.getTradeInAct() == null) {
                    throw validationService.requiredExceptionThrow(CONTRACT).get();
                }
                if (!ContractStatus.ACCEPTED.equals(con.getTradeInAct().getStatus())) {
                    throw validationService.requiredExceptionThrow(CONTRACT).get();
                }
            }
        }

        TradeInContract tradeInContract = new TradeInContract();
        tradeInContract.setBranchId(SecurityUtils.getBranchId());
        tradeInContract.setCompanyId(getCompanyId());
        tradeInContract.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        tradeInContract.setEndDate(LocalDateTime.now().plusMonths(6)); // уточнить
        tradeInContract.setCustomerId(tradeInContractDTO.getCustomerId());

        tradeInContract.setTotalPriceWithVat(history.getPriceWithVat());
        tradeInContract.setVat1pc(history.getVat1pc());
        tradeInContract.setPriceFor1pc(history.getPrice1pc());
        tradeInContract.setPriceFor1pcWithVat(history.getPrice1pcWithVat());
        tradeInContract.setTotalProductCount(1);
        tradeInContract.setTradeInPriceHistoryId(tradeInContractDTO.getTradeInPriceHistoryId());
        tradeInContract = save(tradeInContract);

        if (history.isRequiresLetterOfGuarantee()) {
            if (tradeInContractDTO.getTradeInLetterOfGuaranteeId() == null) {
                throw validationService.requiredExceptionThrow(GUARANTEE_LETTER).get();
            }
            TradeInLetterOfGuarantee guarantee = tradeInLetterOfGuaranteeRepository
                    .findById(tradeInContractDTO.getTradeInLetterOfGuaranteeId())
                    .orElseThrow(validationService.entityNotFoundExceptionThrow(GUARANTEE_LETTER));

            if (!guarantee.getCustomerId().equals(tradeInContractDTO.getCustomerId())) {
                throw validationService.entityNotFoundExceptionThrow(GUARANTEE_LETTER).get();
            }

            if (ContractStatus.ACCEPTED != guarantee.getStatus()) {
                throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
            }

            guarantee.setTradeInContractId(tradeInContract.getId());
            guarantee.setStatus(ContractStatus.CLOSED);
            tradeInLetterOfGuaranteeRepository.save(guarantee);
        }

        return tradeInContractMapper.toDto(tradeInContract);
    }

    @Override
    public TradeInContractResponseDto getById(Long id) {
        return tradeInContractMapper.toDto(findById(id));
    }


    @Override
    public Page<TradeInContractResponseDto> list(TradeInContractFilter filter) {
        Specification<TradeInContract> specification = (root, criteriaQuery, builder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            Path<Long> branchIdPath = root.get(TradeInContract_.BRANCH_ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(), Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null)
                    list.add(builder.equal(branchIdPath, filter.getBranchId()));
            } else
                list.add(builder.equal(branchIdPath, SecurityUtils.getBranchId()));

            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<Customer> customer = root.join(TradeInContract_.CUSTOMER, JoinType.INNER);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);
                Path<String> tin = customer.get(Customer_.TIN);

                Predicate customerNamePred = builder.like(builder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = builder.like(builder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = builder.like(builder.lower(tin), filter.getSearchForQuery());

                list.add(builder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(TradeInContract_.CREATED_DATE);
                list.add(builder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }

            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(TradeInContract_.STATUS);
                list.add(builder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getCreatorId() != null) {
                Path<User> user = root.join(TradeInContract_.CREATOR, JoinType.INNER);
                Path<Long> userId = user.get(User_.ID);
                list.add(builder.equal(userId, filter.getCreatorId()));
            }
            return builder.and(list.toArray(new Predicate[0]));
        };
        Specification<TradeInContract> spec = specification;
        if (ActExistEnum.WITH_ACT.equals(filter.getActExistEnum()))
            spec = Specification.where(specification).and(getExistAct());
        if (ActExistEnum.WITHOUT_ACT.equals(filter.getActExistEnum()))
            spec = Specification.where(specification).and(getNotExistAct());

        return tradeInContractRepository.findAll(spec, filter.getOrderedPageable())
                .map(tradeInContractMapper::toDto);
    }


    public Page<TradeInContractResponseDto> getList(TradeInContractFilter filter) {
        if (!SecurityUtils.isCurrentUserInRoles(
                Role.ADMIN.getRoleCode(),
                Role.ACCOUNTANT.getRoleCode())
        ) {
            filter.setBranchId(SecurityUtils.getBranchId());
        }
        Specification<TradeInContract> specification = (root, criteriaQuery, builder) -> {
            criteriaQuery.distinct(true);

            Predicate and = builder.and(Stream.of(
                    with(filter::getBranchId, a -> builder.equal(root.get(TradeInContract_.branchId), a)),
                    //with(filter.getStatus(), a -> builder.equal(root.get(TradeInContract_.status), a)),
                    with(filter::getCreatorId, a -> builder.equal(root.get(TradeInContract_.creatorId), a)),
                    between(filter::getFrom, filter::getTo, (a, b) -> builder.between(root.get(TradeInContract_.CREATED_DATE), a, b))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));

            Path<Customer> customer = root.join(TradeInContract_.CUSTOMER, JoinType.INNER);
            Predicate or = builder.or(Stream.of(
                    with(filter::getSearch, a -> builder.like(builder.lower(customer.get(Customer_.NAME)), a)),
                    with(filter::getSearch, a -> builder.like(builder.lower(customer.get(Customer_.COMPANY_NAME)), a)),
                    with(filter::getSearch, a -> builder.like(builder.lower(customer.get(Customer_.TIN)), a))
            ).filter(Objects::nonNull).toArray(Predicate[]::new));

            return builder.or(and, or);
        };
        return tradeInContractRepository.findAll(specification, filter.getOrderedPageable())
                .map(tradeInContractMapper::toDto);
    }

//    public List<TradeInContractTestResponseDto> getList(TradeInContractFilter filter) {
//        SelectQuery contracts = criteriaApiHelper.select(TradeInContract.class, TradeInContractTestResponseDto.class);
//        if (filter.getStatus() != null)
//            contracts.equal(TradeInContract_.STATUS, filter.getStatus());
//        if (StringUtils.isNotEmpty(filter.getSearch())) {
//            contracts.or();
//            contracts.join(TradeInContract_.customer, JoinType.INNER)
//                .like(TradeInContract_.customer, Customer_.name, filter.getSearch())
//                .like(TradeInContract_.customer, Customer_.companyName, filter.getSearch())
//                .like(TradeInContract_.customer, Customer_.tin, filter.getSearch());
//        }
//        contracts.and();
//        if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(), Role.COMPANY_ADMIN.getRoleCode(), Role.COMPANY_ACCOUNTANT.getRoleCode())) {
//            if (filter.getBranchId() != null)
//                contracts.equal(TradeInContract_.branchId, filter.getBranchId());
//        } else {
//            contracts.equal(TradeInContract_.branchId, SecurityUtils.getBranchId());
//        }
//        if (filter.getFrom() != null && filter.getTo() != null) {
//            contracts.between(TradeInContract_.CREATED_DATE, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX));
//        }
//        if (filter.getCreatorId() != null) {
//            contracts.equal(TradeInContract_.creator, User_.id, filter.getCreatorId());
//        }
//        return contracts.findAll();
//    }

    public static Specification<TradeInContract> getExistAct() {
        return (root, query, cb) -> cb.isNotNull(root.join(TradeInContract_.TRADE_IN_ACT, JoinType.LEFT));
    }

    public static Specification<TradeInContract> getNotExistAct() {
        return (root, query, cb) -> cb.isNull(root.join(TradeInContract_.TRADE_IN_ACT, JoinType.LEFT));
    }

    public String getContractHtmlTemplate(Long id) {
        TradeInContract contract = findById(id);
        CustomerBank bank = contract.getCustomer().getCustomerBank();
        if (bank == null) {
            throw validationService.entityNotFoundExceptionThrow(BANK).get();
        }
        if (bank.getAccountNumber() == null) {
            throw validationService.entityNotFoundExceptionThrow(ACCOUNT_NUMBER).get();
        }
        return thymeleafService.generateTradeInContractHTML(findById(id));
    }

    @Override
    public TradeInContractResponseDto uploadContractFile(Long id, MultipartFile file) {
        TradeInContract contract = findById(id);
        CheckRejectedStatus(contract);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.TRADE_IN_CONTRACT.name(), DocumentType.TRADE_IN_CONTRACT.name());
        contract.setFileName(response.getFileName());
        contract.setContentType(file.getContentType());
        contract.setFileUri(response.getFileDownloadUri());
        contract.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        save(contract);
        return tradeInContractMapper.toDto(contract);
    }

    @Override
    public Page<TradeInContractResponseDto> listByTin(TradeInContractFilter filter, String tin) {
        Specification<TradeInContract> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            Path<Customer> customer = root.join(TradeInContract_.CUSTOMER, JoinType.INNER);
            Path<String> tinPath = customer.get(Customer_.TIN);
            list.add(criteriaBuilder.equal(tinPath, tin));

            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(TradeInContract_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(TradeInContract_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return tradeInContractRepository
                .findAll(specification, filter.getOrderedPageable())
                .map(tradeInContractMapper::toDto);
    }

    @Override
    public TradeInContractResponseDto addDevice(Long id, TradeInDeviceAddRequestDto dto, boolean skipValidation) {
        TradeInContract contract = findById(id);
        if (contract.getTradeInAct() != null) {
            throw validationService.badRequestExceptionThrow(DEVICE_ADD, contract.getTotalProductCount().toString()).get();
        }
        if (!ContractStatus.ACCEPTED.equals(contract.getStatus())) {
            throw validationService.badRequestExceptionThrow(ACCEPT_CONTRACT, contract.getNumber()).get();
        }

        Device device = deviceService.findById(dto.getDeviceId());
        if (!skipValidation) {
            List<CustomerDevice> customerDevice = customerDeviceRepository.findAllByDeviceId(device.getId());
            if (!customerDevice.isEmpty()) {
                throw validationService.badRequestExceptionThrow(DEVICE_ALREADY_ATTACHED).get();
            }
        }

        generateAct(device.getId(), contract);
        contract = save(contract);

        return tradeInContractMapper.toDto(contract);
    }

    @Override
    public List<TradeInContractResponseDto> getContractWithoutAct(String tin) {
        return tradeInContractRepository.findByCustomerTinAndTradeInActIsNullAndStatus(tin, ContractStatus.ACCEPTED)
                .stream().map(tradeInContractMapper::toDto)
                .collect(Collectors.toList());
    }

    private void generateAct(Long deviceId, TradeInContract contract) {
        TradeInAct act = new TradeInAct();
        act.setDeviceId(deviceId);
        act.setBonus(contract.getTradeInPriceHistory().getBonus());

        act.setTotalProductCount(contract.getTotalProductCount());
        act.setTotalPriceWithVat(contract.getTotalPriceWithVat());
        act.setVat1pc(contract.getVat1pc());
        act.setPriceFor1pc(contract.getPriceFor1pc());
        act.setPriceFor1pcWithVat(contract.getPriceFor1pcWithVat());
        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        act.setContract(contract);
        contract.setTradeInAct(act);
    }

    @Override
    public TradeInContractResponseDto accept(Long id) {
        TradeInContract contract = findById(id);
        if (!ContractStatus.DOCUMENT_ATTACHED.equals(contract.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.DOCUMENT_ATTACHED.getName()).get();
        }
        contract.setStatus(ContractStatus.ACCEPTED);
        save(contract);
        return tradeInContractMapper.toDto(contract);
    }

    @Override
    public TradeInContractResponseDto reject(Long id) {
        TradeInContract tradeInContract = changeStatus(id, ContractStatus.REJECTED);
        return tradeInContractMapper.toDto(tradeInContract);
    }

    @Override
    public TradeInContractResponseDto waitingForAttach(Long id) {
        TradeInContract tradeInContract = findById(id);

        if (ContractStatus.REJECTED.equals(tradeInContract.getStatus()))
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();

        TradeInAct act = tradeInContract.getTradeInAct();
        if (act != null &&
                (ContractStatus.ACCEPTED.equals(act.getStatus()) ||
                        ContractStatus.REJECTED.equals(act.getStatus())))
            throw validationService.statusChangeExceptionThrow(act.getStatus().getName()).get();

        tradeInContract.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        tradeInContract = save(tradeInContract);

        return tradeInContractMapper.toDto(tradeInContract);
    }

    private TradeInContract changeStatus(Long id, ContractStatus contractStatus) {
        TradeInContract tradeInContract = findById(id);
        CheckRejectedStatus(tradeInContract);
        if (tradeInContract.getTradeInAct() != null &&
                !ContractStatus.REJECTED.equals(tradeInContract.getTradeInAct().getStatus()) &&
                ContractStatus.REJECTED.equals(contractStatus))
            throw validationService.badRequestExceptionThrow(CONTRACT_CANNOT_BE_REJECTED).get();

        tradeInContract.setStatus(contractStatus);
        save(tradeInContract);
        return tradeInContract;
    }

    private void CheckRejectedStatus(TradeInContract contract) {
        if (ContractStatus.REJECTED.equals(contract.getStatus()) ||
                ContractStatus.ACCEPTED.equals(contract.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, contract.getStatus().getName()).get();
    }

    @Override
    public TradeInContractResponseDto rejectWithActs(Long id) {
        return null;
    }
}
