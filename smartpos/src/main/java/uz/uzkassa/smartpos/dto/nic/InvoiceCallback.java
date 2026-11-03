package uz.uzkassa.smartpos.dto.nic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.enums.Status;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class InvoiceCallback implements Serializable {
    @JsonProperty("FacturaId")
    private String facturaId;
    @JsonProperty("FacturaProductId")
    private String facturaProductId;
    @JsonProperty("CurrentStateId")
    private Integer currentStateId;
    @JsonProperty("SellerTin")
    private String sellerTin;
    @JsonProperty("SellerName")
    private String sellerName;
    @JsonProperty("SellerBranchCode")
    private String sellerBranchCode;
    @JsonProperty("SellerBranchName")
    private String sellerBranchName;
    @JsonProperty("BuyerTin")
    private String buyerTin;
    @JsonProperty("BuyerName")
    private String buyerName;
    @JsonProperty("BuyerBranchCode")
    private String buyerBranchCode;
    @JsonProperty("BuyerBranchName")
    private String buyerBranchName;
    @JsonProperty("FacturaNo")
    private String facturaNo;
    @JsonProperty("FacturaDate")
    private ZonedDateTime facturaDate;
    @JsonProperty("ContractNo")
    private String contractNo;
    @JsonProperty("ContractDate")
    private ZonedDateTime contractDate;
    @JsonProperty("PayableTotal")
    private BigDecimal payableTotal;
    @JsonProperty("Notes")
    private String notes;

    public Status getState() {
        if (getCurrentStateId() == null) {
            return Status.PENDING;
        }
        return switch (getCurrentStateId()) {
            case 10 -> Status.PENDING;
            case 15 -> Status.PENDING;
            case 17 -> Status.CANCELLED;
            case 20 -> Status.REJECTED;
            case 30 -> Status.ACCEPTED;
            default -> Status.PENDING;
        };
    }
}
