package uz.uzkassa.smartpos.dto.sales;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.domain.postgres.receipt.Additional;
import uz.uzkassa.smartpos.domain.postgres.sales.Sales;
import uz.uzkassa.smartpos.enums.PaymentType;
import uz.uzkassa.smartpos.enums.sales.SalesStatus;
import uz.uzkassa.smartpos.enums.sales.SalesType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DTO for {@link Sales}
 */
@Getter
@Setter
public final class SalesCreateDto implements Serializable {
    private @NotNull String uid;
    private @NotNull LocalDateTime createdDateTime;
    private String originUid;
    private String fiscalSign;
    private String fiscalUrl;
    private Long shiftNumber;
    private Double lat;
    private Double lon;
    private @NotNull BigDecimal totalCost;
    private BigDecimal totalPaid;
    private BigDecimal discountPercent;
    private @NotNull SalesType type;
    private @NotNull SalesStatus status;
    private Map<PaymentType, BigDecimal> payments;
    private Long branchId;
    private Long companyId;
    private Long createdById;
    private String terminalSN;
    private String facturaId;
    private String customerName;
    private String customerPhone;
    private Long customerId;
    private boolean newCustomer;
    private String billId;
    private boolean sendSms;
    private Additional additional;
    private @Valid
    @NotEmpty List<SalesDetailCreateDto> receiptDetails;
    private boolean forceToPrint;
    private Long promotionId;

    @JsonIgnore
    public boolean sendToWarehouse() {
        return SalesStatus.COMPLETED.equals(status) && List.of(SalesType.SALE, SalesType.RETURNED).contains(type);
    }

    @JsonIgnore
    public boolean sendToClickhouse() {
        return SalesStatus.COMPLETED.equals(status);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesCreateDto that = (SalesCreateDto) o;
        return Objects.equals(uid, that.uid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uid);
    }
}