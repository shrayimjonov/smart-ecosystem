package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.receipt.ReceiptStatisticDTO;
import uz.uzkassa.horeca.dto.receipt.admin.ReceiptListAdminDTO;
import uz.uzkassa.horeca.dto.receipt.cabinet.ReceiptDetailCabinetDTO;
import uz.uzkassa.horeca.dto.receipt.cabinet.ReceiptListCabinetDTO;
import uz.uzkassa.horeca.filters.ReceiptFilter;
import uz.uzkassa.horeca.service.ReceiptService;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminReceiptRooApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminReceiptResource {

    ReceiptService receiptService;

    @GetMapping
    public ResponseEntity<Page<ReceiptListAdminDTO>> getList(@ParameterObject ReceiptFilter filter) {
        return ResponseEntity.ok(receiptService.getListForAdmin(filter));
    }

    @DeleteMapping(ApiConstants.id)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        receiptService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<ReceiptStatisticDTO> getStatistics(@ParameterObject ReceiptFilter filter) {
        return ResponseEntity.ok(receiptService.getStatistics(filter));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<ReceiptDetailCabinetDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.getForCabinet(id));
    }
}
