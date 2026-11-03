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
import uz.uzkassa.cts.dto.serviceorderinvoice.admin.ServiceOrderInvoiceListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.ServiceOrderInvoiceFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.ServiceOrderInvoiceService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminServiceOrderInvoiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminServiceOrderInvoiceResource {

    ServiceOrderInvoiceService serviceOrderInvoiceService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<ServiceOrderInvoiceListAdminDTO>> getList(@ParameterObject ServiceOrderInvoiceFilter filter) {
        return ResponseEntity.ok(serviceOrderInvoiceService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject ServiceOrderInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.SERVICE_ORDER_INVOICE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject ServiceOrderInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.SERVICE_ORDER_INVOICE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
