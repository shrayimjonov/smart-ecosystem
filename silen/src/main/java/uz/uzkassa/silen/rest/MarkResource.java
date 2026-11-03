package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.MongoFilter;
import uz.uzkassa.silen.dto.mongo.MarkDTO;
import uz.uzkassa.silen.enumeration.MarkStatus;
import uz.uzkassa.silen.service.MarkService;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.mongo.Mark}.
 */
@RestController
@RequestMapping("/api/mark")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MarkResource {
    MarkService markService;
//    CsvMarkExportHandler csvHandler;

    @GetMapping
    public ResponseEntity<Page<MarkDTO>> getAll(@ModelAttribute MongoFilter filter) {
        return ResponseEntity.ok(markService.findAll(filter));
    }

    @GetMapping("/party/marks")
    public ResponseEntity<List<MarkDTO>> partyMarks(@ModelAttribute MongoFilter filter) {
        filter.setPage(0);
        filter.setSize(100_000);
        return ResponseEntity.ok(markService.findAll(filter).getContent());
    }

//    @GetMapping(value = "/export_csv_old")
//    public void generateCsv(@ModelAttribute MongoFilter filter, HttpServletResponse response) throws IOException {
//        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition");
//        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Marks.csv\"");
//        response.setContentType("text/csv");
//        csvHandler.writeToStream(filter, response.getOutputStream());
//    }

    @GetMapping(value = "/export")
    public ResponseEntity<Void> getMarksCsv(@ModelAttribute final MongoFilter filter) {
        markService.generateReport(filter);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/statuses")
    public ResponseEntity<List<SelectItem>> getMarkStatuses() {
        return ResponseEntity.ok(MarkStatus.getAll());
    }
}
