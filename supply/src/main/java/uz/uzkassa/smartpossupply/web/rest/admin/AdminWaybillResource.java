package uz.uzkassa.smartpossupply.web.rest.admin;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDTO;
import uz.uzkassa.smartpossupply.service.WaybillService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminWaybillRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminWaybillResource {

    WaybillService waybillService;

    @Operation(summary = "Waybill sign")
    @GetMapping("/sign/{tinOrPinfl}/{waybillLocalId}")
    public ResponseEntity<String> getSign(@PathVariable("tinOrPinfl") String tinOrPinfl, @PathVariable("waybillLocalId") String waybillLocalId, @RequestParam("currentStateId") Integer currentStateId, @RequestParam("isConsignor") boolean isConsignor) {
        return ResponseEntity.ok(waybillService.getSign(tinOrPinfl, waybillLocalId, currentStateId, isConsignor));
    }

    @Operation(summary = "Convert sign")
    @GetMapping("/convert")
    public ResponseEntity<WaybillDTO> convertSign(@RequestBody String sign) {
        return ResponseEntity.ok(waybillService.convertFromSign(sign));
    }
}
