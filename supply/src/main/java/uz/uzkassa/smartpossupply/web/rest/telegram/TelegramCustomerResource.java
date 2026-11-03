package uz.uzkassa.smartpossupply.web.rest.telegram;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.customer.CustomerTelegramDTO;
import uz.uzkassa.smartpossupply.enums.TelegramBotType;
import uz.uzkassa.smartpossupply.filters.CompanyFilter;
import uz.uzkassa.smartpossupply.service.CustomerService;
import uz.uzkassa.smartpossupply.service.TelegramOrderService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(ApiConstants.telegramCustomerRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramCustomerResource {

    TelegramOrderService telegramOrderService;
    CustomerService customerService;

    @GetMapping(ApiConstants.check)
    public ResponseEntity<Long> check(@RequestParam("token") String token,
                                      @RequestParam("telegramUserId") Long userId,
                                      @RequestParam("botType") String botType) {
        return ResponseEntity.ok(telegramOrderService.checkCustomer(token, userId, botType));
    }

    @PostMapping
    public ResponseEntity<Long> createOrCheck(@Valid @RequestBody CustomerTelegramDTO customerTelegramDTO) {
        if (customerTelegramDTO.getBotType().equals(TelegramBotType.CUSTOMER)) {
            return ResponseEntity.ok(customerService.create(customerTelegramDTO));
        }
        if (customerTelegramDTO.getBotType().equals(TelegramBotType.SUPPLIER)) {
            return ResponseEntity.ok(telegramOrderService.checkEmployee(customerTelegramDTO));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(ApiConstants.lookUp)
    public ResponseEntity<List<CommonDTO>> lookUp(@ParameterObject CompanyFilter filter) {
        if (filter.getUserId() == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        return ResponseEntity.ok(customerService.lookUpForTelegram(filter));
    }
}
