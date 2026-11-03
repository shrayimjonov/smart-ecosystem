package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.rentoffer.request.RentOfferRequestDto;
import uz.uzkassa.cts.dto.rentoffer.response.RentOfferResponseDto;
import uz.uzkassa.cts.enums.ActExistEnum;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.TradeInContractFilter;
import uz.uzkassa.cts.mappings.RentOfferMapper;
import uz.uzkassa.cts.repository.RentOfferRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.CustomerOldService;
import uz.uzkassa.cts.service.RentOfferService;
import uz.uzkassa.cts.service.ThymeleafService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

@Service
public class RentOfferServiceImpl extends BaseServiceImpl<RentOffer, Long> implements RentOfferService {

    private final CustomerOldService customerOldService;
    private final RentOfferRepository repository;
    private final ThymeleafService thymeleafService;
    private final RentOfferMapper rentOfferMapper;

    public RentOfferServiceImpl(RentOfferRepository repository,
                                CustomerOldService customerOldService,
                                ThymeleafService thymeleafService,
                                RentOfferMapper rentOfferMapper) {
        super(repository);
        this.customerOldService = customerOldService;
        this.repository = repository;
        this.thymeleafService = thymeleafService;
        this.rentOfferMapper = rentOfferMapper;
    }

    @Override
    public Page<RentOfferResponseDto> list(TradeInContractFilter filter) {
        Specification<RentOffer> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            Path<Long> branchIdPath = root.get(RentOffer_.BRANCH_ID);
            if (SecurityUtils.isCurrentUserInRoles(Role.ADMIN.getRoleCode(),
                Role.ACCOUNTANT.getRoleCode())) {
                if (filter.getBranchId() != null)
                    list.add(criteriaBuilder.equal(branchIdPath, filter.getBranchId()));
            } else
                list.add(criteriaBuilder.equal(branchIdPath, SecurityUtils.getBranchId()));

            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<Customer> customer = root.join(RentOffer_.CUSTOMER, JoinType.INNER);
                Path<String> customerName = customer.get(Customer_.NAME);
                Path<String> customerCompanyName = customer.get(Customer_.COMPANY_NAME);
                Path<String> tin = customer.get(Customer_.TIN);

                Predicate customerNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerName), filter.getSearchForQuery());
                Predicate customerCompanyNamePred = criteriaBuilder.like(criteriaBuilder.lower(customerCompanyName), filter.getSearchForQuery());
                Predicate tinPred = criteriaBuilder.like(criteriaBuilder.lower(tin), filter.getSearchForQuery());

                list.add(criteriaBuilder.or(customerNamePred, tinPred, customerCompanyNamePred));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(RentOffer_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }

            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get(RentOffer_.STATUS);
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getCreatorId() != null) {
                Path<User> user = root.join(RentOffer_.CREATOR, JoinType.INNER);
                Path<Long> userId = user.get(User_.ID);
                list.add(criteriaBuilder.equal(userId, filter.getCreatorId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Specification<RentOffer> spec = specification;
        if (ActExistEnum.WITH_ACT.equals(filter.getActExistEnum()))
            spec = Specification.where(specification).and(getExistAct());
        if (ActExistEnum.WITHOUT_ACT.equals(filter.getActExistEnum()))
            spec = Specification.where(specification).and(getNotExistAct());

        return findAll(spec, filter.getOrderedPageable())
            .map(rentOfferMapper::toDto);
    }

    public static Specification<RentOffer> getExistAct() {
        return (root, query, cb) -> cb.isNotNull(root.join(RentOffer_.RENT_ACTS, JoinType.LEFT));
    }

    public static Specification<RentOffer> getNotExistAct() {
        return (root, query, cb) -> cb.isNull(root.join(RentOffer_.RENT_ACTS, JoinType.LEFT));
    }


    @Override
    public RentOfferResponseDto create(RentOfferRequestDto dto) {
        Customer customer = customerOldService.findById(dto.getCustomerId());

        RentOffer contract = new RentOffer();
        contract.setBranchId(SecurityUtils.getBranchId());
        contract.setCompanyId(getCompanyId());
        contract.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        contract.setCustomerId(customer.getId());
        contract = save(contract);

        return rentOfferMapper.toDto(contract);
    }

    @Override
    public RentOfferResponseDto getById(Long id) {
        return rentOfferMapper.toDto(findById(id));
    }

    @Override
    public RentOffer getByIdAndCustomerId(Long id, Long customerId) {
        return repository.findByIdAndCustomerId(id, customerId).orElseThrow(validationService.entityNotFoundExceptionThrow(OFFER));
    }

    @Override
    public RentOffer getByCustomerId(Long customerId) {
        return repository.findFirstByCustomerIdOrderByCreatedDateDesc(customerId)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(OFFER));
    }

    @Override
    public RentOfferResponseDto accept(Long id) {
        return changeStatus(id, ContractStatus.ACCEPTED);
    }

    private RentOfferResponseDto changeStatus(Long id, ContractStatus status) {
        RentOffer offer = findById(id);
        CheckRejectedStatus(offer);
        offer.setStatus(status);
        save(offer);
        return rentOfferMapper.toDto(offer);
    }

    private void CheckRejectedStatus(RentOffer offer) {
        if (ContractStatus.REJECTED.equals(offer.getStatus()) ||
            ContractStatus.ACCEPTED.equals(offer.getStatus()))
            throw validationService.badRequestExceptionThrow(STATUS_CANNOT_CHANGE, offer.getStatus().getName()).get();
    }

    @Override
    public RentOfferResponseDto reject(Long id) {
        return changeStatus(id, ContractStatus.REJECTED);
    }

    @Override
    public RentOfferResponseDto waitingForAttach(Long id) {
        return changeStatus(id, ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
    }

    @Override
    public String getHtmlTemplate(Long id) {
        return thymeleafService.getRentOfferTemplate(findById(id));
    }

    @Override
    public RentOfferResponseDto uploadContractFile(Long id, MultipartFile file) {
//        RentOffer offer = findById(id);
//        CheckRejectedStatus(offer);
//
//        UploadFileResponse response = fileUploadService.fileUpload(file, id, EntityTypeEnum.RENT_ACT.name(), DocumentTypeEnum.RENT_ACT.name());
//        offer.setFileName(response.getFileName());
//        offer.setContentType(file.getContentType());
//        offer.setFileUri(response.getFileDownloadUri());
//        offer.setStatus(ContractStatus.DOCUMENT_ATTACHED);
//        offer = save(offer);
//        return RentActMapper.INSTANCE.toDto(act);
        return null;
    }

    @Override
    public Page<RentOfferResponseDto> listByTin(TradeInContractFilter filter, String tin) {
        Specification<RentOffer> specification = (root, criteriaQuery, criteriaBuilder) -> {
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
            .map(rentOfferMapper::toDto);
    }

}
