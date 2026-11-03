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
import uz.uzkassa.cts.dto.tradeinact.response.TradeInActResponseDto;
import uz.uzkassa.cts.dto.tradeincontract.request.TradeInActPoaRequestDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.ActOfTransmissionFilter;
import uz.uzkassa.cts.mappings.TradeInActMapper;
import uz.uzkassa.cts.repository.TradeInActRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.service.TradeInActService;
import uz.uzkassa.cts.service.TradeInInvoiceOldService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
public class TradeInActServiceImpl extends BaseServiceImpl<TradeInAct, Long> implements TradeInActService {

    private final TradeInActRepository repository;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final TradeInInvoiceOldService tradeInInvoiceOldService;
    private final TradeInActMapper tradeInActMapper;

    public TradeInActServiceImpl(TradeInActRepository repository,
                                 ThymeleafService thymeleafService,
                                 FileUploadService fileUploadService,
                                 TradeInInvoiceOldService tradeInInvoiceOldService,
                                 TradeInActMapper tradeInActMapper) {
        super(repository);
        this.repository = repository;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.tradeInInvoiceOldService = tradeInInvoiceOldService;
        this.tradeInActMapper = tradeInActMapper;
    }

    @Override
    public TradeInActResponseDto getByContractId(Long id) {
        TradeInAct act = repository.findByContract_Id(id);
        return tradeInActMapper.toDto(act);
    }

    @Override
    public Page<TradeInActResponseDto> getList(ActOfTransmissionFilter filter) {
        Specification<TradeInAct> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<TradeInAct, TradeInContract> contractJoin = root.join(TradeInAct_.CONTRACT, JoinType.INNER);
            Join<TradeInAct, Device> deviceJoin = root.join(TradeInAct_.DEVICE, JoinType.INNER);
            Join<TradeInAct, User> creatorJoin = root.join(TradeInAct_.CREATOR, JoinType.INNER);

            ArrayList<Predicate> list = new ArrayList<>();

            Path<Long> branchIdPath = contractJoin.get(TradeInContract_.BRANCH_ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(),
                Role.ADMIN.getRoleCode(),
                Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null)
                    list.add(criteriaBuilder.equal(branchIdPath, filter.getBranchId()));
            } else
                list.add(criteriaBuilder.equal(branchIdPath, SecurityUtils.getBranchId()));

            if (StringUtils.isNotEmpty(filter.getSearch())) {

                Path<Customer> customer = contractJoin.join(TradeInContract_.CUSTOMER, JoinType.INNER);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);
                Path<String> tin = customer.get(Customer_.TIN);
                Path<String> serialNumber = deviceJoin.get(Device_.SERIAL_NUMBER);
                Path<String> creatorLogin = creatorJoin.get(User_.LOGIN);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                Predicate serialNumberPred = criteriaBuilder.like(criteriaBuilder.lower(serialNumber), filter.getSearchForQuery());
                Predicate creatorLoginPred = criteriaBuilder.like(criteriaBuilder.lower(creatorLogin), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred, serialNumberPred, creatorLoginPred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(TradeInAct_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }

            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(TradeInAct_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }

            if (filter.getActDocFlowStatus() != null) {
                Path<ActDocFlowStatus> docFlowStatusPath = root.get(TradeInAct_.DOC_FLOW_STATUS);
                list.add(criteriaBuilder.equal(docFlowStatusPath, filter.getActDocFlowStatus()));
            }
            if (filter.getCreatorId() != null) {
                Path<User> user = root.join(TradeInAct_.CREATOR, JoinType.INNER);
                Path<Long> userId = user.get(User_.ID);
                list.add(criteriaBuilder.equal(userId, filter.getCreatorId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return repository
            .findAll(specification, filter.getOrderedPageable())
            .map(tradeInActMapper::toDto);
    }

    @Override
    public TradeInActResponseDto getById(Long id) {
        return tradeInActMapper.toDto(findById(id));
    }

    @Override
    public String getHtmlTemplate(Long id) {
        return thymeleafService.generateTradeInActHTML(findById(id));
    }

    @Override
    public TradeInActResponseDto uploadFile(Long id, MultipartFile file) {
        TradeInAct act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.TRADE_IN_ACT.name(), DocumentType.TRADE_IN_ACT.name());
        act.setFileName(response.getFileName());
        act.setContentType(file.getContentType());
        act.setFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return tradeInActMapper.toDto(act);
    }

    @Override
    public TradeInActResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file) {
        TradeInAct act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.TRADE_IN_POWER_ATTORNEY.name(), DocumentType.TRADE_IN_POWER_ATTORNEY.name());
        act.setPowerAttorneyFileName(response.getFileName());
        act.setPowerAttorneyContentType(file.getContentType());
        act.setPowerAttorneyFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return tradeInActMapper.toDto(act);
    }

    @Override
    @Transactional
    public TradeInActResponseDto accept(Long id) {
        TradeInAct act = findById(id);
        if (ContractStatus.REJECTED.equals(act.getStatus())) {
            throw validationService.badRequestExceptionThrow(ACT_NOT_ACCEPTED).get();
        }
        if (StringUtils.isEmpty(act.getFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (StringUtils.isEmpty(act.getPowerAttorneyFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }

        act.setStatus(ContractStatus.ACCEPTED);
        act.setAcceptDate(LocalDateTime.now());
        if (act.getTradeInInvoice() == null)
            act.setTradeInInvoice(tradeInInvoiceOldService.createInvoice(act));
        act = save(act);

        return tradeInActMapper.toDto(act);
    }

    @Override
    public TradeInActResponseDto changeToAttach(Long id) {
        TradeInAct act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(act);
        return tradeInActMapper.toDto(act);
    }

    @Override
    public TradeInActResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto) {
        TradeInAct act = findById(dto.getId());
        act.setDocFlowStatus(dto.getDocFlowStatus());
        act.setDocFlowReason(dto.getDocFlowReason());
        save(act);
        return tradeInActMapper.toDto(act);
    }

    @Override
    public String generatePoaTemplate(TradeInActPoaRequestDto dto) {
        TradeInAct tradeInAct = findById(dto.getActId());
        return thymeleafService.generateTradeInActPoaHTML(tradeInAct, dto);
    }

    private void checkStatusForUpload(TradeInAct act) {
        if (ContractStatus.REJECTED.equals(act.getStatus()) ||
            ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, act.getStatus().getName()).get();
    }
}
