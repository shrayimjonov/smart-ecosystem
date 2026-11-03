package uz.uzkassa.cts.web.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.service.DeviceModelService;

import java.util.List;


@RestController
@RequestMapping("/api/models")
@Tag(name = "Models")
@Slf4j
public class DeviceModelResource {

    private final DeviceModelService deviceModelService;

    public DeviceModelResource(DeviceModelService deviceModelService) {
        this.deviceModelService = deviceModelService;
    }

    @Operation(summary ="Get")
    @GetMapping("/{id}")
    public ResponseEntity<CommonCodeDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get DeviceModelDetailDTO: {}", id);
        return ResponseEntity.ok(deviceModelService.get(id));
    }

    @Operation(summary ="Items")
    @GetMapping("/items")
    public ResponseEntity<List<CommonCodeDTO>> getItems() {
        log.debug("REST request to get device model items: {}", new CommonCodeDTO());
        return ResponseEntity.ok(deviceModelService.getItems());
    }

}
