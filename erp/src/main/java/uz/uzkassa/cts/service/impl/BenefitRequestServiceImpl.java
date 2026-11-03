package uz.uzkassa.cts.service.impl;

import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.hrms.BenefitRequest;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.benefitrequest.BenefitRequestDTO;
import uz.uzkassa.cts.dto.benefitrequest.admin.BenefitRequestDetailAdminDTO;
import uz.uzkassa.cts.dto.benefitrequest.admin.BenefitRequestListAdminDTO;
import uz.uzkassa.cts.dto.benefitrequest.cabinet.BenefitRequestDetailCabinetDTO;
import uz.uzkassa.cts.dto.benefitrequest.cabinet.BenefitRequestListCabinetDTO;
import uz.uzkassa.cts.enums.ApproverCommonStatus;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.enums.Priority;
import uz.uzkassa.cts.filter.BenefitRequestFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.BenefitRequestRepository;
import uz.uzkassa.cts.repository.CompanyRepository;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.repository.ReferenceRepository;
import uz.uzkassa.cts.service.BenefitRequestService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;
import static uz.uzkassa.cts.constants.Constants.INCREMENT_NUMBER;
import static uz.uzkassa.cts.constants.Constants.NUMBER;

/**
 * Created date time: 24.12.2024 12:09
 */

@Transactional(readOnly = true)
@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class BenefitRequestServiceImpl extends BaseServiceImpl<BenefitRequest, Long> implements BenefitRequestService {

    BenefitRequestRepository benefitRequestRepository;
    EmployeeRepository employeeRepository;
    ReferenceRepository referenceRepository;
    CompanyRepository companyRepository;
    ValidationContext validationContext;
    NumberingContext numberingContext;

    public BenefitRequestServiceImpl(BenefitRequestRepository benefitRequestRepository, EmployeeRepository employeeRepository, ReferenceRepository referenceRepository, CompanyRepository companyRepository, ValidationContext validationContext, NumberingContext numberingContext) {
        super(benefitRequestRepository);
        this.benefitRequestRepository = benefitRequestRepository;
        this.employeeRepository = employeeRepository;
        this.referenceRepository = referenceRepository;
        this.companyRepository = companyRepository;
        this.validationContext = validationContext;
        this.numberingContext = numberingContext;
    }

    @Override
    @Transactional
    public Long create(BenefitRequestDTO benefitRequestDTO) {
        validationContext.validate(benefitRequestDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.BENEFIT_REQUEST, benefitRequestDTO.getCompanyId());
        BenefitRequest benefitRequest = new BenefitRequest();
        benefitRequest.setBenefitRequestNumber(numberingMap.get(NUMBER));
        benefitRequest.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        benefitRequest.setDescription(benefitRequestDTO.getDescription());
        benefitRequest.setPriority(Optional.ofNullable(benefitRequestDTO.getPriority()).orElse(Priority.LOW));
        benefitRequest.setRequestDate(benefitRequestDTO.getRequestDate());
        benefitRequest.setCompanyId(benefitRequestDTO.getCompanyId());
        benefitRequest.setApproverId(benefitRequestDTO.getApproverId());
        benefitRequest.setTypeId(benefitRequestDTO.getTypeId());
        benefitRequest.setQuantity(benefitRequestDTO.getQuantity());
        return benefitRequestRepository.save(benefitRequest).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, BenefitRequestDTO benefitRequestDTO) {
        validationContext.validate(benefitRequestDTO);
        return benefitRequestRepository.findById(id).map(benefitRequest -> {
            benefitRequest.setDescription(benefitRequestDTO.getDescription());
            benefitRequest.setRequestDate(benefitRequestDTO.getRequestDate());
            benefitRequest.setPriority(Optional.ofNullable(benefitRequestDTO.getPriority()).orElse(Priority.LOW));
            if (!Objects.equals(benefitRequest.getCompanyId(), benefitRequestDTO.getCompanyId())) {
                benefitRequest.setCompany(companyRepository.findById(benefitRequestDTO.getCompanyId()).orElseThrow(validationService.entityNotFoundExceptionThrow(COMPANY)));
                benefitRequest.setCompanyId(benefitRequestDTO.getCompanyId());
            }
            if (!Objects.equals(benefitRequest.getApproverId(), benefitRequestDTO.getApproverId())) {
                benefitRequest.setApprover(employeeRepository.findById(benefitRequestDTO.getApproverId()).orElseThrow(validationService.entityNotFoundExceptionThrow(EMPLOYEE)));
                benefitRequest.setApproverId(benefitRequestDTO.getApproverId());
            }
            if (!Objects.equals(benefitRequest.getTypeId(), benefitRequestDTO.getTypeId())) {
                benefitRequest.setType(referenceRepository.findById(benefitRequestDTO.getTypeId()).orElseThrow(validationService.entityNotFoundExceptionThrow(TYPE)));
                benefitRequest.setTypeId(benefitRequestDTO.getTypeId());
            }
            benefitRequest.setQuantity(benefitRequestDTO.getQuantity());
            return benefitRequestRepository.save(benefitRequest).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    public BenefitRequestDetailAdminDTO getForAdmin(Long id) {
        return benefitRequestRepository.findById(id).map(benefitRequest -> {
            BenefitRequestDetailAdminDTO benefitRequestDetailAdminDTO = new BenefitRequestDetailAdminDTO();
            benefitRequestDetailAdminDTO.setId(benefitRequest.getId());
            benefitRequestDetailAdminDTO.setBenefitRequestNumber(benefitRequest.getBenefitRequestNumber());
            benefitRequestDetailAdminDTO.setDescription(benefitRequest.getDescription());
            benefitRequestDetailAdminDTO.setPriority(benefitRequest.getPriority());
            benefitRequestDetailAdminDTO.setRequestDate(benefitRequest.getRequestDate());
            benefitRequestDetailAdminDTO.setCreator(benefitRequest.getCreator().toCommonDTO());
            benefitRequestDetailAdminDTO.setCompany(benefitRequest.getCompany().toCommonDTO());
            benefitRequestDetailAdminDTO.setApprover(benefitRequest.getApprover().toCommonDTO());
            benefitRequestDetailAdminDTO.setType(benefitRequest.getType().toCommonDTO());
            benefitRequestDetailAdminDTO.setQuantity(benefitRequest.getQuantity());
            benefitRequestDetailAdminDTO.setCreatedDate(benefitRequest.getCreatedDate());
            return benefitRequestDetailAdminDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    public Page<BenefitRequestListAdminDTO> getListForAdmin(BenefitRequestFilter filter) {
        ResultList<BenefitRequest> resultList = benefitRequestRepository.getResultList(filter);
        List<BenefitRequestListAdminDTO> result = resultList
                .getList()
                .stream()
                .map(benefitRequest -> {
                    BenefitRequestListAdminDTO benefitRequestListAdminDTO = new BenefitRequestListAdminDTO();
                    benefitRequestListAdminDTO.setId(benefitRequest.getId());
                    benefitRequestListAdminDTO.setBenefitRequestNumber(benefitRequest.getBenefitRequestNumber());
                    benefitRequestListAdminDTO.setDescription(benefitRequest.getDescription());
                    benefitRequestListAdminDTO.setStatus(benefitRequest.getStatus());
                    benefitRequestListAdminDTO.setPriority(benefitRequest.getPriority());
                    benefitRequestListAdminDTO.setRequestDate(benefitRequest.getRequestDate());
                    benefitRequestListAdminDTO.setCreator(benefitRequest.getCreator().toCommonDTO());
                    benefitRequestListAdminDTO.setApprover(benefitRequest.getApprover().toCommonDTO());
                    benefitRequestListAdminDTO.setType(benefitRequest.getType().toCommonDTO());
                    benefitRequestListAdminDTO.setQuantity(benefitRequest.getQuantity());
                    benefitRequestListAdminDTO.setCreatedDate(benefitRequest.getCreatedDate());
                    return benefitRequestListAdminDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public BenefitRequestDetailCabinetDTO getForCabinet(Long id) {
        return benefitRequestRepository.findById(id).map(benefitRequest -> {
            BenefitRequestDetailCabinetDTO benefitRequestDetailCabinetDTO = new BenefitRequestDetailCabinetDTO();
            benefitRequestDetailCabinetDTO.setId(benefitRequest.getId());
            benefitRequestDetailCabinetDTO.setBenefitRequestNumber(benefitRequest.getBenefitRequestNumber());
            benefitRequestDetailCabinetDTO.setDescription(benefitRequest.getDescription());
            benefitRequestDetailCabinetDTO.setPriority(benefitRequest.getPriority());
            benefitRequestDetailCabinetDTO.setRequestDate(benefitRequest.getRequestDate());
            benefitRequestDetailCabinetDTO.setCreator(benefitRequest.getCreator().toCommonDTO());
            benefitRequestDetailCabinetDTO.setCompany(benefitRequest.getCompany().toCommonDTO());
            benefitRequestDetailCabinetDTO.setApprover(benefitRequest.getApprover().toCommonDTO());
            benefitRequestDetailCabinetDTO.setType(benefitRequest.getType().toCommonDTO());
            benefitRequestDetailCabinetDTO.setQuantity(benefitRequest.getQuantity());
            benefitRequestDetailCabinetDTO.setCreatedDate(benefitRequest.getCreatedDate());
            return benefitRequestDetailCabinetDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(REQUEST));
    }

    @Override
    public Page<BenefitRequestListCabinetDTO> getListForCabinet(BenefitRequestFilter filter) {
        ResultList<BenefitRequest> resultList = benefitRequestRepository.getResultList(filter);
        List<BenefitRequestListCabinetDTO> result = resultList
                .getList()
                .stream()
                .map(benefitRequest -> {
                    BenefitRequestListCabinetDTO benefitRequestListCabinetDTO = new BenefitRequestListCabinetDTO();
                    benefitRequestListCabinetDTO.setId(benefitRequest.getId());
                    benefitRequestListCabinetDTO.setBenefitRequestNumber(benefitRequest.getBenefitRequestNumber());
                    benefitRequestListCabinetDTO.setDescription(benefitRequest.getDescription());
                    benefitRequestListCabinetDTO.setStatus(benefitRequest.getStatus());
                    benefitRequestListCabinetDTO.setPriority(benefitRequest.getPriority());
                    benefitRequestListCabinetDTO.setRequestDate(benefitRequest.getRequestDate());
                    benefitRequestListCabinetDTO.setCreator(benefitRequest.getCreator().toCommonDTO());
                    benefitRequestListCabinetDTO.setApprover(benefitRequest.getApprover().toCommonDTO());
                    benefitRequestListCabinetDTO.setType(benefitRequest.getType().toCommonDTO());
                    benefitRequestListCabinetDTO.setQuantity(benefitRequest.getQuantity());
                    benefitRequestListCabinetDTO.setCreatedDate(benefitRequest.getCreatedDate());
                    return benefitRequestListCabinetDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, ApproverCommonStatus status) {
        benefitRequestRepository
                .findById(id)
                .ifPresentOrElse(benefitRequest -> {
                    benefitRequest.setStatus(status);
                    benefitRequestRepository.save(benefitRequest);
                }, () -> {
                    throw validationService.entityNotFoundExceptionThrow(REQUEST).get();
                });
    }
}
