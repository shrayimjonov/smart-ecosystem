package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.stocktransfer.StockTransferItemDetailDTO;
import uz.uzkassa.horeca.service.StockTransferItemsService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetStockTransferItemsRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetStockTransferItemsResource {
    StockTransferItemsService stockTransferItemsService;

    @GetMapping(ApiConstants.id)
    public ResponseEntity<List<StockTransferItemDetailDTO>> getList(@PathVariable Long id) {
        return ResponseEntity.ok(stockTransferItemsService.getList(id));
    }
}
