package uz.uzkassa.silen.rest.integration._1c;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.StoreFilter;
import uz.uzkassa.silen.dto.filter.WaybillFilter;
import uz.uzkassa.silen.dto.marking.BatchAggregationDTO;
import uz.uzkassa.silen.dto.marking.BatchAggregationResponseDTO;
import uz.uzkassa.silen.enumeration.StoreStatus;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.integration.onec.CheckBatchStatusDTO;
import uz.uzkassa.silen.integration.onec.Order1CDTO;
import uz.uzkassa.silen.rest.integration._1c.dto.Waybill1CDTO;
import uz.uzkassa.silen.service.*;

import java.util.List;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Order}.
 */
@Slf4j
@RestController
@RequestMapping("/api/1c")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Order1CResource {
    Order1CService order1CService;
    StoreService storeService;
    WaybillService waybillService;
    BatchService batchService;
    ExportReportUploadService exportReportUploadService;

    @PostMapping("/order")
    public ResponseEntity<Order1CDTO> create(@RequestBody @Valid Order1CDTO order1CDTO) {
        log.info("1C Order JSON :{}", order1CDTO);
        return ResponseEntity.ok(order1CService.save(order1CDTO));
    }

    @GetMapping("/order/warehouse")
    public ResponseEntity<List<SelectItem>> getItems() {
        log.debug("REST request to get all Store");
        StoreFilter filter = new StoreFilter();
        filter.setStatus(StoreStatus.ACTIVE);
        return ResponseEntity.ok(storeService.getItems(filter));
    }

    @GetMapping("/waybill")
    public ResponseEntity<Page<Waybill1CDTO>> getAllForOneC(@ModelAttribute final WaybillFilter filter) {
        if (filter.getSize() > 50) {
            throw new BadRequestException("Size must be less than or equal to 50");
        }
        return ResponseEntity.ok(waybillService.findAllByFilterFor1C(filter));
    }

    @PostMapping("/aggregation/batch")
    public ResponseEntity<BatchAggregationResponseDTO> createBatch(@RequestBody @Valid BatchAggregationDTO batchAggregationDTO) {
        log.info("Resizing batch aggregation {}", batchAggregationDTO);
        if (batchAggregationDTO.getQuantity() > 30_000) {
            throw new BadRequestException("Допустимое значение для заказ КА от 1 до 30 000");
        }
        batchAggregationDTO.setExportToFile(true);
        return ResponseEntity.ok(batchService.save(batchAggregationDTO));
    }

    @GetMapping("/check/batch/{id}")
    public ResponseEntity<CheckBatchStatusDTO> getAllForOneC(@PathVariable final String id) {
        return ResponseEntity.ok(batchService.checkStatus(id));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Void> download(@PathVariable Long id, HttpServletResponse response) {
         exportReportUploadService.download1c(id, response);
         return ResponseEntity.ok().build();
    }
}
