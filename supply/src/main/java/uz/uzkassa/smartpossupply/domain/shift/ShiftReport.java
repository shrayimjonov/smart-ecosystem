package uz.uzkassa.smartpossupply.domain.shift;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.Branch;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.dto.shift.RegistrationReportDTO;
import uz.uzkassa.smartpossupply.dto.shift.ShiftReportStatisticDTO;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 27.12.2022 18:36
 */
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "RegistrationReportMapper",
                classes = @ConstructorResult(
                        targetClass = RegistrationReportDTO.class,
                        columns = {
                                @ColumnResult(name = "receiptCount", type = BigDecimal.class),
                                @ColumnResult(name = "shiftCount", type = Long.class),
                                @ColumnResult(name = "totalCash", type = BigDecimal.class),
                                @ColumnResult(name = "totalCard", type = BigDecimal.class),
                                @ColumnResult(name = "totalVat", type = BigDecimal.class),
                        }
                )
        ),
        @SqlResultSetMapping(
                name = "ShiftReportStatisticsMapper",
                classes = @ConstructorResult(
                        targetClass = ShiftReportStatisticDTO.class,
                        columns = {
                                @ColumnResult(name = "totalCost", type = BigDecimal.class),
                                @ColumnResult(name = "totalCash", type = BigDecimal.class),
                                @ColumnResult(name = "totalCard", type = BigDecimal.class),
                                @ColumnResult(name = "totalLoyaltyCard", type = BigDecimal.class),
                                @ColumnResult(name = "totalDiscount", type = BigDecimal.class),
                                @ColumnResult(name = "totalExcise", type = BigDecimal.class),
                                @ColumnResult(name = "totalPaid", type = BigDecimal.class),
                                @ColumnResult(name = "totalVat", type = BigDecimal.class),
                                @ColumnResult(name = "receiptCount", type = Long.class),
                                @ColumnResult(name = "draftCount", type = Long.class),
                                @ColumnResult(name = "receiptReturnedCount", type = Long.class),
                                @ColumnResult(name = "totalCashReturned", type = BigDecimal.class),
                                @ColumnResult(name = "totalCardReturned", type = BigDecimal.class),
                                @ColumnResult(name = "totalVatReturned", type = BigDecimal.class),
                                @ColumnResult(name = "shiftCount", type = Long.class)
                        }
                )
        )
}
)
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "shift_report")
public class ShiftReport extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 34839854L;

    @Column(name = "fiscal_number")
    Long fiscalNumber;

    @Column(name = "shift_id")
    Long shiftId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id", insertable = false, updatable = false)
    Shift shift;

    @Column(name = "start_date")
    LocalDateTime startDate;

    @Column(name = "end_date")
    LocalDateTime endDate;

    @Column(name = "receipt_count")
    Long receiptCount;

    @Column(name = "receipt_returned_count")
    Long receiptReturnedCount;

    @Column(name = "total_card", precision = 25, scale = 2)
    BigDecimal totalCard;

    @Column(name = "total_card_returned", precision = 25, scale = 2)
    BigDecimal totalCardReturned;

    @Column(name = "total_cash", precision = 25, scale = 2)
    BigDecimal totalCash;

    @Column(name = "total_cash_returned", precision = 25, scale = 2)
    BigDecimal totalCashReturned;

    @Column(name = "total_vat", precision = 25, scale = 2)
    BigDecimal totalVat;

    @Column(name = "total_vat_returned", precision = 25, scale = 2)
    BigDecimal totalVatReturned;

    @Column(name = "cashier_id")
    Long cashierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_id", insertable = false, updatable = false)
    User cashier;

    @Column(name = "branch_id")
    Long branchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", insertable = false, updatable = false)
    Branch branch;
}
