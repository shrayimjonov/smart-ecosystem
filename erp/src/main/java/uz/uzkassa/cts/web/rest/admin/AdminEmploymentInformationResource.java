package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.employmentInformation.EmploymentInformationDTO;
import uz.uzkassa.cts.dto.employmentInformation.EmploymentInformationDetailDTO;
import uz.uzkassa.cts.enums.EmploymentType;
import uz.uzkassa.cts.service.EmploymentInformationService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminEmploymentInformationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminEmploymentInformationResource {

    EmploymentInformationService employmentInformationService;

    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<Long> create(@PathVariable Long employeeId, @RequestBody EmploymentInformationDTO employmentInformationDTO) {
        return ResponseEntity.ok(employmentInformationService.create(employeeId, employmentInformationDTO));
    }

    @PutMapping("/employee/{employeeId}")
    public ResponseEntity<Long> update(@PathVariable Long employeeId, @RequestBody EmploymentInformationDTO employmentInformationDTO) {
        return ResponseEntity.ok(employmentInformationService.update(employeeId, employmentInformationDTO));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<EmploymentInformationDetailDTO> get(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employmentInformationService.get(employeeId));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<EmploymentType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(EmploymentType.class));
    }
}
