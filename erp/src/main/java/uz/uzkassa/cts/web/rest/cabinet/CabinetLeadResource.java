package uz.uzkassa.cts.web.rest.cabinet;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.lead.LeadMetaDTO;
import uz.uzkassa.cts.dto.lead.LeadRequestDTO;
import uz.uzkassa.cts.service.LeadService;

import static lombok.AccessLevel.PRIVATE;

@Slf4j
@RestController
@RequestMapping(ApiConstants.cabinetLeadRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetLeadResource {

    LeadService leadService;

    @PostMapping(value = "/meta")
    public ResponseEntity<Long> createFromMeta(@RequestBody LeadMetaDTO leadMetaDTO) {
        return ResponseEntity.ok(leadService.createFromMeta(leadMetaDTO));
    }

    @PostMapping(value = "/telegram")
    public ResponseEntity<Void> createFromTelegram(@RequestBody LeadRequestDTO leadRequestDTO) {
        log.info("Lead Telegram bot : {}", leadRequestDTO);
        leadService.createFromTelegramBot(leadRequestDTO);
        return ResponseEntity.ok().build();
    }
}
