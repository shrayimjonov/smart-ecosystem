package uz.uzkassa.cts.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.employeetimeslot.EmployeeTimeslotDTO;
import uz.uzkassa.cts.dto.timeslot.TimeslotDTO;
import uz.uzkassa.cts.repository.EmployeeRepository;
import uz.uzkassa.cts.service.CacheService;
import uz.uzkassa.cts.service.EmployeeTimeslotService;
import uz.uzkassa.cts.service.TimeslotService;

@Transactional(readOnly = true)
@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmployeeTimeslotServiceImpl extends BaseServiceImpl<User, Long> implements EmployeeTimeslotService {

    EmployeeRepository employeeRepository;
    CacheService cacheService;
    TimeslotService timeslotService;

    public EmployeeTimeslotServiceImpl(EmployeeRepository employeeRepository, CacheService cacheService, TimeslotService timeslotService) {
        super(employeeRepository);
        this.employeeRepository = employeeRepository;
        this.cacheService = cacheService;
        this.timeslotService = timeslotService;
    }

    @Override
    @Transactional
    public void create(EmployeeTimeslotDTO employeeTimeslotDTO) {
        employeeRepository.findById(employeeTimeslotDTO.getEmployeeId()).ifPresentOrElse(employee -> {
            employee.setTimeslotId(employeeTimeslotDTO.getTimeslotId());
            employeeRepository.save(employee);
            cacheService.clearByEntityType(employee.getEntityType());
        }, () -> validationService.entityNotFoundExceptionThrow(USER).get());
    }

    @Override
    public TimeslotDTO get(Long employeeId) {
        User employee = employeeRepository.findById(employeeId).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
        if (employee.getTimeslotId() != null) {
            return timeslotService.getDetail(employee.getTimeslot());
        }
        return null;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        employeeRepository.findById(id).ifPresentOrElse(employee -> {
            employee.setTimeslotId(null);
            employee.setTimeslot(null);
            employeeRepository.save(employee);
            cacheService.clearByEntityType(employee.getEntityType());
        }, () -> validationService.entityNotFoundExceptionThrow(USER).get());
    }
}
