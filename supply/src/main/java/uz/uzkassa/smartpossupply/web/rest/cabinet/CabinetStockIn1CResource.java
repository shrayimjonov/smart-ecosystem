package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.aop.onec.OneCValidate;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.stockin.onec.StockIn1CSyncDTO;
import uz.uzkassa.smartpossupply.service.StockInService;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@RestController
@RequestMapping(ApiConstants.cabinetStockIn1CRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
@Slf4j
@OneCValidate
public class CabinetStockIn1CResource {

    StockInService stockInService;

    @Operation(summary = ApiConstants.Send)
    @PostMapping
    public ResponseEntity<Void> send(@RequestBody StockIn1CSyncDTO stockIn1CSyncDTO) {
        stockInService.sendToQueue(stockIn1CSyncDTO);
        return ResponseEntity.ok().build();
    }
}
