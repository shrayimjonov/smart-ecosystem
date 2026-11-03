package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.enums.*;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminCommonRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminCommonResource {

    @GetMapping("/channel-types")
    public ResponseEntity<EnumSet<ChannelType>> getChanelTypes() {
        return ResponseEntity.ok(EnumSet.allOf(ChannelType.class));
    }

    @GetMapping("/stock-statuses")
    public ResponseEntity<EnumSet<StockStatus>> getStockStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockStatus.class));
    }

    @GetMapping("/stock-transfer-statuses")
    public ResponseEntity<EnumSet<StockTransferStatus>> getStockTransferStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(StockTransferStatus.class));
    }

    @GetMapping("/leave-request-reason-types")
    public ResponseEntity<EnumSet<LeaveRequestReasonType>> getLeaveRequestReasonTypes() {
        return ResponseEntity.ok(EnumSet.allOf(LeaveRequestReasonType.class));
    }

    @GetMapping("/leave-request-reason-abbreviations")
    public ResponseEntity<EnumSet<LeaveRequestReasonAbbreviation>> getLeaveRequestReasonAbbreviations() {
        return ResponseEntity.ok(EnumSet.allOf(LeaveRequestReasonAbbreviation.class));
    }

    @GetMapping("/stock-items-types")
    public ResponseEntity<EnumSet<StockItemType>> getStockItemTypes() {
        return ResponseEntity.ok(EnumSet.allOf(StockItemType.class));
    }
}
