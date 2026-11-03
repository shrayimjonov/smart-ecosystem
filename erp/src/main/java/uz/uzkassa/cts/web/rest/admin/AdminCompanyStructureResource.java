package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.companystructure.company.CompanyStructureCompanyDTO;
import uz.uzkassa.cts.dto.companystructure.department.CompanyStructureDepartmentDTO;
import uz.uzkassa.cts.dto.companystructure.position.CompanyStructurePositionDTO;
import uz.uzkassa.cts.dto.companystructure.supervisor.CompanyStructureSupervisorDTO;
import uz.uzkassa.cts.service.CompanyStructureService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminCompanyStructureRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCompanyStructureResource {

    CompanyStructureService companyStructureService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<CompanyStructureCompanyDTO> getByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(companyStructureService.getByCompany(companyId));
    }

    @GetMapping("/position/{companyId}")
    public ResponseEntity<CompanyStructurePositionDTO> getByPosition(@PathVariable Long companyId) {
        return ResponseEntity.ok(companyStructureService.getByPosition(companyId));
    }

    @GetMapping("/department/{companyId}")
    public ResponseEntity<CompanyStructureDepartmentDTO> getByDepartment(@PathVariable Long companyId) {
        return ResponseEntity.ok(companyStructureService.getByDepartment(companyId));
    }

    @GetMapping("/supervisor/{companyId}")
    public ResponseEntity<CompanyStructureSupervisorDTO> getBySupervisor(@PathVariable Long companyId) {
        return ResponseEntity.ok(companyStructureService.getBySupervisor(companyId));
    }
}
