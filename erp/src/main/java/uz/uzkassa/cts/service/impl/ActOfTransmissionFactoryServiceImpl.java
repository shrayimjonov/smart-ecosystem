package uz.uzkassa.cts.service.impl;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.*;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionFullResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.response.ActOfTransmissionResponseDto;
import uz.uzkassa.cts.dto.actoftransmission.smartpos.ActOfTransmissionSmartposDTO;
import uz.uzkassa.cts.dto.actoftransmission.smartpos.ActPaymentScheduleSmartposDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.contractforleasing.request.GeneratePoaTemplateDTO;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.filter.ActOfTransmissionFilter;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.mappings.ActOfTransmissionMapper;
import uz.uzkassa.cts.repository.ActOfTransmissionDetailRepository;
import uz.uzkassa.cts.repository.ActOfTransmissionRepository;
import uz.uzkassa.cts.repository.ContractForLeasingRepository;
import uz.uzkassa.cts.service.ActOfTransmissionFactoryService;
import uz.uzkassa.cts.service.ActOfTransmissionStrategyService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ActOfTransmissionFactoryServiceImpl extends BaseServiceImpl<ActOfTransmission, Long> implements ActOfTransmissionFactoryService {

    private final ActOfTransmissionRepository actOfTransmissionRepository;
    private final Map<String, ActOfTransmissionStrategyService> contractForLeasingService;
    private final ActOfTransmissionDetailRepository actOfTransmissionDetailRepository;
    private final ContractForLeasingRepository contractForLeasingRepository;
    private final ActOfTransmissionMapper actOfTransmissionMapper;

    public ActOfTransmissionFactoryServiceImpl(ActOfTransmissionRepository actOfTransmissionRepository,
                                               Map<String, ActOfTransmissionStrategyService> contractForLeasingService,
                                               ActOfTransmissionDetailRepository actOfTransmissionDetailRepository,
                                               ContractForLeasingRepository contractForLeasingRepository,
                                               ActOfTransmissionMapper actOfTransmissionMapper) {
        super(actOfTransmissionRepository);
        this.actOfTransmissionRepository = actOfTransmissionRepository;
        this.contractForLeasingService = contractForLeasingService;
        this.actOfTransmissionDetailRepository = actOfTransmissionDetailRepository;
        this.contractForLeasingRepository = contractForLeasingRepository;
        this.actOfTransmissionMapper = actOfTransmissionMapper;
    }

    @Override
    public List<ActOfTransmissionResponseDto> getByContractId(Long id) {
        ContractForLeasing contractForLeasing = contractForLeasingRepository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(CONTRACT));
        switch (contractForLeasing.getCompanyType()) {
            case STS -> {
                return contractForLeasingService.get("STS_ACT").getByContractId(id);
            }
            case PRIME -> {
                return contractForLeasingService.get("PRIME_ACT").getByContractId(id);
            }
        }
        return null;
    }

    @Override
    public Page<ActOfTransmissionResponseDto> getList(ActOfTransmissionFilter filter) {
        Specification<ActOfTransmission> specification = (root, criteriaQuery, criteriaBuilder) -> {
            criteriaQuery.distinct(true);

            ArrayList<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotEmpty(filter.getSearch())) {
                Path<String> id = root.get("id");
                list.add(criteriaBuilder.like(id, filter.getSearchForQuery()));
            }
            if (filter.getFrom() != null && filter.getTo() != null) {
                Path<LocalDateTime> createDate = root.get("createdDate");
                list.add(criteriaBuilder.between(createDate, filter.getFrom().atStartOfDay(), filter.getTo().atTime(LocalTime.MAX)));
            }
            if (filter.getCompanyType() != null) {
                Path<CompanyType> companyTypePath = root.get(ActOfTransmission_.COMPANY_TYPE);
                list.add(criteriaBuilder.equal(companyTypePath, filter.getCompanyType()));
            }
            if (filter.getStatus() != null) {
                Path<ContractStatus> status = root.get("status");
                list.add(criteriaBuilder.equal(status, ContractStatus.valueOf(filter.getStatus())));
            }
            if (filter.getTariffId() != null) {
                Path<ContractForLeasing> contractForLeasingPath = root.join(ActOfTransmission_.CONTRACT, JoinType.INNER);
                Path<Long> tariffPath = contractForLeasingPath.get(ContractForLeasing_.MODEL_PRICE_HISTORY_ID);
                list.add(criteriaBuilder.equal(tariffPath, filter.getTariffId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        return actOfTransmissionRepository.findAll(specification, filter.getOrderedPageable())
                .map(actOfTransmissionMapper::toDto);
    }

    @Override
    public Page<ActOfTransmissionSmartposDTO> getPrimeList(BaseFilter filter) {
        if (StringUtils.isEmpty(filter.getTin())) {
            throw validationService.badRequestExceptionThrow(REQUEST, TIN).get();
        }
        ResultList<ActOfTransmissionSmartposDTO> resultList = actOfTransmissionRepository.getResultList(filter);
        return new PageImpl<>
                (resultList.getList(), filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<ActPaymentScheduleSmartposDTO> getScheduleList(Long actId) {
        List<ActPaymentScheduleSmartposDTO> result = new ArrayList<>();
        ActOfTransmission act = findById(actId);
        if (CollectionUtils.isNotEmpty(act.getDetails())) {
            act.getDetails().forEach(detail -> detail.getActPaymentSchedules().forEach(schedule -> {
                ActPaymentScheduleSmartposDTO dto = new ActPaymentScheduleSmartposDTO();
                dto.setAmount(schedule.getAmount());
                dto.setBalance(schedule.getBalance());
                dto.setPaymentDate(schedule.getPaymentDate());
                dto.setPaid(schedule.getInvoiceId() != null);
                result.add(dto);
            }));
        }
        return result;
    }

    @Override
    public ActOfTransmissionFullResponseDto getById(Long id) {
        ActOfTransmission actOfTransmission = findById(id);
        return actOfTransmissionMapper.toFullDto(actOfTransmission);
    }

    @Override
    public ActOfTransmission getByDeviceId(Long deviceId) {
        ActOfTransmissionDetail detail = actOfTransmissionDetailRepository.findByDeviceId(deviceId);
        if (detail != null) {
            return detail.getActOfTransmission();
        }
        return null;
    }

    @Override
    public ActOfTransmission getByDeviceIdAndCustomerId(Long deviceId, Long customerId) {
        ActOfTransmissionDetail detail = actOfTransmissionDetailRepository.findFirstByDeviceIdAndActOfTransmission_Contract_CustomerId(deviceId, customerId);
        if (detail != null) {
            return detail.getActOfTransmission();
        }
        return null;
    }

    @Override
    public String getHtmlTemplate(Long id) {
        ActOfTransmission actOfTransmission = findById(id);
        switch (actOfTransmission.getCompanyType()) {
            case STS -> {
                return contractForLeasingService.get("STS_ACT").getHtmlTemplate(id);
            }
            case PRIME -> {
                return contractForLeasingService.get("PRIME_ACT").getHtmlTemplate(id);
            }
        }
        return null;
    }

    @Override
    public String getScheduleHtmlTemplate(Long id) {
        ActOfTransmission actOfTransmission = findById(id);
        switch (actOfTransmission.getCompanyType()) {
            case STS -> {
                return contractForLeasingService.get("STS_ACT").getScheduleHtmlTemplate(id);
            }
            case PRIME -> {
                return contractForLeasingService.get("PRIME_ACT").getScheduleHtmlTemplate(id);
            }
        }
        return null;
    }

    @Override
    public ActOfTransmissionFullResponseDto uploadFile(Long id, MultipartFile file) {
        ActOfTransmission actOfTransmission = findById(id);
        switch (actOfTransmission.getCompanyType()) {
            case STS -> {
                return contractForLeasingService.get("STS_ACT").uploadFile(id, file);
            }
            case PRIME -> {
                return contractForLeasingService.get("PRIME_ACT").uploadFile(id, file);
            }
        }
        return null;
    }

    @Override
    public ActOfTransmissionFullResponseDto accept(Long id, LocalDate date) {
        ActOfTransmission actOfTransmission = findById(id);
        switch (actOfTransmission.getCompanyType()) {
            case STS -> {
                return contractForLeasingService.get("STS_ACT").accept(id, date);
            }
            case PRIME -> {
                return contractForLeasingService.get("PRIME_ACT").accept(id, date);
            }
        }
        return null;
    }

    @Override
    public ActOfTransmissionFullResponseDto changeToAttach(Long id) {
        ActOfTransmission actOfTransmission = findById(id);
        switch (actOfTransmission.getCompanyType()) {
            case STS -> {
                return contractForLeasingService.get("STS_ACT").changeToAttach(id);
            }
            case PRIME -> {
                return contractForLeasingService.get("PRIME_ACT").changeToAttach(id);
            }
        }
        return null;
    }

    @Override
    public ActOfTransmissionFullResponseDto defect(Long id) {
        ActOfTransmission actOfTransmission = findById(id);
        ActOfTransmissionStrategyService strategyService = contractForLeasingService.get(actOfTransmission.getCompanyType().name() + "_ACT");
        if (strategyService == null) {
            throw validationService.badRequestExceptionThrow("No ActOfTransmissionStrategyService found for company type: " + actOfTransmission.getCompanyType()).get();
        }
        return strategyService.defect(id);
    }

    @Override
    public ActOfTransmissionFullResponseDto uploadScheduleFile(Long id, MultipartFile file) {
        ActOfTransmission actOfTransmission = findById(id);
        switch (actOfTransmission.getCompanyType()) {
            case STS -> {
                return contractForLeasingService.get("STS_ACT").uploadScheduleFile(id, file);
            }
            case PRIME -> {
                return contractForLeasingService.get("PRIME_ACT").uploadScheduleFile(id, file);
            }
        }
        return null;
    }

    @Override
    public ActOfTransmissionFullResponseDto uploadPowerAttorneyFile(Long id, MultipartFile file) {
        ActOfTransmission actOfTransmission = findById(id);
        switch (actOfTransmission.getCompanyType()) {
            case STS -> {
                return contractForLeasingService.get("STS_ACT").uploadPowerAttorneyFile(id, file);
            }
            case PRIME -> {
                return contractForLeasingService.get("PRIME_ACT").uploadPowerAttorneyFile(id, file);
            }
        }
        return null;
    }

    @Override
    public List<ActOfTransmission> findForInvoiceCreate() {
        return actOfTransmissionRepository.findByStatusAndCompanyTypeAndInvoiceIsNull(ContractStatus.CLOSED, CompanyType.STS);
    }

    @Override
    public String generatePoaTemplate(GeneratePoaTemplateDTO dto) {
        ActOfTransmission actOfTransmission = findById(dto.getActOfTransmissionId());
        if (actOfTransmission.getContract().getCustomer().getCustomerBank() == null) {
            throw validationService.badRequestExceptionThrow(CUSTOMER_NO_BANK).get();
        }
        switch (actOfTransmission.getCompanyType()) {
            case STS -> {
                return contractForLeasingService.get("STS_ACT").generatePoaTemplate(dto);
            }
            case PRIME -> {
                return contractForLeasingService.get("PRIME_ACT").generatePoaTemplate(dto);
            }
        }
        return null;
    }

    @Override
    public ActOfTransmissionFullResponseDto changeDocFlowStatus(ActChangeDocFlowRequestDto dto) {
        ActOfTransmission actOfTransmission = findById(dto.getId());
        actOfTransmission.setDocFlowStatus(dto.getDocFlowStatus());
        actOfTransmission.setDocFlowReason(dto.getDocFlowReason());
        save(actOfTransmission);

        return actOfTransmissionMapper.toFullDto(actOfTransmission);
    }
}
