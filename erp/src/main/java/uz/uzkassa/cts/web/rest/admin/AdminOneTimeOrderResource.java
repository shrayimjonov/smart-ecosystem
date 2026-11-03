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
import uz.uzkassa.cts.dto.onetimeorder.OneTimeOrderListDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.OneTimeOrderFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.OneTimeOrderService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminOneTimeOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOneTimeOrderResource {

    OneTimeOrderService oneTimeOrderService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<OneTimeOrderListDTO>> getList(@ParameterObject OneTimeOrderFilter filter) {
        return ResponseEntity.ok(oneTimeOrderService.getList(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject OneTimeOrderFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.ONE_TIME_ORDER, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject OneTimeOrderFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.ONE_TIME_ORDER, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
