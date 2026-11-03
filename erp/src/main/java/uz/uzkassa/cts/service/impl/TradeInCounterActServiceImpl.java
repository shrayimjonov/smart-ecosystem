package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.tradeincounteract.response.TradeInCounterActResponseDto;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.filter.ActOfTransmissionFilter;
import uz.uzkassa.cts.mappings.TradeInCounterActMapper;
import uz.uzkassa.cts.repository.TradeInCounterActRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.ThymeleafService;
import uz.uzkassa.cts.service.TradeInCounterActService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
public class TradeInCounterActServiceImpl extends BaseServiceImpl<TradeInCounterAct, Long> implements TradeInCounterActService {

    private final TradeInCounterActRepository repository;
    private final ThymeleafService thymeleafService;
    private final FileUploadService fileUploadService;
    private final TradeInCounterActMapper tradeInCounterActMapper;

    public TradeInCounterActServiceImpl(TradeInCounterActRepository repository,
                                        ThymeleafService thymeleafService,
                                        FileUploadService fileUploadService,
                                        TradeInCounterActMapper tradeInCounterActMapper) {
        super(repository);
        this.repository = repository;
        this.thymeleafService = thymeleafService;
        this.fileUploadService = fileUploadService;
        this.tradeInCounterActMapper = tradeInCounterActMapper;
    }

    @Override
    public TradeInCounterActResponseDto getByContractId(Long id) {
        TradeInCounterAct act = repository.findByContract_Id(id);
        if (act == null) {
            throw validationService.entityNotFoundExceptionThrow(CONTRACT, ID, id.toString()).get();
        }
        return tradeInCounterActMapper.toDto(act);
    }

    @Override
    public Page<TradeInCounterActResponseDto> getList(ActOfTransmissionFilter filter) {
        Specification<TradeInCounterAct> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<TradeInCounterAct, TradeInCounterContract> contractJoin = root.join(TradeInCounterAct_.CONTRACT, JoinType.INNER);
            Join<TradeInCounterContract, TradeInContract> tradeInContractJoin = contractJoin.join(TradeInCounterContract_.CONTRACT, JoinType.INNER);
            Join<TradeInCounterAct, User> creatorJoin = root.join(TradeInCounterAct_.CREATOR, JoinType.INNER);

            ArrayList<Predicate> list = new ArrayList<>();

            Path<Long> branchIdPath = tradeInContractJoin.get(TradeInContract_.BRANCH_ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(),
                    Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null)
                    list.add(criteriaBuilder.equal(branchIdPath, filter.getBranchId()));
            } else
                list.add(criteriaBuilder.equal(branchIdPath, SecurityUtils.getBranchId()));

            if (StringUtils.isNotEmpty(filter.getSearch())) {

                Path<Customer> customer = contractJoin.join(TradeInCounterContract_.CUSTOMER, JoinType.INNER);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);
                Path<String> tin = customer.get(Customer_.TIN);
                Path<String> deviceSerialNumber = root.get(TradeInCounterAct_.DEVICE_SERIAL_NUMBER);
                Path<String> creatorLogin = creatorJoin.get(User_.LOGIN);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());
                Predicate deviceSerialNumberPred = criteriaBuilder.like(criteriaBuilder.lower(deviceSerialNumber), filter.getSearchForQuery());
                Predicate creatorLoginPred = criteriaBuilder.like(criteriaBuilder.lower(creatorLogin), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred, deviceSerialNumberPred, creatorLoginPred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(TradeInCounterAct_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }

            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(TradeInCounterAct_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getActDocFlowStatus() != null) {
                Path<ActDocFlowStatus> docFlowStatusPath = root.get(TradeInAct_.DOC_FLOW_STATUS);
                list.add(criteriaBuilder.equal(docFlowStatusPath, filter.getActDocFlowStatus()));
            }
            if (filter.getCreatorId() != null) {
                Path<User> user = root.join(TradeInCounterAct_.CREATOR, JoinType.INNER);
                Path<Long> userId = user.get(User_.ID);
                list.add(criteriaBuilder.equal(userId, filter.getCreatorId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return repository
                .findAll(specification, filter.getOrderedPageable())
                .map(tradeInCounterActMapper::toDto);
    }

    @Override
    public TradeInCounterActResponseDto getById(Long id) {
        return tradeInCounterActMapper.toDto(findById(id));
    }

    @Override
    public String getHtmlTemplate(Long id) {
        return thymeleafService.generateTradeInCounterActHTML(findById(id));
    }

    @Override
    public TradeInCounterActResponseDto uploadFile(Long id, MultipartFile file) {
        TradeInCounterAct act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(
                file,
                id,
                EntityTypeEnum.TRADE_IN_COUNTER_ACT.name(),
                DocumentType.TRADE_IN_COUNTER_ACT.name()
        );
        act.setFileName(response.getFileName());
        act.setContentType(file.getContentType());
        act.setFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return tradeInCounterActMapper.toDto(act);
    }

    @Override
    public TradeInCounterActResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file) {
        TradeInCounterAct act = findById(id);
        checkStatusForUpload(act);

        UploadFileResponse response = fileUploadService.upload(
                file,
                id,
                EntityTypeEnum.TRADE_IN_COUNTER_POWER_ATTORNEY.name(),
                DocumentType.TRADE_IN_COUNTER_POWER_ATTORNEY.name()
        );
        act.setPowerAttorneyFileName(response.getFileName());
        act.setPowerAttorneyContentType(file.getContentType());
        act.setPowerAttorneyFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return tradeInCounterActMapper.toDto(act);
    }

    @Override
    public TradeInCounterActResponseDto accept(Long id) {
        TradeInCounterAct act = findById(id);
        if (ContractStatus.REJECTED.equals(act.getStatus())) {
            throw validationService.badRequestExceptionThrow(ACT_NOT_ACCEPTED).get();
        }
        if (StringUtils.isEmpty(act.getFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
//        if (StringUtils.isEmpty(act.getPowerAttorneyFileName())) {
//            throw new BadRequestExp("You must upload Power attorney file");
//        }

        act.setStatus(ContractStatus.ACCEPTED);
        act.setAcceptDate(LocalDateTime.now());
        act = save(act);
        return tradeInCounterActMapper.toDto(act);
    }

    @Override
    public TradeInCounterActResponseDto changeToAttach(Long id) {
        TradeInCounterAct act = findById(id);
        if (!ContractStatus.ACCEPTED.equals(act.getStatus())) {
            throw validationService.statusChangeExceptionThrow(ContractStatus.ACCEPTED.getName()).get();
        }
        act.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(act);
        return tradeInCounterActMapper.toDto(act);
    }

    @Override
    public TradeInCounterActResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto) {
        TradeInCounterAct act = findById(dto.getId());
        act.setDocFlowStatus(dto.getDocFlowStatus());
        act.setDocFlowReason(dto.getDocFlowReason());
        save(act);
        return tradeInCounterActMapper.toDto(act);
    }

    private void checkStatusForUpload(TradeInCounterAct act) {
        if (ContractStatus.REJECTED.equals(act.getStatus()) ||
                ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, act.getStatus().getName()).get();
    }
}
