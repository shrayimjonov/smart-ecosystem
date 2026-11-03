package uz.uzkassa.horeca.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import uz.uzkassa.horeca.dto.employee.EmployeeDTO;
import uz.uzkassa.horeca.dto.employee.admin.EmployeeAdminDTO;
import uz.uzkassa.horeca.dto.employee.cabinet.EmployeeCabinetDTO;

@Component
@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper extends CabinetMapper<EmployeeDTO, EmployeeCabinetDTO>, AdminMapper<EmployeeDTO, EmployeeAdminDTO> {
}
