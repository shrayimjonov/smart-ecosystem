package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class FacturaProductDTO implements Serializable {
    @JsonProperty("OrdNo")
    private String ordNo;
    @JsonProperty("ComittentName")
    private String comittentName;
    @JsonProperty("ComittentTin")
    private String comittentTin;
    @JsonProperty("ComittentVatRegCode")
    private String comittentVatRegCode;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Serial")
    private String serial;
    @JsonProperty("MeasureId")
    private Long measureId;
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
    private int origin = 3;
    // ADDED NEW
    @JsonProperty("CatalogCode")
    private String catalogCode;
    @JsonProperty("CatalogName")
    private String catalogName;
    @JsonProperty("Barcode")
    private String barcode;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("PackageCode")
    private String packageCode;
    @JsonProperty("PackageName")
    private String packageName;
}
