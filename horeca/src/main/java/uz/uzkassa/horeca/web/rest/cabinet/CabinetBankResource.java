package uz.uzkassa.horeca.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.bank.BankLookUpDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.service.BankService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetBankRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetBankResource {

    BankService bankService;

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<BankLookUpDTO>> lookUp(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(bankService.lookUp(filter));
    }
}
