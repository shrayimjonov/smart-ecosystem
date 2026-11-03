package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.Join;
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
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActOfReturnDTO;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActTemplateDTO;
import uz.uzkassa.cts.dto.uploadfile.response.UploadFileResponse;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.RentDeviceActOfReturnMapper;
import uz.uzkassa.cts.repository.CustomerEmployeeRepository;
import uz.uzkassa.cts.repository.RentDeviceActOfReturnRepository;
import uz.uzkassa.cts.repository.UserRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.FileUploadService;
import uz.uzkassa.cts.service.RentDeviceActOfReturnService;
import uz.uzkassa.cts.service.RentDeviceActService;
import uz.uzkassa.cts.service.ThymeleafService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class RentDeviceActOfReturnServiceImpl extends BaseServiceImpl<RentDeviceActOfReturn, Long> implements RentDeviceActOfReturnService {

    private final RentDeviceActOfReturnRepository repository;
    private final FileUploadService fileUploadService;
    private final ThymeleafService thymeleafService;
    private final RentDeviceActService rentDeviceActService;
    private final UserRepository userRepository;
    private final RentDeviceActOfReturnMapper rentDeviceActOfReturnMapper;
    private final CustomerEmployeeRepository customerEmployeeRepository;

    public RentDeviceActOfReturnServiceImpl(RentDeviceActOfReturnRepository repository,
                                            FileUploadService fileUploadService,
                                            ThymeleafService thymeleafService,
                                            RentDeviceActService rentDeviceActService,
                                            UserRepository userRepository,
                                            RentDeviceActOfReturnMapper rentDeviceActOfReturnMapper, CustomerEmployeeRepository customerEmployeeRepository) {
        super(repository);
        this.repository = repository;
        this.fileUploadService = fileUploadService;
        this.thymeleafService = thymeleafService;
        this.rentDeviceActService = rentDeviceActService;
        this.userRepository = userRepository;
        this.rentDeviceActOfReturnMapper = rentDeviceActOfReturnMapper;
        this.customerEmployeeRepository = customerEmployeeRepository;
    }

    @Override
    public List<RentDeviceActOfReturnDTO> getByCustomerId(Long id) {
        List<RentDeviceActOfReturn> acts = repository.findByCustomerId(id);
        if (acts.isEmpty())
            return new ArrayList<>();

        return acts.stream().map(rentDeviceActOfReturnMapper::toListDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<RentDeviceActOfReturnDTO> getList(BaseFilter filter) {
        Specification<RentDeviceActOfReturn> specification = getSpecification(filter);
        Page<RentDeviceActOfReturn> acts = findAll(specification, filter.getOrderedPageable());

        return acts.map(rentDeviceActOfReturnMapper::toListDto);
    }

    private Specification<RentDeviceActOfReturn> getSpecification(BaseFilter filter) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            Join<RentDeviceActOfReturn, Customer> customer = root.join(RentDeviceActOfReturn_.CUSTOMER, JoinType.INNER);
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
                Path<ContractStatus> contractStatusPath = root.get(RentDeviceActOfReturn_.STATUS);
                list.add(criteriaBuilder.equal(contractStatusPath, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get(RentDeviceActOfReturn_.CREATED_DATE);
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
    }

    @Override
    public RentDeviceActOfReturnDTO create(Long rentDeviceActId) {
        RentDeviceAct rentDeviceAct = rentDeviceActService.findById(rentDeviceActId);
        if (rentDeviceAct.getRentDeviceActDetail() == null)
            throw validationService.entityNotFoundExceptionThrow(ACCESSORIES).get();

        RentDeviceActOfReturn actOfReturn = new RentDeviceActOfReturn();
        actOfReturn.setCustomerId(rentDeviceAct.getCustomerId());
        actOfReturn.setPriceFor1pc(rentDeviceAct.getPriceFor1pcWithVat());
        actOfReturn.setStatus(ContractStatus.CREATED);
        actOfReturn.addChild(rentDeviceAct.getRentDeviceActDetail());
        save(actOfReturn);

        return rentDeviceActOfReturnMapper.toDto(actOfReturn);
    }

    @Override
    public RentDeviceActOfReturnDTO getById(Long id) {
        return rentDeviceActOfReturnMapper.toDto(findById(id));
    }

    @Override
    public String getHtmlTemplate(Long id) {
        RentDeviceActOfReturn actOfReturn = repository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(RENT));
        if (actOfReturn.getRentDeviceActDetail() == null) {
            throw validationService.entityNotFoundExceptionThrow(ACCESSORIES).get();
        }
        Customer customer = actOfReturn.getCustomer();
        RentDeviceActTemplateDTO dto = new RentDeviceActTemplateDTO();
        dto.setCompanyName(customer.getCompanyName());
        dto.setModelName(actOfReturn.getRentDeviceActDetail().getDevice().getModel().getName());
        dto.setSerialNumber(actOfReturn.getRentDeviceActDetail().getDevice().getSerialNumber());
        dto.setPrice(actOfReturn.getPriceFor1pc());
        dto.setCreatedDate(actOfReturn.getCreatedDate());

        customerEmployeeRepository.findFirstByCustomerIdAndRole(customer.getId(), Role.DIRECTOR).ifPresent(customerEmployee -> {
            dto.setOwner(customerEmployee.getName());
            dto.setLogin(customerEmployee.getCustomer().getPhone());
        });
        return thymeleafService.generateRentDeviceActReturn(dto);
    }

    @Override
    public RentDeviceActOfReturnDTO uploadFile(Long id, MultipartFile file) {
        RentDeviceActOfReturn act = findById(id);
        checkStatus(act);

        UploadFileResponse response = fileUploadService.upload(file, id, EntityTypeEnum.RENT_DEVICE_ACT_OF_RETURN.name(), DocumentType.RENT_DEVICE_ACT_OF_RETURN.name());
        act.setFileName(response.getFileName());
        act.setFileUri(response.getFileDownloadUri());
        act.setStatus(ContractStatus.DOCUMENT_ATTACHED);
        act = save(act);
        return rentDeviceActOfReturnMapper.toDto(act);
    }

    private void checkStatus(RentDeviceActOfReturn act) {
        if (ContractStatus.REJECTED.equals(act.getStatus()) ||
                ContractStatus.ACCEPTED.equals(act.getStatus()))
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_STATUS, act.getStatus().getName()).get();
    }

    @Override
    public RentDeviceActOfReturnDTO accept(Long id) {
        RentDeviceActOfReturn actOfReturn = findById(id);
        if (ContractStatus.ACCEPTED == actOfReturn.getStatus()) {
            throw validationService.badRequestExceptionThrow(ACT_ALREADY_CONFIRMED).get();
        }
        if (ContractStatus.REJECTED == actOfReturn.getStatus()) {
            throw validationService.badRequestExceptionThrow(REJECTED_ACT).get();
        }
        if (StringUtils.isEmpty(actOfReturn.getFileName())) {
            throw validationService.badRequestExceptionThrow(UPLOAD_FILE).get();
        }
        if (actOfReturn.getRentDeviceActDetail() == null) {
            throw validationService.requiredExceptionThrow(ACCESSORIES).get();
        }

        actOfReturn.setStatus(ContractStatus.ACCEPTED);
        actOfReturn.setAcceptDate(LocalDateTime.now());
        actOfReturn = save(actOfReturn);

        RentDeviceAct rentDeviceAct = actOfReturn.getRentDeviceActDetail().getDeviceAct();
        rentDeviceAct.setStatus(ContractStatus.CLOSED);
        rentDeviceAct.setClosedDate(LocalDateTime.now());
        rentDeviceActService.save(rentDeviceAct);

        return rentDeviceActOfReturnMapper.toDto(actOfReturn);
    }

    @Override
    public RentDeviceActOfReturnDTO changeToAttach(Long id) {
        RentDeviceActOfReturn actOfReturn = findById(id);
        checkStatus(actOfReturn);

        actOfReturn.setStatus(ContractStatus.WAITING_FOR_DOCUMENT_ATTACHMENT);
        save(actOfReturn);
        return rentDeviceActOfReturnMapper.toDto(actOfReturn);
    }

    @Override
    public RentDeviceActOfReturnDTO changeDocFlowStatus(ActChangeDocFlowRequestDto dto) {
        RentDeviceActOfReturn act = findById(dto.getId());
        act.setDocFlowStatus(dto.getDocFlowStatus());
        act.setDocFlowReason(dto.getDocFlowReason());
        save(act);
        return rentDeviceActOfReturnMapper.toDto(act);
    }

    @Override
    public RentDeviceActOfReturnDTO findByCustomerIdAndDeviceSerialNumber(Long customerId, String deviceSerialNumber) {
        return rentDeviceActOfReturnMapper.toListDto(repository.findByCustomerIdAndDeviceSerialNumber(customerId, deviceSerialNumber));
    }
}
