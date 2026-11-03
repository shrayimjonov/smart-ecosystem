package uz.uzkassa.silen.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.silen.dto.marking.ValidationBatchDTO;
import uz.uzkassa.silen.service.StationService;

import java.util.Map;

/**
 * REST controller for managing {@link uz.uzkassa.silen.domain.Warehouse}.
 */
@RestController
@RequestMapping("/api/station")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StationResource {
    StationService stationService;

    @PostMapping("/validate")
    public ResponseEntity<Map<String, String>> cisesInfo(@RequestBody ValidationBatchDTO request) {
        return ResponseEntity.ok(stationService.validateBatch(request));
    }
    @PostMapping("/validate/test")
    public ResponseEntity<Map<String, String>> cisesInfoTest(@RequestBody ValidationBatchDTO request) {
        return ResponseEntity.ok(stationService.validateBatchTest(request));
    }
}
