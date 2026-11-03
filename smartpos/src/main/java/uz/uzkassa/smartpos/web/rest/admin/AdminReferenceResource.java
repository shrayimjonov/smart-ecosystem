package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.domain.postgres.ReferenceAdminDto;
import uz.uzkassa.smartpos.domain.postgres.ReferenceCreateDto;
import uz.uzkassa.smartpos.filter.ReferenceFilter;
import uz.uzkassa.smartpos.service.ReferenceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/references")
public class AdminReferenceResource {
    private final ReferenceService referenceService;

    @PostMapping
    public Long create(@Valid @RequestBody ReferenceCreateDto referenceCreateDto) {
        return referenceService.create(referenceCreateDto);
    }

    @PutMapping("/{id}")
    public void update(@Valid @RequestBody ReferenceCreateDto referenceCreateDto, @PathVariable Long id) {
        referenceService.update(referenceCreateDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        referenceService.delete(id);
    }

    @GetMapping
    public Page<ReferenceAdminDto> findAllByFilter(@ParameterObject ReferenceFilter filter) {
        return referenceService.findAll(filter);
    }
}
