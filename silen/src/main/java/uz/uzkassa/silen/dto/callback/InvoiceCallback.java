package uz.uzkassa.silen.dto.callback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.Status;

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

    public Status getState(){
        if(getCurrentStateId() == null){
            return Status.PENDING;
        }
        switch (getCurrentStateId()){
            case 10:
                return Status.PENDING;
            case 15:
                return Status.PENDING;
            case 17:
                return Status.CANCELLED;
            case 20:
                return Status.REJECTED;
            case 30:
                return Status.ACCEPTED;
            default:
                return Status.PENDING;
        }
    }
}
