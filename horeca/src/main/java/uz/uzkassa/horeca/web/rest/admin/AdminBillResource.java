package uz.uzkassa.horeca.web.rest.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.terminal.*;
import uz.uzkassa.horeca.service.BillService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminBillRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AdminBillResource {

    BillService billService;

    @PostMapping(ApiConstants.statuses)
    public ResponseData<?> updateBillStatus(@RequestParam String billId, @RequestBody @Valid ApayUpdateBillStatusRequest request) {
        request.setBillId(billId);
        log.info("=====================================================================");
        log.info("Update bill status request: {}", request);
        log.info("=====================================================================");
        billService.updateBillStatus(request);
        return ResponseData.ok();
    }
}
