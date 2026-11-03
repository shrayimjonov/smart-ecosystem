package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.device.HistoryListDTO;
import uz.uzkassa.cts.filter.HistoryListFilter;
import uz.uzkassa.cts.service.HistoryService;


@RestController
@RequestMapping("/api/histories")
@Tag(name = "History")
@Slf4j
public class HistoryResource {

    private final HistoryService historyService;

    public HistoryResource(HistoryService historyService) {
        this.historyService = historyService;
    }

    @Operation(summary ="List")
    @GetMapping
    public ResponseEntity<Page<HistoryListDTO>> list(HistoryListFilter filter) {
        return ResponseEntity.ok(historyService.list(filter));
    }
}
