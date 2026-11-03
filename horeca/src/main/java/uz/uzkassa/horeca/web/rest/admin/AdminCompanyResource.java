package uz.uzkassa.horeca.web.rest.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.company.CompanyCommonDTO;
import uz.uzkassa.horeca.dto.company.CompanyDTO;
import uz.uzkassa.horeca.dto.company.admin.CompanyDetailAdminDTO;
import uz.uzkassa.horeca.dto.company.admin.CompanyListAdminDTO;
import uz.uzkassa.horeca.dto.sync.CompanySyncDTO;
import uz.uzkassa.horeca.enums.CommonStatus;
import uz.uzkassa.horeca.filters.CompanyFilter;
import uz.uzkassa.horeca.service.CompanyService;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.adminCompanyRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCompanyResource {

    CompanyService companyService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CompanyDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getForAdmin(id));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.update(id, companyDTO));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyCommonDTO>> lookUp(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(companyService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        companyService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<CompanyListAdminDTO>> getList(@ParameterObject CompanyFilter filter) {
        return ResponseEntity.ok(companyService.getListForAdmin(filter));
    }

    @PostMapping(ApiConstants.sync)
    public ResponseEntity<Long> sync(@RequestBody CompanySyncDTO companySyncDTO) {
        return ResponseEntity.ok(companyService.sync(companySyncDTO));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<CommonStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(CommonStatus.class));
    }
}
