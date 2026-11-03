package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.ProductType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Getter
@Setter
@ToString
public class FacturaProductDTO implements Serializable {

    Long productId;
    ProductType productType;
    CommonDTO branch;
    CommonDTO warehouse;
    BigDecimal qtyInWarehouse;
    LocalDate expiryDate;
    BigDecimal prepaymentAmount;
    String code1C;

    @JsonProperty("OrdNo")
    private String ordNo;

    @JsonProperty("Code")
    private String code;

    @JsonProperty("CommittentName")
    private String committentName;

    @JsonProperty("CommittentTin")
    private String committentTin;

    @JsonProperty("CommittentVatRegCode")
    private String committentVatRegCode;

    @JsonProperty("CommittentVatRegStatus")
    private Integer committentVatRegStatus;

    @JsonProperty("CatalogCode")
    private String catalogCode;

    @JsonProperty("CatalogName")
    private String catalogName;

    @JsonProperty("Barcode")
    private String barcode;

    @JsonProperty("LgotaId")
    private Integer lgotaId;

    @JsonProperty("LgotaName")
    private String lgotaName;

    @JsonProperty("LgotaVatSum")
    private BigDecimal lgotaVatSum = BigDecimal.ZERO;

    @JsonProperty("LgotaType")
    private Integer lgotaType;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Serial")
    private String serial;

    @JsonProperty("MeasureId")
    private Long measureId;

    @JsonProperty("PackageCode")
    private String packageCode;

    @JsonProperty("PackageName")
    private String packageName;

    @JsonProperty("BaseSumma")
    private BigDecimal baseSumma;

    @JsonProperty("ProfitRate")
    private BigDecimal profitRate;

    @JsonProperty("Count")
    private BigDecimal count;

    @JsonProperty("Summa")
    private BigDecimal summa;

    @JsonProperty("ExciseRate")
    private BigDecimal exciseRate;

    @JsonProperty("ExciseSum")
    private BigDecimal exciseSum;

    @JsonProperty("DeliverySum")
    private BigDecimal deliverySum;

    @JsonProperty("VatRate")
    private BigDecimal vatRate;

    @JsonProperty("VatSum")
    private BigDecimal vatSum;

    @JsonProperty("DeliverySumWithVat")
    private BigDecimal deliverySumWithVat;

    @JsonProperty("WithoutVat")
    private Boolean withoutVat;

    @JsonProperty("Marks")
    private MarkDTO marks;

    @JsonProperty("Origin")
    private Integer origin;

    private String originName;

    @JsonProperty("ExchangeInfo")
    private ExchangeInfoDTO exchangeInfo;

    @JsonSetter("ordno")
    public void setordno(String ordno) {
        this.ordNo = ordno;
    }

    public void setOrdNo(String ordNo) {
        this.ordNo = ordNo;
    }

    @JsonSetter("committentname")
    public void setcommittentname(String committentname) {
        this.committentName = committentname;
    }

    public void setCommittentName(String committentName) {
        this.committentName = committentName;
    }

    @JsonSetter("committenttin")
    public void setcommittenttin(String committenttin) {
        this.committentTin = committenttin;
    }

    public void setCommittentTin(String committenttin) {
        this.committentTin = committenttin;
    }

    @JsonSetter("committentvatregcode")
    public void setcommittentvatregcode(String committentvatregcode) {
        this.committentVatRegCode = committentvatregcode;
    }

    public void setCommittentVatRegCode(String committentvatregcode) {
        this.committentVatRegCode = committentvatregcode;
    }

    @JsonSetter("committentvatregstatus")
    public void setcommittentvatregstatus(Integer committentvatregstatus) {
        this.committentVatRegStatus = committentvatregstatus;
    }

    public void setCommittentVatRegStatus(Integer committentvatregstatus) {
        this.committentVatRegStatus = committentvatregstatus;
    }

    @JsonSetter("catalogcode")
    public void setcatalogcode(String catalogcode) {
        this.catalogCode = catalogcode;
    }

    public void setCatalogCode(String catalogCode) {
        this.catalogCode = catalogCode != null ? catalogCode.trim() : null;
    }

    @JsonSetter("catalogname")
    public void setcatalogname(String catalogname) {
        this.catalogName = catalogname;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    @JsonSetter("barcode")
    public void setBarcodeLowerCase(String barcode) {
        this.barcode = barcode != null ? barcode.trim() : null;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode != null ? barcode.trim() : null;
    }

    @JsonSetter("lgotaid")
    public void setLgotaIdLowerCase(Integer lgotaid) {
        this.lgotaId = lgotaid;
    }

    public void setLgotaId(Integer lgotaId) {
        this.lgotaId = lgotaId;
    }

    @JsonSetter("lgotaname")
    public void setLgotaNameLowerCase(String lgotaname) {
        this.lgotaName = lgotaname;
    }

    public void setLgotaName(String lgotaName) {
        this.lgotaName = lgotaName;
    }

    @JsonSetter("lgotatype")
    public void setLgotaTypeLowerCase(Integer lgotatype) {
        this.lgotaType = lgotatype;
    }

    public void setLgotaType(Integer lgotaType) {
        this.lgotaType = lgotaType;
    }

    public void setLgotaVatSum(BigDecimal lgotaVatSum) {
        this.lgotaVatSum = lgotaVatSum;
    }

    @JsonSetter("lgotavatsum")
    public void setLgotaVatSumLowerCase(BigDecimal lgotavatsum) {
        this.lgotaVatSum = lgotavatsum;
    }

    @JsonSetter("name")
    public void setNameLowerCase(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonSetter("serial")
    public void setSerialLowerCase(String serial) {
        this.serial = serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    @JsonSetter("measureid")
    public void setmeasureid(Long measureid) {
        this.measureId = measureid;
    }

    public void setMeasureId(Long measureId) {
        this.measureId = measureId;
    }

    @JsonSetter("packagecode")
    public void setpackagecode(String packagecode) {
        this.packageCode = packagecode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    @JsonSetter("packagename")
    public void setpackagename(String packagename) {
        this.packageName = packagename;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @JsonSetter("basesumma")
    public void setbasesumma(BigDecimal basesumma) {
        this.baseSumma = basesumma;
    }

    public void setBaseSumma(BigDecimal baseSumma) {
        this.baseSumma = baseSumma;
    }

    @JsonSetter("profitrate")
    public void setprofitrate(BigDecimal profitrate) {
        this.profitRate = profitrate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    @JsonSetter("count")
    public void setCountLowerCase(BigDecimal count) {
        this.count = count;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    @JsonSetter("summa")
    public void setSummaLowerCase(BigDecimal summa) {
        this.summa = summa;
    }

    public void setSumma(BigDecimal summa) {
        this.summa = summa;
    }

    @JsonSetter("exciserate")
    public void setexciserate(BigDecimal exciserate) {
        this.exciseRate = Optional.ofNullable(exciserate).orElse(BigDecimal.ZERO);
    }

    public void setExciseRate(BigDecimal exciseRate) {
        this.exciseRate = Optional.ofNullable(exciseRate).orElse(BigDecimal.ZERO);
    }

    @JsonSetter("excisesum")
    public void setexcisesum(BigDecimal excisesum) {
        this.exciseSum = Optional.ofNullable(excisesum).orElse(BigDecimal.ZERO);
    }

    public void setExciseSum(BigDecimal exciseSum) {
        this.exciseSum = Optional.ofNullable(exciseSum).orElse(BigDecimal.ZERO);
    }

    @JsonSetter("deliverysum")
    public void setdeliverysum(BigDecimal deliverysum) {
        this.deliverySum = deliverysum;
    }

    public void setDeliverySum(BigDecimal deliverySum) {
        this.deliverySum = deliverySum;
    }

    @JsonSetter("vatrate")
    public void setvatrate(BigDecimal vatrate) {
        this.vatRate = vatrate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    @JsonSetter("vatsum")
    public void setvatsum(BigDecimal vatsum) {
        this.vatSum = vatsum;
    }

    public void setVatSum(BigDecimal vatSum) {
        this.vatSum = vatSum;
    }

    @JsonSetter("deliverysumwithvat")
    public void setdeliverysumwithvat(BigDecimal deliverysumwithvat) {
        this.deliverySumWithVat = deliverysumwithvat;
    }

    public void setDeliverySumWithVat(BigDecimal deliverySumWithVat) {
        this.deliverySumWithVat = deliverySumWithVat;
    }

    @JsonSetter("withoutvat")
    public void setwithoutvat(Boolean withoutvat) {
        this.withoutVat = withoutvat;
    }

    public void setWithoutVat(Boolean withoutVat) {
        this.withoutVat = withoutVat;
    }

    @JsonSetter("marks")
    public void setMarksLowerCase(MarkDTO marks) {
        this.marks = marks;
    }

    public void setMarks(MarkDTO marks) {
        this.marks = marks;
    }

    @JsonSetter("origin")
    public void setOriginLowerCase(Integer origin) {
        this.origin = origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    @JsonSetter("exchangeinfo")
    public void setexchangeinfo(ExchangeInfoDTO exchangeinfo) {
        this.exchangeInfo = exchangeinfo;
    }

    public void setExchangeInfo(ExchangeInfoDTO exchangeInfo) {
        this.exchangeInfo = exchangeInfo;
    }

    public ProductType getProductType() {
        if (this.productType == null) {
            if (this.marks != null) {
                if (this.marks.getProductType() != null) {
                    productType = ProductType.buildByMarkingCode(this.marks.getProductType());
                }
            }
        }
        return productType;
    }
}
