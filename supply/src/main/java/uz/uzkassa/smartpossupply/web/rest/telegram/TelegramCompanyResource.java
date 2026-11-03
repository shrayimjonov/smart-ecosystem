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
import uz.uzkassa.smartpossupply.dto.file.FileDTO;
import uz.uzkassa.smartpossupply.service.TelegramOrderService;

@RestController
@RequestMapping(ApiConstants.telegramCompanyRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramCompanyResource {

    TelegramOrderService telegramOrderService;

    @GetMapping("/background-photo")
    public ResponseEntity<FileDTO> getBackgroundPhoto(@RequestParam String token) {
        return ResponseEntity.ok(telegramOrderService.getCompanyBackgroundPhoto(token));
    }

    @GetMapping("/logo")
    public ResponseEntity<FileDTO> getLogo(@RequestParam String token) {
        return ResponseEntity.ok(telegramOrderService.getCompanyLogo(token));
    }
}
