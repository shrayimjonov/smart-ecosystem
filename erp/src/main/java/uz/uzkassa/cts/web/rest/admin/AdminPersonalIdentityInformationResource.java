package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.personalidentityinformation.PersonalIdentityInformationDTO;
import uz.uzkassa.cts.enums.DriverLicenseCategory;
import uz.uzkassa.cts.enums.PersonalIdentityType;
import uz.uzkassa.cts.service.PersonalIdentityInformationService;

import java.util.EnumSet;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminPersonalIdentityInformationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminPersonalIdentityInformationResource {

    PersonalIdentityInformationService personalIdentityInformationService;

    @PostMapping("/employee/{employeeId}")
    public ResponseEntity<Long> create(@PathVariable Long employeeId, @RequestBody PersonalIdentityInformationDTO personalIdentityInformationDTO) {
        return ResponseEntity.ok(personalIdentityInformationService.create(employeeId, personalIdentityInformationDTO));
    }

    @PutMapping("/employee/{employeeId}")
    public ResponseEntity<Long> update(@PathVariable Long employeeId, @RequestBody PersonalIdentityInformationDTO personalIdentityInformationDTO) {
        return ResponseEntity.ok(personalIdentityInformationService.update(employeeId, personalIdentityInformationDTO));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PersonalIdentityInformationDTO>> getList(@PathVariable Long employeeId) {
        return ResponseEntity.ok(personalIdentityInformationService.getList(employeeId));
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<PersonalIdentityType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(PersonalIdentityType.class));
    }

    @GetMapping("/employee/driver-license-categories")
    public ResponseEntity<EnumSet<DriverLicenseCategory>> getDriverLicenseCategory() {
        return ResponseEntity.ok(EnumSet.allOf(DriverLicenseCategory.class));
    }
}
