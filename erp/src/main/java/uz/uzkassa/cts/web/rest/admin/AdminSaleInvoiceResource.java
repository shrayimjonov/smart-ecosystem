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
import uz.uzkassa.cts.dto.sale.invoice.admin.SaleInvoiceListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.SaleInvoiceService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminSaleInvoiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminSaleInvoiceResource {

    SaleInvoiceService saleInvoiceService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<SaleInvoiceListAdminDTO>> getList(@ParameterObject TradeInInvoiceFilter filter) {
        return ResponseEntity.ok(saleInvoiceService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject TradeInInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.SALE_INVOICE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject TradeInInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.SALE_INVOICE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
