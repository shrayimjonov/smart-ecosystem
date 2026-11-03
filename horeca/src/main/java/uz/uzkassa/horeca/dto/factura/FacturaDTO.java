package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Optional;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaDTO implements Serializable {

    private Boolean appliedFinancialDiscount;

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

    @JsonProperty("LotId")
    private String lotId;

    @JsonProperty("BuyerBranchTin")
    private String buyerBranchTin;

    @JsonProperty("Seller")
    private FacturaCompanyDTO seller;

    @JsonProperty("Buyer")
    private FacturaCompanyDTO buyer;

    @JsonProperty("ProductList")
    private FacturaProductListDTO productList;

    private Integer currentStateId;

    private String notes;

    @JsonSetter("version")
    public void setVersionLowerCase(Integer version) {
        this.version = version;
    }

    @JsonSetter("hasmarking")
    public void sethasmarking(Boolean hasmarking) {
        this.hasMarking = Optional.ofNullable(hasmarking).orElse(Boolean.FALSE);
    }

    public void setHasMarking(Boolean hasMarking) {
        this.hasMarking = Optional.ofNullable(hasMarking).orElse(Boolean.FALSE);
    }

    @JsonSetter("facturaid")
    public void setfacturaid(String facturaid) {
        this.facturaId = facturaid;
    }

    public void setFacturaId(String facturaId) {
        this.facturaId = facturaId;
    }

    @JsonSetter("facturatype")
    public void setfacturatype(Integer facturatype) {
        this.facturaType = facturatype;
    }

    public void setFacturaType(Integer facturaType) {
        this.facturaType = facturaType;
    }

    @JsonSetter("singlesidedtype")
    public void setsinglesidedtype(Integer singlesidedtype) {
        this.singleSidedType = singlesidedtype;
    }

    public void setSingleSidedType(Integer singleSidedType) {
        this.singleSidedType = singleSidedType;
    }

    @JsonSetter("oldfacturadoc")
    public void setoldfacturadoc(OldFacturaDoc oldfacturadoc) {
        this.oldFacturaDoc = oldfacturadoc;
    }

    public void setOldFacturaDoc(OldFacturaDoc oldFacturaDoc) {
        this.oldFacturaDoc = oldFacturaDoc;
    }

    @JsonSetter("facturadoc")
    public void setfacturadoc(FacturaDocDTO facturadoc) {
        this.facturaDoc = facturadoc;
    }

    public void setFacturaDoc(FacturaDocDTO facturaDoc) {
        this.facturaDoc = facturaDoc;
    }

    @JsonSetter("contractdoc")
    public void setcontractdoc(FacturaContractDTO contractdoc) {
        this.contractDoc = contractdoc;
    }

    public void setContractDoc(FacturaContractDTO contractDoc) {
        this.contractDoc = contractDoc;
    }

    @JsonSetter("empowermentid")
    public void setempowermentid(String empowermentid) {
        this.empowermentId = empowermentid;
    }

    public void setEmpowermentId(String empowermentId) {
        this.empowermentId = empowermentId;
    }

    @JsonSetter("facturaempowermentdoc")
    public void setfacturaempowermentdoc(FacturaEmpowermentDTO facturaempowermentdoc) {
        this.facturaEmpowermentDoc = facturaempowermentdoc;
    }

    public void setFacturaEmpowermentDoc(FacturaEmpowermentDTO facturaEmpowermentDoc) {
        this.facturaEmpowermentDoc = facturaEmpowermentDoc;
    }

    @JsonSetter("itemreleaseddoc")
    public void setitemreleaseddoc(FacturaItemReleasedDocDTO itemreleaseddoc) {
        this.itemReleasedDoc = itemreleaseddoc;
    }

    public void setItemReleasedDoc(FacturaItemReleasedDocDTO itemReleasedDoc) {
        this.itemReleasedDoc = itemReleasedDoc;
    }

    @JsonSetter("foreigncompany")
    public void setforeigncompany(FacturaForeignCompanyDTO foreigncompany) {
        this.foreignCompany = foreigncompany;
    }

    public void setForeignCompany(FacturaForeignCompanyDTO foreignCompany) {
        this.foreignCompany = foreignCompany;
    }

    @JsonSetter("sellertin")
    public void setsellertin(String sellertin) {
        this.sellerTin = sellertin;
    }

    public void setSellerTin(String sellerTin) {
        this.sellerTin = sellerTin;
    }

    @JsonSetter("buyertin")
    public void setbuyertin(String buyertin) {
        this.buyerTin = buyertin;
    }

    public void setBuyerTin(String buyerTin) {
        this.buyerTin = buyerTin;
    }

    public String getBuyerTin() {
        return buyerTin;
    }

    public String getSellerTin() {
        return sellerTin;
    }

    @JsonSetter("lotid")
    public void setlotid(String lotid) {
        this.lotId = lotid;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    @JsonSetter("seller")
    public void setSellerLowerCase(FacturaCompanyDTO seller) {
        this.seller = seller;
    }

    @JsonSetter("buyer")
    public void setBuyerLowerCase(FacturaCompanyDTO buyer) {
        this.buyer = buyer;
    }

    @JsonSetter("productlist")
    public void setproductlist(FacturaProductListDTO productlist) {
        this.productList = productlist;
    }

    public void setProductList(FacturaProductListDTO productList) {
        this.productList = productList;
    }

    public Status getState() {
        if (getCurrentStateId() == null) {
            return null;
        }
        switch (getCurrentStateId()) {
            case 17:
                return Status.CANCELLED;
            case 20:
                return Status.REJECTED;
            case 30:
                return Status.ACCEPTED;
            case 10:
            case 15:
            default:
                return Status.PENDING;
        }
    }
}
