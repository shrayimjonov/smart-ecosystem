package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.ctsservice.response.ServiceResponseDto;
import uz.uzkassa.cts.dto.tariff.TariffTotalDTO;
import uz.uzkassa.cts.dto.tariff.request.TariffRequestDto;
import uz.uzkassa.cts.dto.tariff.request.TariffServiceDto;
import uz.uzkassa.cts.dto.tariff.response.TariffServiceResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeReportFilter;
import uz.uzkassa.cts.service.TariffService;

import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
@Tag(name = "Tariff")
@Slf4j
public class TariffResource {

    private final TariffService tariffService;

    @Autowired
    public TariffResource(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @Operation(summary ="Add")
    @PostMapping
    public ResponseEntity<TariffServiceResponseDto> create(@Valid @RequestBody TariffRequestDto tariffDto) {
        log.debug("REST request to create TariffDto: {}", tariffDto);
        return ResponseEntity.ok(tariffService.create(tariffDto));
    }

    @Operation(summary = "Update")
    @PutMapping
    public ResponseEntity<TariffRequestDto> update(@Valid @RequestBody TariffRequestDto tariffDto) {
        log.debug("REST request to update TariffDto: {}", tariffDto);
        return ResponseEntity.ok(tariffService.update(tariffDto));
    }

    @Operation(summary = "Get")
    @GetMapping("/{id}")
    public ResponseEntity<TariffServiceResponseDto> getById(@PathVariable Long id) {
        log.debug("REST request to update TariffDto: {}", id);
        return ResponseEntity.ok(tariffService.get(id));
    }

    @Operation(summary = "Items")
    @GetMapping("/items")
    public ResponseEntity<List<CommonCodeDTO>> getItems() {
        log.debug("REST request to get Tariffs as drop down");
        return ResponseEntity.ok(tariffService.getItems());
    }


    @Operation(summary = "List")
    @GetMapping("/list")
    public ResponseEntity<Page<TariffServiceResponseDto>> list(@Valid BaseFilter filter) {
        log.debug("REST request to get tariffs list: {}", filter.getOrderBy());
        return ResponseEntity.ok(tariffService.list(filter));
    }

    @Operation(summary = "List By Employee")
    @GetMapping("/by-employee")
    public ResponseEntity<List<TariffTotalDTO>> listByEmployee(EmployeeReportFilter filter) {
        log.debug("REST request to get tariffs by employee : {}", filter);
        return ResponseEntity.ok(tariffService.listByEmployee(filter));
    }

    @Operation(summary ="Delete")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete region : {}", id);
        tariffService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary ="Get Tariff Services")
    @GetMapping("/{id}/services")
    public ResponseEntity<List<ServiceResponseDto>> getTariffServices(@PathVariable Long id) {
        log.debug("REST request to delete region : {}", id);
        return ResponseEntity.ok(tariffService.getTariffServices(id));
    }

    @Operation(summary ="Add Services to Tariff")
    @PostMapping("/services/add")
    public ResponseEntity<TariffServiceResponseDto> addTariffServices(@Valid @RequestBody TariffServiceDto dto) {
        log.debug("REST request attach Service to Tariff : {}", dto);
        return ResponseEntity.ok(tariffService.addService(dto));
    }

    @Operation(summary ="Remove Services from Tariff")
    @PostMapping("/services/remove")
    public ResponseEntity<TariffServiceResponseDto> removeTariffServices(@Valid @RequestBody TariffServiceDto dto) {
        log.debug("REST request to delete service from tariff : {}", dto);
        return ResponseEntity.ok(tariffService.removeService(dto));
    }


}
