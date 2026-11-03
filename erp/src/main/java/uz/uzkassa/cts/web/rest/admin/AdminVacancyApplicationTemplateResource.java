package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.VacancyApplicationTemplateDTO;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.admin.VacancyApplicationTemplateDetailAdminDTO;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.admin.VacancyApplicationTemplateListAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.VacancyApplicationTemplateFilter;
import uz.uzkassa.cts.service.VacancyApplicationTemplateService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminVacancyApplicationTemplateRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminVacancyApplicationTemplateResource {

    VacancyApplicationTemplateService vacancyApplicationTemplateService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody VacancyApplicationTemplateDTO vacancyApplicationTemplateDTO) {
        return ResponseEntity.ok(vacancyApplicationTemplateService.create(vacancyApplicationTemplateDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody VacancyApplicationTemplateDTO vacancyApplicationTemplateDTO) {
        return ResponseEntity.ok(vacancyApplicationTemplateService.update(id, vacancyApplicationTemplateDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<VacancyApplicationTemplateDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(vacancyApplicationTemplateService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<VacancyApplicationTemplateListAdminDTO>> getList(@ParameterObject VacancyApplicationTemplateFilter filter) {
        return ResponseEntity.ok(vacancyApplicationTemplateService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject VacancyApplicationTemplateFilter filter) {
        return ResponseEntity.ok(vacancyApplicationTemplateService.lookUp(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vacancyApplicationTemplateService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
