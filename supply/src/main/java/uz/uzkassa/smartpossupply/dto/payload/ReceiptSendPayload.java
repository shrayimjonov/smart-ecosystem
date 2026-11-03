package uz.uzkassa.smartpossupply.dto.payload;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 20.12.2022 15:40
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
@NoArgsConstructor
public class ReceiptSendPayload {

    @NotNull
    Long stockOutId;

    @NotNull
    Long branchId;

    String token;

    boolean xtraceEnabled;

    boolean committent;

    public ReceiptSendPayload(Long stockOutId, Long branchId, String token) {
        this.stockOutId = stockOutId;
        this.branchId = branchId;
        this.token = token;
    }

    public ReceiptSendPayload(Long stockOutId, Long branchId, String token, boolean xtraceEnabled, boolean committent) {
        this.stockOutId = stockOutId;
        this.branchId = branchId;
        this.token = token;
        this.xtraceEnabled = xtraceEnabled;
        this.committent = committent;
    }
}
