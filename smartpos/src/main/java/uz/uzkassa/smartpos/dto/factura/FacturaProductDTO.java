package uz.uzkassa.smartpos.dto.factura;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("Code")
    private String code;
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
    private String profitRate;
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
    @Min(value = 2, message = "Тип не может быть ниже 1")
    private int origin = 2;
    @JsonProperty("Marks")
    private MarkDTO marks;
    private Long branchId;
}
