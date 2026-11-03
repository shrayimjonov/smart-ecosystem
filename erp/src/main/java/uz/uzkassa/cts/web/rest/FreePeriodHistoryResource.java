package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.freeperiodhistory.response.FreePeriodHistoryResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.FreePeriodHistoryService;

@RestController
@RequestMapping("/api/free-period-history")
@Tag(name = "Device")
@Slf4j
public class FreePeriodHistoryResource {

    private final FreePeriodHistoryService service;

    public FreePeriodHistoryResource(FreePeriodHistoryService service) {
        this.service = service;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<FreePeriodHistoryResponseDto>> getAll(BaseFilter filter) {
        log.debug("REST request to get all DeviceListDTO : {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }
}
