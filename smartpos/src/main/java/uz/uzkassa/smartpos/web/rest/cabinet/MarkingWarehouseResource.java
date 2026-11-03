package uz.uzkassa.smartpos.web.rest.cabinet;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.dto.warehouse.*;
import uz.uzkassa.smartpos.filter.MarkingIncomeFilter;
import uz.uzkassa.smartpos.service.warehouse.MarkingWarehouseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouse/marking")
public class MarkingWarehouseResource {
    private final MarkingWarehouseService markingWarehouseService;

    @PostMapping("/check")
    public MarkingWarehouseCheckResponseDto check(@Valid @RequestBody MarkingWarehouseCheckDto dto) {
        return markingWarehouseService.checkForGiving(dto);
    }

    @PostMapping
    public void create(@Valid @RequestBody MarkingIncomeCreateDto dto) {
        markingWarehouseService.create(dto);
    }

    @PutMapping
    public void updateStatus(@Valid @RequestBody MarkingDetailUpdateStatusDto dto) {
        markingWarehouseService.updateStatus(dto, true, true, true);
    }

    @GetMapping
    public Page<MarkingIncomeListDto> findAllByFilter(@ParameterObject MarkingIncomeFilter filter) {
        return markingWarehouseService.findAllByFilter(filter);
    }

    @GetMapping("/{id}")
    public MarkingIncomeDto findById(@PathVariable Long id) {
        return markingWarehouseService.findById(id);
    }

    @GetMapping("/report")
    public Page<MarkingStatusReportDto> markingsReport(@ParameterObject MarkingIncomeFilter filter) {
        return markingWarehouseService.markingsReport(filter);
    }

    @GetMapping("/report/excel")
    public ByteArrayResource markingsReportExcel(@ParameterObject MarkingIncomeFilter filter) {
        return markingWarehouseService.exportReport(filter);
    }
}
