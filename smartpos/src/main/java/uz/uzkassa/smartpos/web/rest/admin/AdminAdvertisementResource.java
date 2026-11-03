package uz.uzkassa.smartpos.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementAdminDto;
import uz.uzkassa.smartpos.dto.advertisement.AdvertisementCreateDto;
import uz.uzkassa.smartpos.dto.base.PageDto;
import uz.uzkassa.smartpos.filter.AdvertisementFilter;
import uz.uzkassa.smartpos.service.AdvertisementService;

@RestController
@RequestMapping("/api/v1/admin/ads")
@RequiredArgsConstructor
public class AdminAdvertisementResource {
    private final AdvertisementService service;

    @PostMapping
    public Long create(@Valid @RequestBody AdvertisementCreateDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @Valid @RequestBody AdvertisementCreateDto dto) {
        service.update(id, dto);
    }

    @GetMapping
    public PageDto<AdvertisementAdminDto> findAllByFilter(@ParameterObject AdvertisementFilter filter) {
        return service.findAllByFilter(filter);
    }

    @GetMapping("/{id}")
    public AdvertisementAdminDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
