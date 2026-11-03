package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.Status;

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
    @JsonProperty("Seller")
    private FacturaCustomer seller;
    @JsonProperty("Buyer")
    private FacturaCustomer buyer;
    @JsonProperty("ProductList")
    private FacturaProductListDTO productList;
    @JsonProperty("FacturaInvestmentObjectDoc")
    private FacturaInvestmentObjectDoc facturaInvestmentObjectDoc;
    //    @JsonIgnore
    private Integer currentStateId;
    private String notes;
    private String buyerAccount;

    @JsonSetter("version")
    public void setVersionLowerCase(Integer version) {
        this.version = version;
    }

    @JsonSetter("hasmarking")
    public void sethasmarking(Boolean hasmarking) {
        this.hasMarking = hasmarking;
    }

    public void setHasMarking(Boolean hasMarking) {
        this.hasMarking = hasMarking;
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
    public void setfacturadoc(FacturaDoc facturadoc) {
        this.facturaDoc = facturadoc;
    }

    public void setFacturaDoc(FacturaDoc facturaDoc) {
        this.facturaDoc = facturaDoc;
    }

    @JsonSetter("contractdoc")
    public void setcontractdoc(FacturaContract contractdoc) {
        this.contractDoc = contractdoc;
    }

    public void setContractDoc(FacturaContract contractDoc) {
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
    public void setfacturaempowermentdoc(FacturaEmpowerment facturaempowermentdoc) {
        this.facturaEmpowermentDoc = facturaempowermentdoc;
    }

    public void setFacturaEmpowermentDoc(FacturaEmpowerment facturaEmpowermentDoc) {
        this.facturaEmpowermentDoc = facturaEmpowermentDoc;
    }

    @JsonSetter("itemreleaseddoc")
    public void setitemreleaseddoc(ItemReleasedDoc itemreleaseddoc) {
        this.itemReleasedDoc = itemreleaseddoc;
    }

    public void setItemReleasedDoc(ItemReleasedDoc itemReleasedDoc) {
        this.itemReleasedDoc = itemReleasedDoc;
    }

    @JsonSetter("foreigncompany")
    public void setforeigncompany(FacturaForeignCompany foreigncompany) {
        this.foreignCompany = foreigncompany;
    }

    public void setForeignCompany(FacturaForeignCompany foreignCompany) {
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

    @JsonSetter("lotid")
    public void setlotid(String lotid) {
        this.lotId = lotid;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    @JsonSetter("seller")
    public void setSellerLowerCase(FacturaCustomer seller) {
        this.seller = seller;
    }

    @JsonSetter("buyer")
    public void setBuyerLowerCase(FacturaCustomer buyer) {
        this.buyer = buyer;
    }

    @JsonSetter("productlist")
    public void setproductlist(FacturaProductListDTO productlist) {
        this.productList = productlist;
    }

    public void setProductList(FacturaProductListDTO productList) {
        this.productList = productList;
    }

    @JsonSetter("facturainvestmentobjectdoc")
    public void setFacturaInvestmentObjectDocLowerCase(FacturaInvestmentObjectDoc facturaInvestmentObjectDoc) {
        this.facturaInvestmentObjectDoc = facturaInvestmentObjectDoc;
    }

    public void setFacturaInvestmentObjectDoc(FacturaInvestmentObjectDoc facturaInvestmentObjectDoc) {
        this.facturaInvestmentObjectDoc = facturaInvestmentObjectDoc;
    }

    @JsonIgnore
    public Status getState() {
        if (getCurrentStateId() == null) {
            return null;
        }
        switch (getCurrentStateId()) {
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
