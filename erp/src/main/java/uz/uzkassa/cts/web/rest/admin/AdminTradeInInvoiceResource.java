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
import uz.uzkassa.cts.dto.tradeininvoice.admin.TradeInInvoiceListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.TradeInInvoiceFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.TradeInInvoiceService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminTradeInInvoiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminTradeInInvoiceResource {

    TradeInInvoiceService tradeInInvoiceService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<TradeInInvoiceListAdminDTO>> getList(@ParameterObject TradeInInvoiceFilter filter) {
        return ResponseEntity.ok(tradeInInvoiceService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject TradeInInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.TRADE_IN_INVOICE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject TradeInInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.TRADE_IN_INVOICE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
