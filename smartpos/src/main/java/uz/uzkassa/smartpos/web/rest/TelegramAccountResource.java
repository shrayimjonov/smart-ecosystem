package uz.uzkassa.smartpos.web.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpos.domain.postgres.user.TelegramAccount;
import uz.uzkassa.smartpos.dto.filter.TelegramAccountFilter;
import uz.uzkassa.smartpos.dto.integration.agreement.PublicAgreementDTO;
import uz.uzkassa.smartpos.dto.telegram.TelegramAccountDTO;
import uz.uzkassa.smartpos.exceptions.BadRequestException;
import uz.uzkassa.smartpos.service.LocalizationService;
import uz.uzkassa.smartpos.service.TelegramAccountService;

import java.util.List;

/**
 * REST controller for managing {@link TelegramAccount}.
 */
@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TelegramAccountResource {
    private final TelegramAccountService telegramAccountService;
    private final LocalizationService localizationService;

    @PostMapping("/telegram-accounts")
    public ResponseEntity<TelegramAccountDTO> createTelegramAccount(@RequestBody TelegramAccountDTO telegramAccountDTO) {
        log.debug("REST request to save TelegramAccount : {}", telegramAccountDTO);
        if (telegramAccountDTO.getId() != null) {
            throw new BadRequestException(localizationService.getMessage("new.telegram.account.cannot.id"));
        }
        TelegramAccountDTO result = telegramAccountService.save(telegramAccountDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/telegram-accounts/{id}")
    public ResponseEntity<TelegramAccountDTO> updateTelegramAccount(@PathVariable Long id, @RequestBody TelegramAccountDTO telegramAccountDTO) {
        log.debug("REST request to update TelegramAccount : {}", telegramAccountDTO);
        if (telegramAccountDTO.getId() == null) {
            if (id == null) {
                throw new BadRequestException(localizationService.getMessage("invalid.id"));
            }
            telegramAccountDTO.setId(id);
        }
        TelegramAccountDTO result = telegramAccountService.save(telegramAccountDTO);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/telegram-accounts/{id}/enable")
    public ResponseEntity<TelegramAccountDTO> enable(@PathVariable Long id) {
        log.debug("REST request to enable TelegramAccount : {}", id);
        TelegramAccountDTO result = telegramAccountService.forceEnable(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/telegram-accounts/available_agreements")
    public ResponseEntity<List<PublicAgreementDTO>> getAvailableAgreements(@RequestParam(value = "companyId") Long companyId) {
        log.debug("REST request to enable TelegramAccount : {}", companyId);
        List<PublicAgreementDTO> result = telegramAccountService.getTelegramAgreementList(companyId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/telegram-accounts/{id}/disable")
    public ResponseEntity<TelegramAccountDTO> disable(@PathVariable Long id) {
        log.debug("REST request to disable TelegramAccount : {}", id);
        telegramAccountService.disable(id);
        return ResponseEntity.ok(telegramAccountService.findOne(id).orElse(null));
    }

    @GetMapping("/telegram-accounts")
    public ResponseEntity<List<TelegramAccountDTO>> getAllTelegramAccounts(Pageable pageable) {
        log.debug("REST request to get a page of TelegramAccounts");
        Page<TelegramAccountDTO> page = telegramAccountService.findAll(pageable);
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/companies/{id}/telegram-accounts")
    public ResponseEntity<List<TelegramAccountDTO>> getCompanyTelegramAccounts(@PathVariable("id") Long companyId) {
        log.debug("REST request to get a page of TelegramAccounts");
        TelegramAccountFilter filter = new TelegramAccountFilter();
        filter.setCompanyId(companyId);
        Page<TelegramAccountDTO> page = telegramAccountService.findAllByFilter(filter);
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/telegram-accounts/{id}")
    public ResponseEntity<TelegramAccountDTO> getTelegramAccount(@PathVariable Long id) {
        log.debug("REST request to get TelegramAccount : {}", id);
        return ResponseEntity.ok(telegramAccountService.findOne(id).orElse(null));
    }

    @DeleteMapping("/telegram-accounts/{id}")
    public ResponseEntity<Void> deleteTelegramAccount(@PathVariable Long id) {
        log.debug("REST request to delete TelegramAccount : {}", id);
        telegramAccountService.delete(id);
        return ResponseEntity.ok().build();
    }
}
