package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.devicesuspendhistory.response.DeviceSuspendHistoryResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.DeviceSuspendHistoryFilter;
import uz.uzkassa.cts.service.DeviceSuspendHistoryOldService;

@RestController
@RequestMapping("/api/device-suspend-history")
@Tag(name = "Customer Branch Devices suspend")
@Slf4j
public class DeviceSuspendHistoryResource {

    private final DeviceSuspendHistoryOldService service;

    public DeviceSuspendHistoryResource(DeviceSuspendHistoryOldService service) {
        this.service = service;
    }

    @Operation(summary ="Get by ID")
    @GetMapping("/contract/{contractId}")
    public ResponseEntity<Page<DeviceSuspendHistoryResponseDto>> getByContractId(@PathVariable Long contractId, BaseFilter filter) {
        log.debug("REST request to get by id: {}", contractId);
        Page<DeviceSuspendHistoryResponseDto> result = service.getByContractId(contractId, filter);
        return ResponseEntity.ok(result);
    }


    @Operation(summary ="Get by ID")
    @GetMapping("/list")
    public ResponseEntity<Page<DeviceSuspendHistoryResponseDto>> getList(DeviceSuspendHistoryFilter filter) {
        Page<DeviceSuspendHistoryResponseDto> result = service.getList(filter);
        return ResponseEntity.ok(result);
    }

    @Operation(summary ="get info")
    @GetMapping("/download")
    public ResponseEntity<byte[]> download(DeviceSuspendHistoryFilter filter) {
        log.debug("card payment request filter: {}", filter);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"suspend_history.xlsx\"")
            .body(service.download(filter));
    }
}
