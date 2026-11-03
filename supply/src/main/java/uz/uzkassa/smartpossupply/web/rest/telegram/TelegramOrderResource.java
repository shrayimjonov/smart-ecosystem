package uz.uzkassa.smartpossupply.web.rest.telegram;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.order.telegram.TelegramOrderDTO;
import uz.uzkassa.smartpossupply.dto.order.telegram.TelegramOrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.order.telegram.TelegramOrderListDTO;
import uz.uzkassa.smartpossupply.filters.OrderFilter;
import uz.uzkassa.smartpossupply.service.TelegramOrderService;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

@RestController
@RequestMapping(ApiConstants.telegramOrderRootApi)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramOrderResource {

    TelegramOrderService telegramOrderService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody TelegramOrderDTO telegramOrderDTO,
                                       @RequestParam("token") String token,
                                       @RequestParam("userId") Long userId) {
        return ResponseEntity.ok(telegramOrderService.create(telegramOrderDTO, token, userId));
    }

    @GetMapping
    public ResponseEntity<Page<TelegramOrderListDTO>> getList(@ParameterObject OrderFilter filter) {
        if (!StringUtils.hasText(filter.getToken()) || filter.getTelegramUserId() == null) {
            throw new BadRequestException("Token and userId are required");
        }
        return ResponseEntity.ok(telegramOrderService.getList(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<TelegramOrderDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(telegramOrderService.get(id));
    }
}
