package uz.uzkassa.smartpossupply.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.telegramintegration.TelegramIntegrationDTO;
import uz.uzkassa.smartpossupply.dto.telegramintegration.TelegramIntegrationDetailDTO;
import uz.uzkassa.smartpossupply.dto.telegramintegration.TelegramIntegrationListDTO;
import uz.uzkassa.smartpossupply.enums.TelegramBotType;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.TelegramIntegrationService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminTelegramIntegrationRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminTelegramIntegrationResource {

    TelegramIntegrationService telegramIntegrationService;

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody TelegramIntegrationDTO telegramIntegrationDTO) {
        return ResponseEntity.ok(telegramIntegrationService.create(telegramIntegrationDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @Valid @RequestBody TelegramIntegrationDTO telegramIntegrationDTO) {
        return ResponseEntity.ok(telegramIntegrationService.update(id, telegramIntegrationDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<TelegramIntegrationDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(telegramIntegrationService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<TelegramIntegrationListDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(telegramIntegrationService.getList(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        telegramIntegrationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.types)
    public ResponseEntity<EnumSet<TelegramBotType>> getTypes() {
        return ResponseEntity.ok(EnumSet.allOf(TelegramBotType.class));
    }
}
