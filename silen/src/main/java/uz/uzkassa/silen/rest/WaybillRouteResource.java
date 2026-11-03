package uz.uzkassa.silen.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.silen.dto.waybill.WaybillRouteDTO;
import uz.uzkassa.silen.service.WaybillRouteService;

import java.util.List;

@RestController
@RequestMapping("/api/waybill/route")
@RequiredArgsConstructor
public class WaybillRouteResource {
    private final WaybillRouteService waybillRouteService;

    @PostMapping
    public ResponseEntity<WaybillRouteDTO> save(@Valid @RequestBody WaybillRouteDTO waybillRouteDTO) {
        return ResponseEntity.ok(waybillRouteService.save(waybillRouteDTO));
    }

    @GetMapping("/{tin}")
    public ResponseEntity<List<WaybillRouteDTO>> findByTin(@PathVariable String tin) {
        return ResponseEntity.ok(waybillRouteService.findAll(tin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        waybillRouteService.delete(id);
        return ResponseEntity.ok().build();
    }
}
