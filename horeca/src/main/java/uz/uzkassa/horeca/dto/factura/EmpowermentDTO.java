package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class EmpowermentDTO implements Serializable {

    @JsonProperty("ContractDoc")
    private EmpowermentContractDTO contractDoc;

    @JsonProperty("EmpowermentId")
    private String empowermentId;

    @JsonProperty("EmpowermentDoc")
    private EmpowermentDocDTO empowermentDoc;

    @JsonProperty("Agent")
    private AgentDTO agent;

    @JsonProperty("SellerTin")
    private String sellerTin;

    @JsonProperty("BuyerTin")
    private String buyerTin;

    @JsonProperty("Seller")
    private FacturaCompanyDTO seller;

    @JsonProperty("Buyer")
    private FacturaCompanyDTO buyer;

    @JsonProperty("ProductList")
    private FacturaProductListDTO productList;
}
