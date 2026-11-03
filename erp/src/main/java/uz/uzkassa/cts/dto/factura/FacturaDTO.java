package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class FacturaDTO implements Serializable {
    @JsonProperty("Version")
    private Integer version = 1;
    @JsonProperty("FacturaType")
    private Integer facturaType;
    @JsonProperty("SingleSidedType")
    private Integer singleSidedType = 0;
    //    @JsonProperty("OldFacturaDoc")
//    private OldFacturaDoc oldFacturaDoc;
    @JsonProperty("FacturaId")
    private String facturaId;
    @JsonProperty("InvoiceId")
    private Long invoiceId;
    @JsonProperty("FacturaDoc")
    private FacturaDocDTO facturaDoc;
    @JsonProperty("ContractDoc")
    private FacturaContractDTO contractDoc;
    @JsonProperty("EmpowermentId")
    private String empowermentId;
    @JsonProperty("FacturaEmpowermentDoc")
    private FacturaEmpowermentDTO facturaEmpowermentDoc;
    @JsonProperty("ItemReleasedDoc")
    private FacturaItemReleasedDocDTO itemReleasedDoc;
    @JsonProperty("ForeignCompany")
    private FacturaForeignCompanyDTO foreignCompany;
    @JsonProperty("SellerTin")
    private String sellerTin;
    @JsonProperty("BuyerTin")
    private String buyerTin;
    @JsonProperty("BuyerBranchTin")
    private String buyerBranchTin;
    @JsonProperty("Seller")
    private FacturaCustomerDTO seller;
    @JsonProperty("Buyer")
    private FacturaCustomerDTO buyer;
    @JsonProperty("ProductList")
    private FacturaProductListDTO productList;
    @JsonProperty(defaultValue = "6")
    private int sellerCategory = 6;
}
