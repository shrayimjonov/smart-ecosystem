package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.company.CompanyCommonDTO;
import uz.uzkassa.cts.dto.company.admin.CompanyDetailAdminDTO;
import uz.uzkassa.cts.dto.company.admin.CompanyListAdminDTO;
import uz.uzkassa.cts.dto.company.base.CompanyCheckDTO;
import uz.uzkassa.cts.dto.company.base.CompanyDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.CompanyService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminCompanyRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCompanyResource {

    CompanyService companyService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.create(companyDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyService.update(id, companyDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<CompanyDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<CompanyListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(companyService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CompanyCommonDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(companyService.lookUp(filter));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        companyService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.check + "/{tin}")
    public ResponseEntity<CompanyCheckDTO> check(@PathVariable String tin) {
        return ResponseEntity.ok(companyService.check(tin));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        companyService.deleteById(id, true);
        return ResponseEntity.ok().build();
    }
}
