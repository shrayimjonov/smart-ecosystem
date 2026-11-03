package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.transaction.TransactionListDTO;
import uz.uzkassa.smartpossupply.filters.TransactionFilter;
import uz.uzkassa.smartpossupply.service.TransactionService;

/**
 * Powered by:Shuxratjon Rayimjonov
 * Date: 08.02.2023 14:05
 */
@RestController
@RequestMapping(ApiConstants.cabinetTransactionRootApi)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetTransactionResource {

    TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionListDTO>> getList(@ParameterObject TransactionFilter filter) {
        return ResponseEntity.ok(transactionService.getList(filter));
    }

}
