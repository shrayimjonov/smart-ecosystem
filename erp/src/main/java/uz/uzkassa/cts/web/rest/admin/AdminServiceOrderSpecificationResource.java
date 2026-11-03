package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.orderspecification.ServiceOrderSpecificationListDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.ServiceOrderSpecificationService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminServiceOrderSpecificationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminServiceOrderSpecificationResource {

    ServiceOrderSpecificationService serviceOrderSpecificationService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<ServiceOrderSpecificationListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(serviceOrderSpecificationService.getList(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.SERVICE_ORDER_SPECIFICATION, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.SERVICE_ORDER_SPECIFICATION, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
