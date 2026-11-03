package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.filters.ClickhouseFilter;
import uz.uzkassa.smartpossupply.service.ClickhouseSyncService;
import uz.uzkassa.smartpossupply.service.TelegramBotFacturaService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.05.2023
 */

@RestController
@RequestMapping(ApiConstants.adminClickhouseRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminClickhouseResource {

    ClickhouseSyncService clickhouseSyncService;
    TelegramBotFacturaService telegramFacturaService;
    Environment environment;

    @GetMapping("/sync")
    public ResponseEntity<Void> sync(@ParameterObject ClickhouseFilter filter) {
//        clickhouseSyncService.reSync(filter);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test-invoice-tg-bot")
    public ResponseEntity<Void> checkInvoiceTgBot() {
        StringBuilder message = new StringBuilder();
        message.append("<b>Environment:</b> ").append(environment.getActiveProfiles()[0]).append("\n");
        message.append("<b>Method:</b> ").append("Test").append("\n");
        message.append("<b>Sign:</b> ").append("sign").append("\n");
        message.append("<b>Status:</b> ").append("status").append("\n");
        telegramFacturaService.send(message.toString());
        return ResponseEntity.ok().build();
    }
}
