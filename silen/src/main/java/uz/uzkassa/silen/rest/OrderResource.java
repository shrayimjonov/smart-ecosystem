package uz.uzkassa.silen.rest;

import com.opencsv.exceptions.CsvValidationException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.OrderFilter;
import uz.uzkassa.silen.dto.marking.OrderDTO;
import uz.uzkassa.silen.dto.marking.OrderProductDTO;
import uz.uzkassa.silen.dto.marking.OrderProductStatsDTO;
import uz.uzkassa.silen.enumeration.*;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.service.ExportReportService;
import uz.uzkassa.silen.service.OrderService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Order}.
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderResource {
    OrderService orderService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid OrderDTO orderDTO) {
        orderService.save(orderDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderProductDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping("/serial/number/types")
    public ResponseEntity<List<SelectItem>> getAllSerialNumberTypes() {
        return ResponseEntity.ok(SerialNumberType.getAll());
    }

    @GetMapping("/check-status")
    public ResponseEntity checkStatus(@RequestParam String orderProductId) {
        orderService.checkStatus(orderProductId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<SelectItem>> getStatuses() {
        return ResponseEntity.ok(BufferStatus.getAll());
    }

    @GetMapping("/release-method-types")
    public ResponseEntity<List<SelectItem>> getAllReleaseMethodTypes() {
        return ResponseEntity.ok(ReleaseMethodType.getAll());
    }

    @GetMapping("/payment/types")
    public ResponseEntity<List<SelectItem>> getAllPaymentTypes() {
        return ResponseEntity.ok(RateType.getAll());
    }

    @GetMapping("/cis/types")
    public ResponseEntity<List<SelectItem>> getCisTypes() {
        return ResponseEntity.ok(Arrays.asList(CisType.UNIT.toSelectItem(), CisType.GROUP.toSelectItem()));
    }

    @GetMapping
    public ResponseEntity<Page<OrderProductDTO>> getAll(@ModelAttribute OrderFilter filter) {
        return ResponseEntity.ok(orderService.findAll(filter));
    }

    @GetMapping("/report")
    public ResponseEntity<Collection<OrderProductStatsDTO>> generateReport(@ModelAttribute OrderFilter filter) {
        if (filter.getFrom() == null || filter.getTo() == null) {
            throw new BadRequestException("Нужный период не выбрано");
        }
        filter.setStatus(BufferStatus.CLOSED);
        return ResponseEntity.ok(orderService.generateReport(filter));
    }

    @PostMapping("/report/export")
    public ResponseEntity<Void> exportReport(@RequestBody OrderFilter filter) {
        if (filter.getParties() == null || filter.getParties().isEmpty()) {
            throw new BadRequestException("Нужный партии не выбрано");
        }
        exportReportService.export(filter, ExportReportType.CSV_UNUSED_MAKRS, FileType.CSV, "UnusedMarks");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items")
    public ResponseEntity<List<SelectItem>> getItems(@ModelAttribute OrderFilter filter) {
        return ResponseEntity.ok(orderService.getItems(filter));
    }

    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Set<String>> readFromFile(@RequestParam("file") MultipartFile file) throws IOException, CsvValidationException {
        return ResponseEntity.ok(orderService.readFromFile(file));
    }
}
