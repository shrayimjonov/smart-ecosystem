package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FacturaProductDTO implements Serializable {
    @JsonProperty("OrdNo")
    private String ordNo;
    @JsonProperty("ComittentName")
    private String comittentName;
    @JsonProperty("ComittentTin")
    private String comittentTin;
    @JsonProperty("ComittentVatRegCode")
    private String comittentVatRegCode;
    @JsonProperty("CatalogCode")
    private String catalogCode;
    @JsonProperty("CatalogName")
    private String catalogName;
    @JsonProperty("Barcode")
    private String barcode;
    @JsonProperty("WarehouseId")
    private Integer warehouseId;
    @JsonProperty("LgotaId")
    private Integer lgotaId;
    @JsonProperty("LgotaName")
    private String lgotaName;
    @JsonProperty("LgotaVatSum")
    private BigDecimal lgotaVatSum;
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
    @JsonProperty("Origin")
    @Min(value = 1, message = "Тип не может быть ниже 1")
    private int origin = 1;
    @JsonProperty("Marks")
    private Object marks;
    @JsonProperty("ExchangeInfo")
    private ExchangeInfoDTO exchangeInfo;
    private String originName;
    private boolean hasDiscount;
    private Long invoiceItemId;

    @JsonSetter("ordno")
    public void setordno(String ordno) {
        this.ordNo = ordno;
    }

    public void setOrdNo(String ordNo) {
        this.ordNo = ordNo;
    }

    @JsonSetter("comittentname")
    public void setcomittentname(String comittentname) {
        this.comittentName = comittentname;
    }

    public void setComittentName(String comittentName) {
        this.comittentName = comittentName;
    }

    @JsonSetter("comittenttin")
    public void setcomittenttin(String comittenttin) {
        this.comittentTin = comittenttin;
    }

    public void setComittentTin(String comittentTin) {
        this.comittentTin = comittentTin;
    }

    @JsonSetter("comittentvatregcode")
    public void setcomittentvatregcode(String comittentvatregcode) {
        this.comittentVatRegCode = comittentvatregcode;
    }

    public void setComittentVatRegCode(String comittentVatRegCode) {
        this.comittentVatRegCode = comittentVatRegCode;
    }

    @JsonSetter("catalogcode")
    public void setcatalogcode(String catalogcode) {
        this.catalogCode = catalogcode;
    }

    public void setCatalogCode(String catalogCode) {
        this.catalogCode = catalogCode;
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
        this.barcode = barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @JsonSetter("warehouseid")
    public void setWarehouseIdLowerCase(Integer warehouseid) {
        this.warehouseId = warehouseid;
    }

    @JsonSetter("warehouseId")
    public void setWarehouseIdCamelCase(Integer warehouseId) {
        this.warehouseId = warehouseId;
    }

    public void setWarehouseId(Integer warehouseId) {
        this.warehouseId = warehouseId;
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

    @JsonSetter("lgotaName")
    public void setLgotaNameCamelCase(String lgotaName) {
        this.lgotaName = lgotaName;
    }

    public void setLgotaName(String lgotaName) {
        this.lgotaName = lgotaName;
    }

    @JsonSetter("lgotavatsum")
    public void setLgotaVatSumLowerCase(BigDecimal lgotavatsum) {
        this.lgotaVatSum = lgotavatsum;
    }

    @JsonSetter("lgotaVatSum")
    public void setLgotaVatSumCamelCase(BigDecimal lgotaVatSum) {
        this.lgotaVatSum = lgotaVatSum;
    }

    public void setLgotaVatSum(BigDecimal lgotaVatSum) {
        this.lgotaVatSum = lgotaVatSum;
    }

    @JsonSetter("lgotatype")
    public void setLgotaTypeLowerCase(Integer lgotatype) {
        this.lgotaType = lgotatype;
    }

    @JsonSetter("lgotaType")
    public void setLgotaTypeCamelCase(Integer lgotaType) {
        this.lgotaType = lgotaType;
    }

    public void setLgotaType(Integer lgotaType) {
        this.lgotaType = lgotaType;
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
        this.exciseRate = exciserate;
    }

    public void setExciseRate(BigDecimal exciseRate) {
        this.exciseRate = exciseRate;
    }

    @JsonSetter("excisesum")
    public void setexcisesum(BigDecimal excisesum) {
        this.exciseSum = excisesum;
    }

    public void setExciseSum(BigDecimal exciseSum) {
        this.exciseSum = exciseSum;
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
    public void setMarksLowerCase(Object marks) {
        this.marks = marks;
    }

    public void setMarks(Object marks) {
        this.marks = marks;
    }

    @JsonSetter("exchangeinfo")
    public void setexchangeinfo(ExchangeInfoDTO exchangeinfo) {
        this.exchangeInfo = exchangeinfo;
    }

    public void setExchangeInfo(ExchangeInfoDTO exchangeInfo) {
        this.exchangeInfo = exchangeInfo;
    }

    @JsonSetter("origin")
    public void setOriginLowerCase(int origin) {
        this.origin = origin;
    }

    public void setOrigin(int origin) {
        this.origin = origin;
    }
}
