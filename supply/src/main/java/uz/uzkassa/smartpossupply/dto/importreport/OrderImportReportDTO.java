package uz.uzkassa.smartpossupply.dto.importreport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 21.05.2024
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class OrderImportReportDTO extends ImportReportDTO {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Long branchId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    Long warehouseId;

    boolean createStockIn = false;

    public OrderImportReportDTO(Long branchId, Long warehouseId) {
        this.branchId = branchId;
        this.warehouseId = warehouseId;
    }
}
