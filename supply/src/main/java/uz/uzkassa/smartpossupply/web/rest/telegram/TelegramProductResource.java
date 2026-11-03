package uz.uzkassa.smartpossupply.web.rest.telegram;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramTopProductListDTO;
import uz.uzkassa.smartpossupply.service.TelegramOrderService;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.telegramProductRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramProductResource {

    TelegramOrderService telegramOrderService;

    @GetMapping("/top")
    public ResponseEntity<List<TelegramTopProductListDTO>> getTopProducts(@RequestParam String token) {
        return ResponseEntity.ok(telegramOrderService.getTopProducts(token));
    }

}
