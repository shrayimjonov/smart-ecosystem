package uz.uzkassa.smartpossupply.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.service.TelegramConnectTariffService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.telegramConnectTariffRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetTelegramConnectTariffResource {

    TelegramConnectTariffService telegramConnectTariffService;

    @PostMapping
    public ResponseEntity<String> sendMessage() {
        telegramConnectTariffService.sendMessage();
        return ResponseEntity.ok().build();
    }
}
