package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;
import uz.uzkassa.smartpos.enums.MarkingWarehouseStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for {@link uz.uzkassa.smartpos.domain.postgres.warehouse.MarkingIncome}
 */
@Getter
@Setter
public class MarkingStatusReportDto implements Serializable {
    private String marking;
    private String printCode;
    private LocalDateTime createdDate;
    private LocalDate operationDate;
    private LocalDateTime receivedAt;
    private CommonDTO branch;
    private CommonDTO fromBranch;
    private CommonDTO product;
    private CommonDTO createdBy;
    private MarkingWarehouseStatus status;
}