package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.lot.LotDTO;
import uz.uzkassa.smartpossupply.dto.lot.LotPrefixDTO;
import uz.uzkassa.smartpossupply.service.LotService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetLotRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetLotResource {

    LotService lotService;

    @GetMapping
    ResponseEntity<LotDTO> get(@RequestParam("logId") String lotId,
                               @RequestParam("buyerTin") String buyerTin) {
        return ResponseEntity.ok(lotService.get(lotId, buyerTin));
    }

    @GetMapping("/prefixes")
    ResponseEntity<LotPrefixDTO[]> getPrefixes(@RequestParam("logId") String lotId,
                                               @RequestParam("buyerTin") String buyerTin) {
        return ResponseEntity.ok(lotService.getPrefixes(lotId, buyerTin));
    }
}
