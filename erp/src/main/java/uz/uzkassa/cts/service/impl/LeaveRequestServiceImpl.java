package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.hrms.Department;
import uz.uzkassa.cts.domain.hrms.LeaveRequest;
import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestDTO;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestDetailDTO;
import uz.uzkassa.cts.dto.leaverequest.LeaveRequestListDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.numbering.NumberingContext;
import uz.uzkassa.cts.repository.LeaveRequestRepository;
import uz.uzkassa.cts.service.ApproverService;
import uz.uzkassa.cts.service.LeaveRequestService;
import uz.uzkassa.cts.validator.ValidationContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static uz.uzkassa.cts.constants.Constants.INCREMENT_NUMBER;
import static uz.uzkassa.cts.constants.Constants.NUMBER;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LeaveRequestServiceImpl extends BaseServiceImpl<LeaveRequest, Long> implements LeaveRequestService {

    LeaveRequestRepository leaveRequestRepository;
    ApproverService approverService;
    ValidationContext validationContext;
    NumberingContext numberingContext;

    public LeaveRequestServiceImpl(LeaveRequestRepository leaveRequestRepository, ApproverService approverService, ValidationContext validationContext, NumberingContext numberingContext) {
        super(leaveRequestRepository);
        this.leaveRequestRepository = leaveRequestRepository;
        this.approverService = approverService;
        this.validationContext = validationContext;
        this.numberingContext = numberingContext;
    }

    @Override
    @Transactional
    public Long create(LeaveRequestDTO leaveRequestDTO) {
        validationContext.validate(leaveRequestDTO);
        Map<String, String> numberingMap = numberingContext.generateNumber(Prefix.LEAVE_REQUEST, getCompanyId());
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setLeaveRequestNumber(numberingMap.get(NUMBER));
        leaveRequest.setIncrementNumber(Long.valueOf(numberingMap.get(INCREMENT_NUMBER)));
        leaveRequest.setDescription(leaveRequestDTO.getDescription());
        leaveRequest.setReasonId(leaveRequestDTO.getReasonId());
        leaveRequest.setFromDate(leaveRequestDTO.getFromDate());
        leaveRequest.setToDate(leaveRequestDTO.getToDate());
        leaveRequest.setDuration(leaveRequestDTO.getFromDate(), leaveRequestDTO.getToDate());
        leaveRequest.setCompanyId(getCompanyId());
        Long id = leaveRequestRepository.save(leaveRequest).getId();
        approverService.create(leaveRequest.getEntityType(), id, leaveRequestDTO.getApprovers());
        return id;
    }

    @Override
    @Transactional
    public Long update(Long id, LeaveRequestDTO leaveRequestDTO) {
        return leaveRequestRepository.findById(id)
                .map(leaveRequest -> {
                    validationContext.validate(leaveRequestDTO);
                    leaveRequest.setDescription(leaveRequestDTO.getDescription());
                    leaveRequest.setReasonId(leaveRequestDTO.getReasonId());
                    leaveRequest.setFromDate(leaveRequestDTO.getFromDate());
                    leaveRequest.setToDate(leaveRequestDTO.getToDate());
                    leaveRequest.setDuration(leaveRequestDTO.getFromDate(), leaveRequestDTO.getToDate());
                    return leaveRequestRepository.save(leaveRequest).getId();
                })
                .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(REQUEST).get());
    }

    @Override
    public LeaveRequestDetailDTO get(Long id) {
        return leaveRequestRepository.findById(id)
                .map(leaveRequest -> {
                    LeaveRequestDetailDTO leaveRequestDetailDTO = new LeaveRequestDetailDTO();
                    leaveRequestDetailDTO.setId(leaveRequest.getId());
                    leaveRequestDetailDTO.setLeaveRequestNumber(leaveRequest.getLeaveRequestNumber());
                    leaveRequestDetailDTO.setDescription(leaveRequest.getDescription());
                    leaveRequestDetailDTO.setFromDate(leaveRequest.getFromDate());
                    leaveRequestDetailDTO.setToDate(leaveRequest.getToDate());
                    leaveRequestDetailDTO.setDuration(leaveRequest.getDuration());
                    leaveRequestDetailDTO.setCreator(leaveRequest.getCreator().toCommonDTO());
                    leaveRequestDetailDTO.setReason(leaveRequest.getReason().toCommonDTO());
                    leaveRequestDetailDTO.setStatus(leaveRequest.getStatus());
                    leaveRequestDetailDTO.setApprover(approverService.getCurrentApprover(EntityTypeEnum.LEAVE_REQUEST, leaveRequest.getEntityId()));
                    return leaveRequestDetailDTO;
                })
                .orElseThrow(() -> validationService.entityNotFoundExceptionThrow(REQUEST).get());
    }

    @Override
    public Page<LeaveRequestListDTO> getList(BaseFilter filter) {
        ResultList<LeaveRequest> resultList = leaveRequestRepository.getResultList(filter);
        List<LeaveRequestListDTO> result = resultList.getList()
                .stream()
                .map(leaveRequest -> {
                    LeaveRequestListDTO leaveRequestListDTO = new LeaveRequestListDTO();
                    leaveRequestListDTO.setId(leaveRequest.getId());
                    leaveRequestListDTO.setLeaveRequestNumber(leaveRequest.getLeaveRequestNumber());
                    leaveRequestListDTO.setCreator(leaveRequest.getCreator().toCommonDTO());
                    leaveRequestListDTO.setDescription(leaveRequest.getDescription());
                    leaveRequestListDTO.setFromDate(leaveRequest.getFromDate());
                    leaveRequestListDTO.setToDate(leaveRequest.getToDate());
                    leaveRequestListDTO.setDuration(leaveRequest.getDuration());
                    leaveRequestListDTO.setReason(leaveRequest.getReason().toCommonDTO());
                    leaveRequestListDTO.setStatus(leaveRequest.getStatus());
                    leaveRequestListDTO.setCompany(leaveRequest.getCompany().toCommonDTO());
                    leaveRequestListDTO.setDepartment(Optional.ofNullable(leaveRequest.getCreator().getDepartment()).map(Department::toCommonDTO).orElse(null));
                    leaveRequestListDTO.setPosition(Optional.ofNullable(leaveRequest.getCreator().getPosition()).map(Position::toCommonDTO).orElse(null));
                    return leaveRequestListDTO;
                })
                .toList();
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        leaveRequestRepository.deleteById(id);
    }
}
