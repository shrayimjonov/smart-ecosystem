package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.employeetimeslot.EmployeeTimeslotDTO;
import uz.uzkassa.cts.dto.timeslot.TimeslotDTO;

public interface EmployeeTimeslotService extends BaseService<User, Long> {

    void create(EmployeeTimeslotDTO employeeTimeslotDTO);

    TimeslotDTO get(Long employeeId);

    void delete(Long employeeId);

}
