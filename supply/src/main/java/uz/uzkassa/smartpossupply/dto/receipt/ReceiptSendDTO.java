package uz.uzkassa.smartpossupply.dto.receipt;

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
public class ReceiptSendDTO {

    @NotNull
    Long stockOutId;

    @NotNull
    Long branchId;

    boolean committent;
}
