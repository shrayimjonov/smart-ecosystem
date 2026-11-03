package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.PaymentQuote;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.payment.*;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.PaymentContractFilter;
import uz.uzkassa.cts.mappings.PaymentContractMapper;
import uz.uzkassa.cts.repository.BaseRepository;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.repository.PaymentContractRepository;
import uz.uzkassa.cts.repository.TradeInPriceHistoryRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.DateUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class PaymentContractServiceImpl extends BaseServiceImpl<PaymentContract, Long> implements PaymentContractService {

    private final PaymentContractRepository paymentContractRepository;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final PaymentQuoteService paymentQuoteService;
    private final TradeInPriceHistoryRepository tradeInPriceHistoryRepository;
    private final PaymentContractDetailService paymentContractDetailService;
    private final TradeInPriceHistoryService tradeInPriceHistoryService;
    private final PaymentContractMapper paymentContractMapper;
    private final CustomerEmployeeRepository customerEmployeeRepository;

    public PaymentContractServiceImpl(BaseRepository<PaymentContract, Long> repository,
                                      PaymentContractRepository paymentContractRepository,
                                      ThymeleafService thymeleafService, FileUploadService fileUploadService,
                                      PaymentQuoteService paymentQuoteService,
                                      TradeInPriceHistoryRepository tradeInPriceHistoryRepository,
                                      PaymentContractDetailService paymentContractDetailService,
                                      TradeInPriceHistoryService tradeInPriceHistoryService,
                                      PaymentContractMapper paymentContractMapper, CustomerEmployeeRepository customerEmployeeRepository) {
        super(repository);
        this.paymentContractRepository = paymentContractRepository;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.paymentQuoteService = paymentQuoteService;
        this.tradeInPriceHistoryRepository = tradeInPriceHistoryRepository;
        this.paymentContractDetailService = paymentContractDetailService;
        this.tradeInPriceHistoryService = tradeInPriceHistoryService;
        this.paymentContractMapper = paymentContractMapper;
        this.customerEmployeeRepository = customerEmployeeRepository;
    }

    @Override
    public void create(PaymentContractDTO paymentContractDTO) {
        PaymentQuote paymentQuote = paymentQuoteService.findById(paymentContractDTO.getPaymentQuoteId());
        if (paymentContractDTO.getDeviceIds().size() != paymentQuote.getQty()) {
            throw validationService.badRequestExceptionThrow(PAYMENT_CONTRACT).get();
        }
        if (paymentContractDTO.isIndividualContract() && StringUtils.isEmpty(paymentContractDTO.getIndividualContractNumber()) && paymentContractDTO.getIndividualDate() == null) {
            throw validationService.badRequestExceptionThrow(PAYMENT_INDIVIDUAL_CONTRACT).get();
        }
        TradeInPriceHistory tradeInPriceHistory = tradeInPriceHistoryService.findById(paymentContractDTO.getTradeInPriceHistoryId());
        if (tradeInPriceHistory == null) {
            throw validationService.badRequestExceptionThrow(PAYMENT_CONTRACT_DEVICE_MODEL).get();
        }
        if (paymentContractDTO.isIndividualContract() && StringUtils.isNotEmpty(paymentContractDTO.getIndividualContractNumber()) && paymentContractRepository.existsByIndividualContractNumber(paymentContractDTO.getIndividualContractNumber())) {
            throw validationService.badRequestExceptionThrow(PAYMENT_INDIVIDUAL_CONTRACT_NUMBER).get();
        }
        BigDecimal qty = new BigDecimal(paymentQuote.getQty());

        PaymentContract paymentContract = new PaymentContract();
        paymentContract.setCompanyId(getCompanyId());
        paymentContract.setBranchId(SecurityUtils.getBranchId());
        paymentContract.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        paymentContract.setPaymentQuoteId(paymentContractDTO.getPaymentQuoteId());
        paymentContract.setCustomerId(paymentContractDTO.getCustomerId());
        paymentContract.setVat1pc(tradeInPriceHistory.getVat1pc());
        paymentContract.setPriceFor1pc(tradeInPriceHistory.getPrice1pc());
        paymentContract.setTotalPriceWithVat(tradeInPriceHistory.getPriceWithVat().multiply(qty));
        paymentContract.setTotalProductCount(paymentQuote.getQty());
        paymentContract.setPriceFor1pcWithVat(tradeInPriceHistory.getPrice1pcWithVat());
        paymentContract.setDeviceType(tradeInPriceHistory.getDeviceType());
        paymentContract.setTradeInPriceHistoryId(tradeInPriceHistory.getId());
        paymentContract.setIndividualContract(paymentContractDTO.isIndividualContract());
        paymentContract.setIndividualContractNumber(paymentContractDTO.getIndividualContractNumber());
        paymentContract.setIndividualDate(paymentContractDTO.getIndividualDate());
        save(paymentContract);

        PaymentContractDetailDTO detailDTO = new PaymentContractDetailDTO();
        detailDTO.setPaymentContractId(paymentContract.getId());
        detailDTO.setDeviceIds(paymentContractDTO.getDeviceIds());
        detailDTO.setPrice1pc(tradeInPriceHistory.getPrice1pc());
        detailDTO.setPrice1pcWithVat(tradeInPriceHistory.getPrice1pcWithVat());
        detailDTO.setVat1pc(tradeInPriceHistory.getVat1pc());
        detailDTO.setPriceWithVat(tradeInPriceHistory.getPriceWithVat());
        paymentContractDetailService.create(detailDTO);

        paymentQuote.setStatus(PaymentQuoteStatus.ACCEPTED);
        paymentQuoteService.save(paymentQuote);
    }

    @Override
    public PaymentContractDetailResponseDTO getById(Long id) {
        return paymentContractMapper.toDto(findById(id));
    }

    @Override
    public List<PaymentContractDetailResponseDTO> getByCustomerId(Long id) {
        return paymentContractRepository
                .findByCustomerId(id)
                .stream()
                .map(paymentContractMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentContractDetailResponseDTO> getByContractsIsAccepted(Long id, String deviceType) {
        return paymentContractRepository
                .findByCustomerIdAndStatusAndDeviceType(id, ContractStatus.ACCEPTED, DeviceType.valueOf(deviceType))
                .stream()
                .map(paymentContractMapper::toDtoExecute)
                .collect(Collectors.toList());
    }

    @Override
    public Page<PaymentContractDetailResponseDTO> list(PaymentContractFilter filter) {
        Specification<PaymentContract> specification = (root, criteriaQuery, builder) -> {
            criteriaQuery.distinct(true);
            ArrayList<Predicate> list = new ArrayList<>();
            Path<Long> branchIdPath = root.get(PaymentContract_.BRANCH_ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(), Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null) {
                    list.add(builder.equal(branchIdPath, filter.getBranchId()));
                }
            } else {
                list.add(builder.equal(branchIdPath, SecurityUtils.getBranchId()));
            }
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Join<Customer, PaymentContract> customerJoin = root.join(PaymentContract_.CUSTOMER);
                Path<String> tinPath = customerJoin.get(Customer_.TIN);
                Path<String> customerNamePath = customerJoin.get(Customer_.NAME);

                Predicate customerNamePred = builder.like(builder.lower(customerNamePath), filter.getSearchForQuery());
                Predicate tinPred = builder.like(builder.lower(tinPath), filter.getSearchForQuery());
                list.add(builder.or(customerNamePred, tinPred));
            }
            if (filter.getBranchId() != null) {
                list.add(builder.equal(branchIdPath, filter.getBranchId()));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(PaymentContract_.STATUS);
                list.add(builder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(PaymentContract_.CREATED_DATE);
                list.add(builder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getCreatorId() != null) {
                Path<Long> longPath = root.get(PaymentContract_.CREATOR_ID);
                list.add(builder.equal(longPath, filter.getCreatorId()));
            }
            if (filter.getDeviceType() != null) {
                Path<DeviceType> deviceType = root.get(PaymentContract_.DEVICE_TYPE);
                list.add(builder.equal(deviceType, filter.getDeviceType()));
            }
            return builder.and(list.toArray(new Predicate[0]));
        };
        Specification<PaymentContract> spec = specification;
        if (ActExistEnum.WITH_ACT.equals(filter.getActExistEnum()))
            spec = Specification.where(specification).and(getExistAct());
        if (ActExistEnum.WITHOUT_ACT.equals(filter.getActExistEnum()))
            spec = Specification.where(specification).and(getNotExistAct());
        return paymentContractRepository.findAll(spec, filter.getOrderedPageable())
                .map(paymentContractMapper::toDto);
    }

    @Override
    public String generateContractHtml(Long id) {
        PaymentContract paymentContract = findById(id);
        CustomerBank bank = paymentContract.getCustomer().getCustomerBank();
        if (bank == null) {
            throw validationService.entityNotFoundExceptionThrow(BANK).get();
        }
        if (bank.getAccountNumber() == null) {
            throw validationService.entityNotFoundExceptionThrow(ACCOUNT_NUMBER).get();
        }
        if (CollectionUtils.isEmpty(paymentContract.getDetails())) {
            throw validationService.requiredExceptionThrow(DEVICE).get();
        }

        List<String> serialNumbers = new ArrayList<>();
        paymentContract.getDetails().forEach(paymentContractDetail -> serialNumbers.add(paymentContractDetail.getDevice().getSerialNumber()));
        PaymentContractTemplateDTO contractTemplateDTO = new PaymentContractTemplateDTO();
        contractTemplateDTO.setId(paymentContract.getId());
        contractTemplateDTO.setContractNumber(paymentContract.getNumber());
        contractTemplateDTO.setTin(paymentContract.getCustomer().getTin());
        contractTemplateDTO.setCreatedDate(DateUtil.getLocalDateTimeAsStringWithoutSecond(paymentContract.getCreatedDate()));
        contractTemplateDTO.setCustomerName(paymentContract.getCustomer().getCompanyName());
        contractTemplateDTO.setBusinessType(paymentContract.getCustomer().getBusinessType().getName());
        contractTemplateDTO.setPhone(paymentContract.getCustomer().getPhone());
        contractTemplateDTO.setOked(paymentContract.getCustomer().getOked());
        contractTemplateDTO.setAccountNumber(bank.getAccountNumber());
        contractTemplateDTO.setBankName(bank.getBank().getNameNotNullable());
        contractTemplateDTO.setModelName(paymentContract.getPaymentQuote().getModel().getName());
        contractTemplateDTO.setSerialNumbers(serialNumbers);
        contractTemplateDTO.setRegion(paymentContract.getCustomer().getAddress().getRegion().getNameRu());
        contractTemplateDTO.setDistrict(paymentContract.getCustomer().getAddress().getDistrict().getNameRu());
        contractTemplateDTO.setStreet(paymentContract.getCustomer().getAddress().getStreet());
        contractTemplateDTO.setHouse(paymentContract.getCustomer().getAddress().getHouse());
        contractTemplateDTO.setApartment(paymentContract.getCustomer().getAddress().getApartment());
        contractTemplateDTO.setQty(paymentContract.getPaymentQuote().getQty());
        customerEmployeeRepository.findFirstByCustomerIdAndRole(paymentContract.getCustomerId(), Role.DIRECTOR).ifPresent(customerEmployee -> contractTemplateDTO.setOwnerName(customerEmployee.getName()));

        contractTemplateDTO.setVatRate(paymentContract.getVat1pc());
        contractTemplateDTO.setVat(paymentContract.getVat1pc());
        contractTemplateDTO.setPrice(paymentContract.getPriceFor1pc());
        contractTemplateDTO.setPriceWithVat(paymentContract.getTotalPriceWithVat());
        contractTemplateDTO.setTotalVat(paymentContract.getVat1pc());
        contractTemplateDTO.setTotalPrice(paymentContract.getTotalPriceWithVat());
        contractTemplateDTO.setTotalPriceWithVat(paymentContract.getPriceFor1pcWithVat());

        return thymeleafService.generatePaymentContractHTML(contractTemplateDTO);
    }

    @Override
    public PaymentContractDetailResponseDTO accept(Long id) {
        PaymentContract contract = findById(id);
        if (!ContractStatus.DOCUMENT_ATTACHED.equals(contract.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.DOCUMENT_ATTACHED.getName()).get();
        }
        contract.setStatus(ContractStatus.ACCEPTED);
        save(contract);
        return paymentContractMapper.toDto(contract);
    }

    @Override
    public PaymentContractDetailResponseDTO reject(Long id) {
        PaymentContract contract = changeStatus(id);
        return paymentContractMapper.toDto(contract);
    }

    @Override
    public PaymentContractDetailResponseDTO waitingForAttach(Long id) {
        PaymentContract contract = findById(id);

        if (ContractStatus.REJECTED.equals(contract.getStatus()))
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();

//        PaymentAct act = contract.getPaymentAct();
//        if (act != null &&
//            (ContractStatus.ACCEPTED.equals(act.getStatus()) ||
//                ContractStatus.REJECTED.equals(act.getStatus())))
//            throw validationService.badRequestExceptionThrow("contract.not.changed.act.status", act.getStatus().getNameUzCyrillic()).get();

        contract.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        contract = save(contract);

        return paymentContractMapper.toDto(contract);
    }

    @Override
    public PaymentContractDetailResponseDTO uploadContractFile(Long id, MultipartFile file) {
        PaymentContract contract = findById(id);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.PAYMENT_CONTRACT.name(), DocumentType.PAYMENT_CONTRACT.name());
        contract.setFileName(response.getFileName());
        contract.setFileUri(response.getFileDownloadUri());
        contract.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        save(contract);
        return paymentContractMapper.toDto(contract);
    }

    @Override
    public void generateAct(PaymentContractActDTO dto) {
        PaymentContract contract = findById(dto.getPaymentContractId());
//        if (contract.getPaymentActs() != null) {
//            throw validationService.badRequestExceptionThrow("you.cannot.add.more.then.device", contract.getPaymentQuote().getQty().toString()).get();
//        }
        if (!ContractStatus.ACCEPTED.equals(contract.getStatus())) {
            throw validationService.badRequestExceptionThrow(ACCEPT_CONTRACT, contract.getNumber()).get();
        }

        TradeInPriceHistory tradeInPriceHistory = tradeInPriceHistoryRepository.findFirstByActiveTrueAndContractTypeAndDeviceModelId(ContractType.PAYMENT_CONTRACT, contract.getPaymentQuote().getModelId());
        if (tradeInPriceHistory == null) {
            throw validationService.badRequestExceptionThrow(PAYMENT_CONTRACT_DEVICE_MODEL).get();
        }

        Set<PaymentContractDetail> details = contract.getDetails();
        for (PaymentContractDetail paymentContractDetail : details) {
            if (Objects.equals(dto.getDeviceId(), paymentContractDetail.getDeviceId())) {
                PaymentAct paymentAct = new PaymentAct();
                paymentAct.setContractDetail(paymentContractDetail);
                paymentAct.setDeviceId(paymentContractDetail.getDevice().getId());
                paymentAct.setTotalProductCount(contract.getPaymentQuote().getQty());
                paymentAct.setTotalPriceWithVat(tradeInPriceHistory.getPrice1pcWithVat());
                paymentAct.setVat1pc(tradeInPriceHistory.getVat1pc());
                paymentAct.setPriceFor1pc(tradeInPriceHistory.getPriceWithVat());
                paymentAct.setPriceFor1pcWithVat(tradeInPriceHistory.getPrice1pcWithVat());
                paymentAct.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
                contract.addPaymentAct(paymentAct);
            }
        }
        save(contract);
    }

    public static Specification<PaymentContract> getExistAct() {
        return (root, query, cb) -> cb.isNotNull(root.join(PaymentContract_.PAYMENT_ACTS, JoinType.LEFT));
    }

    public static Specification<PaymentContract> getNotExistAct() {
        return (root, query, cb) -> cb.isNull(root.join(PaymentContract_.PAYMENT_ACTS, JoinType.LEFT));
    }

    private PaymentContract changeStatus(Long id) {
        PaymentContract contract = findById(id);
        checkRejectedStatus(contract);
//        if (contract.getPaymentAct() != null && !ContractStatus.REJECTED.equals(contract.getPaymentAct().getStatus()))
//            throw validationService.badRequestExceptionThrow("contract.not.reject.if.act.status").get();

        contract.setStatus(ContractStatus.REJECTED);
        save(contract);
        return contract;
    }

    private void checkRejectedStatus(PaymentContract contract) {
        if (ContractStatus.REJECTED.equals(contract.getStatus()) ||
                ContractStatus.ACCEPTED.equals(contract.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, contract.getStatus().getName()).get();
    }
}
