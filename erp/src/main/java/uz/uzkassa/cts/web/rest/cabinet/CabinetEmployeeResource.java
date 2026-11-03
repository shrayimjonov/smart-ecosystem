package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.employee.base.EmployeeDTO;
import uz.uzkassa.cts.dto.employee.cabinet.EmployeeDetailCabinetDTO;
import uz.uzkassa.cts.dto.employee.cabinet.EmployeeListCabinetDTO;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.service.EmployeeService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.cabinetEmployeeRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetEmployeeResource {

    EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.create(employeeDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO) {
        return ResponseEntity.ok(employeeService.update(id, employeeDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<EmployeeDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getForCabinet(id));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeListCabinetDTO>> getList(@ParameterObject EmployeeFilter filter) {
        return ResponseEntity.ok(employeeService.getListForCabinet(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject EmployeeFilter filter) {
        return ResponseEntity.ok(employeeService.lookUp(filter));
    }
}
