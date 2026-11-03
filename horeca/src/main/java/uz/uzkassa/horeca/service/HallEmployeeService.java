package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.horeca.domain.hall.HallEmployee;
import uz.uzkassa.horeca.dto.hallemployee.HallEmployeeBatchDTO;
import uz.uzkassa.horeca.dto.hallemployee.HallEmployeeListDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.HallEmployeeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class HallEmployeeService extends BaseService {

    HallEmployeeRepository hallEmployeeRepository;

    private void validate(HallEmployeeBatchDTO hallEmployeeBatchDTO) {
        if (hallEmployeeBatchDTO.getHallId() == null) {
            throw badRequestExceptionThrow("Hall is required").get();
        }
        if (CollectionUtils.isEmpty(hallEmployeeBatchDTO.getEmployeeIds())) {
            throw badRequestExceptionThrow("Employees are required").get();
        }
    }

    public void createOrUpdateBatch(HallEmployeeBatchDTO hallEmployeeBatchDTO) {
        this.validate(hallEmployeeBatchDTO);
        List<Long> hallEmployeeList = hallEmployeeRepository.findByHallIdAndDeletedIsFalse(hallEmployeeBatchDTO.getHallId());
        List<HallEmployee> hallEmployees = new ArrayList<>();
        for (Long employeeId : hallEmployeeBatchDTO.getEmployeeIds()) {
            if (!hallEmployeeList.contains(employeeId)) {
                HallEmployee hallEmployee = new HallEmployee();
                hallEmployee.setHallId(hallEmployeeBatchDTO.getHallId());
                hallEmployee.setEmployeeId(employeeId);
                hallEmployees.add(hallEmployee);
            }
        }
        hallEmployeeRepository.saveAll(hallEmployees);
    }

    public List<HallEmployeeListDTO> getList(BaseFilter filter) {
        return hallEmployeeRepository.getResultList(filter)
                .getList()
                .stream()
                .map(hallEmployee -> {
                    HallEmployeeListDTO hallEmployeeListDTO = new HallEmployeeListDTO();
                    hallEmployeeListDTO.setId(hallEmployee.getId());
                    hallEmployeeListDTO.setHall(hallEmployee.getHall().toCommonDTO());
                    hallEmployeeListDTO.setEmployee(hallEmployee.getEmployee().toCommonDTO());
                    hallEmployeeListDTO.setRole(hallEmployee.getEmployee().getRole());
                    return hallEmployeeListDTO;
                }).collect(Collectors.toList());
    }

    public void delete(Long id) {
        hallEmployeeRepository.deleteById(id);
    }
}
