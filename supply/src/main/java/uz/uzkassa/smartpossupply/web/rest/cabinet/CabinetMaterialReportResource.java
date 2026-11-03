package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.materialreport.MaterialReportDTO;
import uz.uzkassa.smartpossupply.enums.FileType;
import uz.uzkassa.smartpossupply.filters.MaterialReportFilter;
import uz.uzkassa.smartpossupply.service.MaterialReportService;
import uz.uzkassa.smartpossupply.service.handlers.excel.MaterialReportStrategy;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created time: 08.05.2025 16:43
 */

@RestController
@RequestMapping(ApiConstants.cabinetMaterialReportRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetMaterialReportResource {

    MaterialReportService materialReportService;
    MaterialReportStrategy materialReportStrategy;

    @GetMapping
    public ResponseEntity<List<MaterialReportDTO>> getReport(@ParameterObject MaterialReportFilter filter) {
        return ResponseEntity.ok(materialReportService.getReport(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportExcel(@ParameterObject MaterialReportFilter filter) {
        materialReportStrategy.generate(filter, FileType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.sync)
    public ResponseEntity<Void> sync() {
        materialReportService.sync();
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.resync)
    public ResponseEntity<Void> resync() {
        materialReportService.resync();
        return ResponseEntity.ok().build();
    }
}
