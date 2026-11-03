package uz.uzkassa.smartpos.dto.factura;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpos.enums.Status;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacturaDTO implements Serializable {
    @JsonProperty("Version")
    private Integer version = 1;
    @JsonProperty("HasMarking")
    private Boolean hasMarking;
    @JsonProperty("FacturaType")
    private Integer facturaType;
    @JsonProperty("SingleSidedType")
    private Integer singleSidedType = 0;
    @JsonProperty("OldFacturaDoc")
    private OldFacturaDoc oldFacturaDoc;
    @JsonProperty("FacturaId")
    private String facturaId;
    @JsonProperty("FacturaDoc")
    private FacturaDoc facturaDoc;
    @JsonProperty("ContractDoc")
    private FacturaContract contractDoc;
    @JsonProperty("EmpowermentId")
    private String empowermentId;
    @JsonProperty("FacturaEmpowermentDoc")
    private FacturaEmpowerment facturaEmpowermentDoc;
    @JsonProperty("ItemReleasedDoc")
    private ItemReleasedDoc itemReleasedDoc;
    @JsonProperty("ForeignCompany")
    private FacturaForeignCompany foreignCompany;
    @JsonProperty("SellerTin")
    private String sellerTin;
    @JsonProperty("BuyerTin")
    private String buyerTin;
    @JsonProperty("LotId")
    private String lotId;
    @JsonProperty("BuyerBranchTin")
    private String buyerBranchTin;
    @JsonProperty("Seller")
    private FacturaCustomer seller;
    @JsonProperty("Buyer")
    private FacturaCustomer buyer;
    @JsonProperty("ProductList")
    private FacturaProductListDTO productList;
    private Integer currentStateId;
    private String notes;

    @JsonIgnore
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
