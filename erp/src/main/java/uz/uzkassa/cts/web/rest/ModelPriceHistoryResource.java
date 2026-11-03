package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.modelpricehistory.ModelPriceHistoryDetailDTO;
import uz.uzkassa.cts.dto.modelpricehistory.request.ModelPriceHistoryDto;
import uz.uzkassa.cts.dto.modelpricehistory.response.ModelPriceHistoryResponseDto;
import uz.uzkassa.cts.filter.ModelPriceHistoryFilter;
import uz.uzkassa.cts.service.ModelPriceHistoryService;

@RestController
@RequestMapping("/api/modelPriceHistory")
@Tag(name = "Model Price History")
@Slf4j
public class ModelPriceHistoryResource {

    private final ModelPriceHistoryService service;

    public ModelPriceHistoryResource(ModelPriceHistoryService service) {
        this.service = service;
    }

    @Operation(summary = "Add")
    @PostMapping
    public ResponseEntity<ModelPriceHistoryResponseDto> create(@Valid @RequestBody ModelPriceHistoryDto modelPriceHistoryDto) {
        log.debug("REST request to create new ModelPriceHistoryDto: {}", modelPriceHistoryDto);
        return ResponseEntity.ok(service.create(modelPriceHistoryDto));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<ModelPriceHistoryResponseDto> update(@Valid @RequestBody ModelPriceHistoryDto modelPriceHistoryDto) {
        log.debug("REST request to create new ModelPriceHistoryDto: {}", modelPriceHistoryDto);
        return ResponseEntity.ok(service.update(modelPriceHistoryDto));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<ModelPriceHistoryResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to get ModelPriceHistoryDto id: {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "List")
    @GetMapping("/list")
    public ResponseEntity<Page<ModelPriceHistoryResponseDto>> getAll(ModelPriceHistoryFilter filter) {
        log.debug("REST request to get all ModelPriceHistoryDto filter : {}", filter);
        return ResponseEntity.ok(service.list(filter));
    }

    @Operation(summary = "Delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.debug("REST request to delete ModelPriceHistoryDto id: {}", id);
        service.customDeleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Detail")
    @GetMapping(value = "/detail")
    public ResponseEntity<ModelPriceHistoryDetailDTO> detail() {
        return ResponseEntity.ok(service.getDetail());
    }
}
