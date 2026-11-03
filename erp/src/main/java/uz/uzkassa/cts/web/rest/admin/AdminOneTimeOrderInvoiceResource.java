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
import uz.uzkassa.cts.dto.onetimeorderinvoice.admin.OneTimeOrderInvoiceListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.OneTimeOrderInvoiceFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.OneTimeOrderInvoiceService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminOneTimeOrderInvoiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminOneTimeOrderInvoiceResource {

    OneTimeOrderInvoiceService oneTimeOrderInvoiceService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<OneTimeOrderInvoiceListAdminDTO>> getList(@ParameterObject OneTimeOrderInvoiceFilter filter) {
        return ResponseEntity.ok(oneTimeOrderInvoiceService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject OneTimeOrderInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.ONE_TIME_ORDER_INVOICE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject OneTimeOrderInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.ONE_TIME_ORDER_INVOICE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
