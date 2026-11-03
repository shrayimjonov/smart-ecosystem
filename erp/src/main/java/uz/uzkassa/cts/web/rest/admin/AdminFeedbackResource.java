package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.feedback.admin.FeedbackDetailAdminDTO;
import uz.uzkassa.cts.dto.feedback.admin.FeedbackListAdminDTO;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.enums.FeedBackStatus;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.FeedbackFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.FeedbackService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminFeedbackRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminFeedbackResource {

    FeedbackService feedbackService;
    ExportReportService exportReportService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<FeedbackDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getForAdmin(id));
    }

    @GetMapping
    public ResponseEntity<Page<FeedbackListAdminDTO>> getList(@ParameterObject FeedbackFilter filter) {
        return ResponseEntity.ok(feedbackService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.exportExcel)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.FEEDBACK, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.FEEDBACK, ContentType.CSV);
        return ResponseEntity.ok().build();
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") FeedBackStatus status) {
        feedbackService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
