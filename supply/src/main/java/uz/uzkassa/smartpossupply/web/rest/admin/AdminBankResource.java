package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.bank.BankLookUpDTO;
import uz.uzkassa.smartpossupply.filters.BankFilter;
import uz.uzkassa.smartpossupply.service.BankService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminBankRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminBankResource {

    BankService bankService;

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<BankLookUpDTO>> lookUp(@ParameterObject BankFilter filter) {
        return ResponseEntity.ok(bankService.lookUp(filter));
    }
}
