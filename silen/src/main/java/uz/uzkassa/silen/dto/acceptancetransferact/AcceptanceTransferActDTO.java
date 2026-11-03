package uz.uzkassa.silen.dto.acceptancetransferact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcceptanceTransferActDTO implements Serializable {

    String silenId;

    @JsonProperty("AcceptanceTransferActId")
    String acceptanceTransferActId;

    @JsonProperty("SellerPinfl")
    String sellerPinfl;

    @JsonProperty("BuyerTinOrPinfl")
    String buyerTinOrPinfl;

    @JsonProperty("TotalPrice")
    BigDecimal totalPrice;

    @JsonProperty("Seller")
    AcceptanceTransferActSellerDTO seller;

    @JsonProperty("Buyer")
    AcceptanceTransferActBuyerDTO buyer;

    @JsonProperty("AcceptanceTransferActDoc")
    AcceptanceTransferActDocDTO acceptanceTransferActDoc;

    @JsonProperty("ContractDoc")
    AcceptanceTransferActContractDocDTO contractDoc;

    @JsonProperty("Products")
    List<AcceptanceTransferActProductDTO> products;

    @JsonSetter("acceptancetransferactid")
    public void setacceptancetransferactid(String acceptancetransferactid) {
        this.acceptanceTransferActId = acceptancetransferactid;
    }

    @JsonSetter("sellerpinfl")
    public void setsellerpinfl(String sellerpinfl) {
        this.sellerPinfl = sellerpinfl;
    }

    @JsonSetter("buyertinorpinfl")
    public void setbuyertinorpinfl(String buyertinorpinfl) {
        this.buyerTinOrPinfl = buyertinorpinfl;
    }

    @JsonSetter("totalprice")
    public void settotalprice(BigDecimal totalprice) {
        this.totalPrice = totalprice;
    }

    @JsonSetter("seller")
    public void setSellerLowerCase(AcceptanceTransferActSellerDTO seller) {
        this.seller = seller;
    }

    @JsonSetter("buyer")
    public void setBuyerLowerCase(AcceptanceTransferActBuyerDTO buyer) {
        this.buyer = buyer;
    }

    @JsonSetter("acceptancetransferactdoc")
    public void setacceptancetransferactdoc(AcceptanceTransferActDocDTO acceptancetransferactdoc) {
        this.acceptanceTransferActDoc = acceptancetransferactdoc;
    }

    public void setAcceptanceTransferActDoc(AcceptanceTransferActDocDTO acceptanceTransferActDoc) {
        this.acceptanceTransferActDoc = acceptanceTransferActDoc;
    }

    @JsonSetter("contractdoc")
    public void setcontractdoc(AcceptanceTransferActContractDocDTO contractdoc) {
        this.contractDoc = contractdoc;
    }

    @JsonSetter("products")
    public void setProductsLowerCase(List<AcceptanceTransferActProductDTO> products) {
        this.products = products;
    }

    public void setSilenId(String silenId) {
        this.silenId = silenId;
    }

    public void setAcceptanceTransferActId(String acceptanceTransferActId) {
        this.acceptanceTransferActId = acceptanceTransferActId;
    }

    public void setSellerPinfl(String sellerPinfl) {
        this.sellerPinfl = sellerPinfl;
    }

    public void setBuyerTinOrPinfl(String buyerTinOrPinfl) {
        this.buyerTinOrPinfl = buyerTinOrPinfl;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setSeller(AcceptanceTransferActSellerDTO seller) {
        this.seller = seller;
    }

    public void setBuyer(AcceptanceTransferActBuyerDTO buyer) {
        this.buyer = buyer;
    }

    public void setContractDoc(AcceptanceTransferActContractDocDTO contractDoc) {
        this.contractDoc = contractDoc;
    }

    public void setProducts(List<AcceptanceTransferActProductDTO> products) {
        this.products = products;
    }
}
