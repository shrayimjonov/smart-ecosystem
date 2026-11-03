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
import uz.uzkassa.cts.dto.actoftransmissioninvoice.admin.ActOfTransmissionInvoiceListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.PaymentScheduleInvoiceFilter;
import uz.uzkassa.cts.service.ActOfTransmissionInvoiceService;
import uz.uzkassa.cts.service.ExportReportService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminActOfTransmissionInvoiceRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminActOfTransmissionInvoiceResource {

    ActOfTransmissionInvoiceService actOfTransmissionInvoiceService;
    ExportReportService exportReportService;

    @GetMapping
    public ResponseEntity<Page<ActOfTransmissionInvoiceListAdminDTO>> getList(@ParameterObject PaymentScheduleInvoiceFilter filter) {
        return ResponseEntity.ok(actOfTransmissionInvoiceService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject PaymentScheduleInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.ACT_OF_TRANSMISSION_INVOICE, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCsv(@ParameterObject PaymentScheduleInvoiceFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.ACT_OF_TRANSMISSION_INVOICE, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
