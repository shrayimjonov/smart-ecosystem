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
import uz.uzkassa.cts.dto.tradeincountercontract.request.TradeInCounterContractEditRequestDto;
import uz.uzkassa.cts.dto.tradeincountercontract.request.TradeInCounterContractRequestDto;
import uz.uzkassa.cts.dto.tradeincountercontract.response.TradeInCounterContractResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.CustomerVatTypeEnum;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.TradeInCounterContractFilter;
import uz.uzkassa.cts.mappings.TradeInCounterContractMapper;
import uz.uzkassa.cts.repository.TradeInCounterContractRepository;
import uz.uzkassa.cts.repository.TradeInLetterOfGuaranteeRepository;
import uz.uzkassa.cts.service.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Transactional
@Service
@Slf4j
public class TradeInCounterContractServiceImpl extends BaseServiceImpl<TradeInCounterContract, Long> implements TradeInCounterContractService {

    private final TradeInCounterContractRepository tradeInCounterContractRepository;
    private final TradeInCounterDevicePriceService tradeInCounterDevicePriceService;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final TradeInContractService tradeInContractService;
    private final TradeInDeviceService tradeInDeviceService;
    private final TradeInLetterOfGuaranteeRepository tradeInLetterOfGuaranteeRepository;
    private final TradeInCounterContractMapper tradeInCounterContractMapper;

    public TradeInCounterContractServiceImpl(TradeInCounterContractRepository tradeInCounterContractRepository,
                                             TradeInCounterDevicePriceService tradeInCounterDevicePriceService,
                                             ThymeleafService thymeleafService,
                                             FileUploadService fileUploadService,
                                             TradeInContractService tradeInContractService,
                                             TradeInDeviceService tradeInDeviceService,
                                             TradeInLetterOfGuaranteeRepository tradeInLetterOfGuaranteeRepository,
                                             TradeInCounterContractMapper tradeInCounterContractMapper) {
        super(tradeInCounterContractRepository);
        this.tradeInCounterContractRepository = tradeInCounterContractRepository;
        this.tradeInCounterDevicePriceService = tradeInCounterDevicePriceService;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.tradeInContractService = tradeInContractService;
        this.tradeInDeviceService = tradeInDeviceService;
        this.tradeInLetterOfGuaranteeRepository = tradeInLetterOfGuaranteeRepository;
        this.tradeInCounterContractMapper = tradeInCounterContractMapper;
    }

    @Override
    @Transactional
    public TradeInCounterContractResponseDto create(TradeInCounterContractRequestDto dto) {
        tradeInLetterOfGuaranteeRepository.findByTradeInContractId(dto.getContractId())
            .ifPresent(s -> {
                throw validationService.badRequestExceptionThrow(IMPOSSIBLE_CREATE_COUNTER_AGREEMENT).get();
            });

        TradeInCounterDevicePrice devicePrice = tradeInCounterDevicePriceService.findById(dto.getTradeInCounterDevicePriceId());
        if (!devicePrice.isActive()) {
            throw validationService.badRequestExceptionThrow(TRADE_IN_DEVICE_NOT_ACTIVE).get();
        }

        TradeInContract contract = tradeInContractService.findById(dto.getContractId());
        TradeInAct act = contract.getTradeInAct();
        if (act == null)
            throw validationService.badRequestExceptionThrow(TRADE_IN_NOT_CREATED).get();
        if (!ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.badRequestExceptionThrow(TRADE_IN_NOT_ACCEPTED).get();
        if (contract.getTradeInCounterContract() != null)
            throw validationService.badRequestExceptionThrow(TRADE_IN_CONTRACT_ALREADY_CREATED).get();

        TradeInDevice device = tradeInDeviceService.findById(dto.getTradeInDeviceId());

        TradeInCounterContract counterContract = new TradeInCounterContract();
        counterContract.setTradeInDeviceId(dto.getTradeInDeviceId());
        counterContract.setTradeInDevice(device);

        counterContract.setCompanyId(getCompanyId());
        counterContract.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        counterContract.setEndDate(LocalDateTime.now().plusMonths(1));
        counterContract.setCustomerId(contract.getCustomerId());
        counterContract.setDeviceSerialNumber(dto.getDeviceSerialNumber());
        counterContract.setTotalPriceWithVat(devicePrice.getPriceWithVat());
        if (CustomerVatTypeEnum.WITH_VAT == devicePrice.getCustomerVatTypeEnum()) {
            counterContract.setVat1pc(devicePrice.getVat1pc());
        }
        counterContract.setPriceFor1pc(devicePrice.getPrice1pc());
        counterContract.setPriceFor1pcWithVat(devicePrice.getPrice1pcWithVat());
        counterContract.setTotalProductCount(1);
        counterContract.setTradeInCounterDevicePriceId(dto.getTradeInCounterDevicePriceId());
        contract.setTradeInCounterContract(counterContract);
        counterContract.setContract(contract);
        save(counterContract);

        return tradeInCounterContractMapper.toDto(counterContract);
    }

    @Override
    @Transactional
    public TradeInCounterContractResponseDto update(TradeInCounterContractEditRequestDto dto) {
        TradeInCounterContract counterContract = findById(dto.getId());
        if (ContractStatus.ACCEPTED.equals(counterContract.getStatus())) {
            throw validationService.badRequestExceptionThrow(TRADE_IN_CONTRACT_ACCEPTED).get();
        }

        TradeInCounterDevicePrice devicePrice = tradeInCounterDevicePriceService.findById(dto.getTradeInCounterDevicePriceId());
        if (!devicePrice.isActive()) {
            throw validationService.badRequestExceptionThrow(TRADE_IN_DEVICE_NOT_ACTIVE).get();
        }
//        TradeInContract contract = counterContract.getContract();

        TradeInDevice device = tradeInDeviceService.findById(dto.getTradeInDeviceId());
        counterContract.setTradeInDeviceId(dto.getTradeInDeviceId());
        counterContract.setTradeInDevice(device);

        counterContract.setCompanyId(getCompanyId());
        counterContract.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        counterContract.setEndDate(LocalDateTime.now().plusMonths(1));
//        counterContract.setCustomerId(contract.getCustomerId());
        counterContract.setDeviceSerialNumber(dto.getDeviceSerialNumber());
        counterContract.setTotalPriceWithVat(devicePrice.getPriceWithVat());
        if (CustomerVatTypeEnum.WITH_VAT == devicePrice.getCustomerVatTypeEnum())
            counterContract.setVat1pc(devicePrice.getVat1pc());
        else
            counterContract.setVat1pc(null);
        counterContract.setPriceFor1pc(devicePrice.getPrice1pc());
        counterContract.setPriceFor1pcWithVat(devicePrice.getPrice1pcWithVat());
        counterContract.setTotalProductCount(1);
        counterContract.setTradeInCounterDevicePriceId(dto.getTradeInCounterDevicePriceId());
//        contract.setTradeInCounterContract(counterContract);
//        counterContract.setContract(contract);
        save(counterContract);

        return tradeInCounterContractMapper.toDto(counterContract);
    }

    @Override
    public TradeInCounterContractResponseDto getById(Long id) {
        return tradeInCounterContractMapper.toDto(findById(id));
    }


    @Override
    public Page<TradeInCounterContractResponseDto> list(TradeInCounterContractFilter filter) {
        Specification<TradeInCounterContract> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            Path<Customer> customer = root.join(TradeInCounterContract_.CUSTOMER, JoinType.INNER);
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);
                Path<String> tin = customer.get(Customer_.TIN);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(TradeInCounterContract_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(TradeInCounterContract_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getCreatorId() != null) {
                Path<User> user = root.join(TradeInCounterContract_.CREATOR, JoinType.INNER);
                Path<Long> userId = user.get(User_.ID);
                list.add(criteriaBuilder.equal(userId, filter.getCreatorId()));
            }
            if (filter.getBranchId() != null) {
                Path<Long> branchId = customer.get(Customer_.BRANCH_ID);
                list.add(criteriaBuilder.equal(branchId, filter.getBranchId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };

        return tradeInCounterContractRepository.findAll(specification, filter.getOrderedPageable()).map(tradeInCounterContractMapper::toDto);
    }

    public String getContractHtmlTemplate(Long id) {
        TradeInCounterContract contract = findById(id);
        CustomerBank bank = contract.getCustomer().getCustomerBank();
        if (bank == null) {
            throw validationService.entityNotFoundExceptionThrow(BANK).get();
        }
        if (bank.getAccountNumber() == null) {
            throw validationService.entityNotFoundExceptionThrow(ACCOUNT_NUMBER).get();
        }
        return thymeleafService.generateTradeInCounterContractHTML(contract);
    }

    @Override
    public TradeInCounterContractResponseDto uploadContractFile(Long id, MultipartFile file) {
        TradeInCounterContract contract = findById(id);
        CheckRejectedStatus(contract);

        UploadFileResponse response = fileUploadService.upload(file, id,
            EntityTypeEnum.TRADE_IN_COUNTER_CONTRACT.name(),
            DocumentType.TRADE_IN_COUNTER_CONTRACT.name()
        );
        contract.setFileName(response.getFileName());
        contract.setContentType(file.getContentType());
        contract.setFileUri(response.getFileDownloadUri());
        contract.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        save(contract);
        return tradeInCounterContractMapper.toDto(contract);
    }

    @Override
    public Page<TradeInCounterContractResponseDto> listByTin(TradeInCounterContractFilter filter, String tin) {
        Specification<TradeInCounterContract> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            Path<Customer> customer = root.join(TradeInCounterContract_.CUSTOMER, JoinType.INNER);
            Path<String> tinPath = customer.get(Customer_.TIN);
            list.add(criteriaBuilder.equal(tinPath, tin));

            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(TradeInCounterContract_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(TradeInCounterContract_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getCreatorId() != null) {
                Path<User> user = root.join(TradeInCounterContract_.CREATOR, JoinType.INNER);
                Path<Long> userId = user.get(User_.ID);
                list.add(criteriaBuilder.equal(userId, filter.getCreatorId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return tradeInCounterContractRepository.findAll(specification, filter.getOrderedPageable()).map(tradeInCounterContractMapper::toDto);
    }

    @Override
    public TradeInCounterContractResponseDto uploadNote(Long id, MultipartFile file) {
        TradeInCounterContract contract = findById(id);
        CheckRejectedStatus(contract);

        UploadFileResponse response = fileUploadService.upload(file, id,
            EntityTypeEnum.TRADE_IN_COUNTER_CONTRACT_NOTE.name(),
            DocumentType.TRADE_IN_COUNTER_CONTRACT_NOTE.name()
        );
        contract.setNoteFileName(response.getFileName());
        contract.setNoteContentType(file.getContentType());
        contract.setNoteFileUri(response.getFileDownloadUri());
        contract.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        save(contract);
        return tradeInCounterContractMapper.toDto(contract);
    }

    @Override
    public TradeInCounterContractResponseDto uploadDeviceImage(Long id, MultipartFile file) {
        TradeInCounterContract contract = findById(id);
        CheckRejectedStatus(contract);

        UploadFileResponse response = fileUploadService.upload(file, id,
            EntityTypeEnum.TRADE_IN_COUNTER_CONTRACT_FILE.name(),
            DocumentType.TRADE_IN_COUNTER_CONTRACT_FILE.name()
        );
        TradeInCounterContractFile contractFile = new TradeInCounterContractFile();
        contractFile.setFileName(response.getFileName());
        contractFile.setContentType(file.getContentType());
        contractFile.setFileUri(response.getFileDownloadUri());
        contract.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        contract.addFile(contractFile);
        save(contract);
        return tradeInCounterContractMapper.toDto(contract);
    }


    private TradeInCounterAct generateAct(TradeInCounterContract contract) {
        TradeInCounterAct act = new TradeInCounterAct();
        act.setTotalProductCount(contract.getTotalProductCount());
        act.setTotalPriceWithVat(contract.getTotalPriceWithVat());
        act.setVat1pc(contract.getVat1pc());
        act.setPriceFor1pc(contract.getPriceFor1pc());
        act.setPriceFor1pcWithVat(contract.getPriceFor1pcWithVat());
        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        act.setContract(contract);
        act.setDeviceSerialNumber(contract.getDeviceSerialNumber());
        act.setContract(contract);
        return act;
    }

    @Override
    @Transactional
    public TradeInCounterContractResponseDto accept(Long id) {
        TradeInCounterContract contract = findById(id);
        if (contract.getFiles().isEmpty()) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (StringUtils.isEmpty(contract.getFileUri())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (StringUtils.isEmpty(contract.getNoteFileUri())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        contract.setStatus(ContractStatus.ACCEPTED);
        TradeInCounterAct act = generateAct(contract);
        contract.setTradeInCounterAct(act);
        save(contract);
        return tradeInCounterContractMapper.toDto(contract);
    }

    @Override
    public TradeInCounterContractResponseDto reject(Long id) {
        TradeInCounterContract tradeInCounterContract = changeStatus(id, ContractStatus.REJECTED);
        return tradeInCounterContractMapper.toDto(tradeInCounterContract);
    }

    private TradeInCounterContract changeStatus(Long id, ContractStatus contractStatus) {
        TradeInCounterContract tradeInCounterContract = findById(id);
        CheckRejectedStatus(tradeInCounterContract);
        if (tradeInCounterContract.getTradeInCounterAct() != null &&
            !ContractStatus.REJECTED.equals(tradeInCounterContract.getTradeInCounterAct().getStatus()) &&
            ContractStatus.REJECTED.equals(contractStatus))
            throw validationService.badRequestExceptionThrow(CONTRACT_CANNOT_BE_REJECTED).get();

        tradeInCounterContract.setStatus(contractStatus);
        save(tradeInCounterContract);
        return tradeInCounterContract;
    }

    private void CheckRejectedStatus(TradeInCounterContract contract) {
        if (ContractStatus.REJECTED.equals(contract.getStatus()) ||
            ContractStatus.ACCEPTED.equals(contract.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, contract.getStatus().getName()).get();
    }
}
