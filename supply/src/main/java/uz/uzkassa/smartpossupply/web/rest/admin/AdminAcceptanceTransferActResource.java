package uz.uzkassa.smartpossupply.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.acceptancetransferact.AcceptanceTransferActDTO;
import uz.uzkassa.smartpossupply.enums.AcceptanceTransferActStatus;
import uz.uzkassa.smartpossupply.service.AcceptanceTransferActService;
import uz.uzkassa.smartpossupply.web.rest.errors.BadRequestException;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminAcceptanceTransferActRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminAcceptanceTransferActResource {

    AcceptanceTransferActService acceptanceTransferActService;

    @PostMapping("/silen/webhook/created")
    public ResponseEntity<Void> silenWebhookCreated(@RequestBody AcceptanceTransferActDTO acceptanceTransferActDTO) {
        acceptanceTransferActDTO.setStatus(AcceptanceTransferActStatus.SENT);
        acceptanceTransferActService.create(acceptanceTransferActDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/smartpos/webhook/update-status/{supplyId}")
    public ResponseEntity<Void> smartposWebhookUpdateStatus(@PathVariable Long supplyId, @RequestParam("status") AcceptanceTransferActStatus status) {
        if (!(AcceptanceTransferActStatus.REJECT.equals(status) || AcceptanceTransferActStatus.ACCEPT.equals(status))) {
            throw new BadRequestException("Incorrect Acceptance transfer act status");
        }
        acceptanceTransferActService.updateStatus(supplyId, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/silen/webhook/update-status/{silenId}")
    public ResponseEntity<Void> silenWebhookUpdateStatus(@PathVariable String silenId, @RequestParam("status") AcceptanceTransferActStatus status) {
        if (!AcceptanceTransferActStatus.CANCEL.equals(status)) {
            throw new BadRequestException("Incorrect Acceptance transfer act status");
        }
        acceptanceTransferActService.updateStatus(silenId, status);
        return ResponseEntity.ok().build();
    }
}
