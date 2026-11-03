package uz.uzkassa.smartpossupply.web.rest.telegram;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.telegram.TelegramCategoryListDTO;
import uz.uzkassa.smartpossupply.filters.TelegramCategoryFilter;
import uz.uzkassa.smartpossupply.service.TelegramOrderService;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.telegramCategoryRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramCategoryResource {

    TelegramOrderService telegramOrderService;

    @GetMapping
    public ResponseEntity<List<TelegramCategoryListDTO>> getList(@ParameterObject TelegramCategoryFilter filter) {
        return ResponseEntity.ok(telegramOrderService.getCategoryList(filter));
    }
}
