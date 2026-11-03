package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.salaryAllocation.SalaryAllocationDTO;
import uz.uzkassa.cts.dto.salaryAllocation.SalaryAllocationDetailDTO;
import uz.uzkassa.cts.dto.salaryAllocation.SalaryAllocationListDTO;
import uz.uzkassa.cts.filter.SalaryAllocationFilter;
import uz.uzkassa.cts.service.SalaryAllocationService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminSalaryAllocationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminSalaryAllocationResource {

    SalaryAllocationService salaryAllocationService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody SalaryAllocationDTO salaryAllocationDTO) {
        return ResponseEntity.ok(salaryAllocationService.create(salaryAllocationDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody SalaryAllocationDTO salaryAllocationDTO) {
        return ResponseEntity.ok(salaryAllocationService.update(id, salaryAllocationDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<SalaryAllocationDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(salaryAllocationService.get(id));
    }

    @GetMapping()
    public ResponseEntity<Page<SalaryAllocationListDTO>> getList(@ParameterObject SalaryAllocationFilter filter) {
        return ResponseEntity.ok(salaryAllocationService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        salaryAllocationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}
