package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.hrms.LeaveAllowance;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceDTO;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceDetailDTO;
import uz.uzkassa.cts.dto.leaveallowance.LeaveAllowanceListDTO;
import uz.uzkassa.cts.filter.LeaveAllowanceFilter;
import uz.uzkassa.cts.repository.LeaveAllowanceRepository;
import uz.uzkassa.cts.service.LeaveAllowanceService;
import uz.uzkassa.cts.utils.DateUtil;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LeaveAllowanceServiceImpl extends BaseServiceImpl<LeaveAllowance, Long> implements LeaveAllowanceService {

    LeaveAllowanceRepository leaveAllowanceRepository;
    ValidationContext validationContext;

    public LeaveAllowanceServiceImpl(LeaveAllowanceRepository leaveAllowanceRepository, ValidationContext validationContext) {
        super(leaveAllowanceRepository);
        this.leaveAllowanceRepository = leaveAllowanceRepository;
        this.validationContext = validationContext;
    }

    @Transactional
    public void create(Long employeeId) {
        LeaveAllowanceDTO leaveAllowanceDTO = new LeaveAllowanceDTO();
        leaveAllowanceDTO.setEmployeeId(employeeId);
        leaveAllowanceDTO.setYear(LocalDate.now().getYear());
        this.create(leaveAllowanceDTO);
    }

    @Override
    @Transactional
    public Long create(LeaveAllowanceDTO leaveAllowanceDTO) {
        validationContext.validate(leaveAllowanceDTO);
        LeaveAllowance leaveAllowance = new LeaveAllowance();
        leaveAllowance.setEmployeeId(leaveAllowanceDTO.getEmployeeId());
        leaveAllowance.setTotalAllowance(21 * 8 * 60);
        leaveAllowance.setYear(leaveAllowanceDTO.getYear());
        return leaveAllowanceRepository.save(leaveAllowance).getId();
    }

    @Override
    @Transactional
    public Long update(Long id, LeaveAllowanceDTO leaveAllowanceDTO) {
        leaveAllowanceDTO.setId(id);
        validationContext.validate(leaveAllowanceDTO);
        return leaveAllowanceRepository.findById(id).map(leaveAllowance -> {
            leaveAllowance.setEmployeeId(leaveAllowanceDTO.getEmployeeId());
            leaveAllowance.setYear(leaveAllowanceDTO.getYear());
            return leaveAllowanceRepository.save(leaveAllowance).getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(LEAVE_ALLOWANCE));
    }

    @Override
    public LeaveAllowanceDetailDTO get(Long id) {
        return leaveAllowanceRepository.findById(id).map(leaveAllowance -> {
            LeaveAllowanceDetailDTO leaveAllowanceDetailDTO = new LeaveAllowanceDetailDTO();
            leaveAllowanceDetailDTO.setId(leaveAllowance.getId());
            leaveAllowanceDetailDTO.setEmployee(leaveAllowance.getEmployee().toCommonDTO());
            leaveAllowanceDetailDTO.setYear(leaveAllowance.getYear());
            leaveAllowanceDetailDTO.setTotalAllowance(leaveAllowance.getTotalAllowance());
            leaveAllowanceDetailDTO.setTotalAllowanceTitle(DateUtil.convertMinutesToString(leaveAllowance.getTotalAllowance()));
            leaveAllowanceDetailDTO.setRemainingAllowance(leaveAllowance.getRemainingAllowance());
            leaveAllowanceDetailDTO.setRemainingAllowanceTitle(DateUtil.convertMinutesToString(leaveAllowance.getRemainingAllowance()));
            return leaveAllowanceDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(LEAVE_ALLOWANCE));
    }

    @Override
    public Page<LeaveAllowanceListDTO> getList(LeaveAllowanceFilter filter) {
        ResultList<LeaveAllowance> resultList = leaveAllowanceRepository.getResultList(filter);
        List<LeaveAllowanceListDTO> result = resultList
                .getList()
                .stream()
                .map(leaveAllowance -> {
                    LeaveAllowanceListDTO leaveAllowanceListDTO = new LeaveAllowanceListDTO();
                    leaveAllowanceListDTO.setId(leaveAllowance.getId());
                    leaveAllowanceListDTO.setEmployee(leaveAllowance.getEmployee().toCommonDTO());
                    leaveAllowanceListDTO.setTotalAllowance(leaveAllowance.getTotalAllowance());
                    leaveAllowanceListDTO.setTotalAllowanceTitle(DateUtil.convertMinutesToString(leaveAllowance.getTotalAllowance()));
                    leaveAllowanceListDTO.setRemainingAllowance(leaveAllowance.getRemainingAllowance());
                    leaveAllowanceListDTO.setRemainingAllowanceTitle(DateUtil.convertMinutesToString(leaveAllowance.getRemainingAllowance()));
                    return leaveAllowanceListDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    @Transactional
    public void recalculate(Long employeeId, Integer allowance, Integer year) {
        leaveAllowanceRepository.findFirstByEmployeeIdAndYearAndDeletedIsFalse(employeeId, year).ifPresent(leaveAllowance -> {
            if (leaveAllowance.getRemainingAllowance() != null && leaveAllowance.getRemainingAllowance() - allowance < 0) {
                throw validationService.badRequestExceptionThrow(NO_ENOUGH_ALLOWANCE).get();
            }
            if (leaveAllowance.getRemainingAllowance() == null) {
                leaveAllowance.setRemainingAllowance(leaveAllowance.getTotalAllowance() - allowance);
            } else {
                leaveAllowance.setRemainingAllowance(leaveAllowance.getRemainingAllowance() - allowance);
            }
            leaveAllowanceRepository.save(leaveAllowance);
        });
    }
}
