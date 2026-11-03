package uz.uzkassa.smartpossupply.dto.acceptancetransferact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActProductDTO implements Serializable {

    @JsonProperty("OrdNo")
    Integer ordNo;

    @JsonProperty("Name")
    String name;

    @JsonProperty("CatalogCode")
    String catalogCode;

    @JsonProperty("CatalogName")
    String catalogName;

    @JsonProperty("Barcode")
    String barcode;

    @JsonProperty("PackageCode")
    String packageCode;

    @JsonProperty("PackageName")
    String packageName;

    @JsonProperty("Count")
    BigDecimal count;

    BigDecimal price;

    @JsonProperty("Summa")
    BigDecimal summa;

    @JsonProperty("DeliverySum")
    BigDecimal deliverySum;

    String serial;

    Set<String> marks;

    Set<String> aggregations;

    Set<String> blocks;

    @JsonSetter("ordno")
    public void setordno(Integer ordno) {
        this.ordNo = ordno;
    }

    @JsonSetter("name")
    public void setNameLowerCase(String name) {
        this.name = name;
    }

    @JsonSetter("catalogcode")
    public void setcatalogcode(String catalogcode) {
        this.catalogCode = catalogcode != null ? catalogcode.trim() : null;
    }

    @JsonSetter("catalogname")
    public void setcatalogname(String catalogname) {
        this.catalogName = catalogname;
    }

    @JsonSetter("barcode")
    public void setBarcodeLowerCase(String barcode) {
        this.barcode = barcode != null ? barcode.trim() : null;
    }

    @JsonSetter("packagecode")
    public void setpackagecode(String packagecode) {
        this.packageCode = packagecode;
    }

    @JsonSetter("packagename")
    public void setpackagename(String packagename) {
        this.packageName = packagename;
    }

    @JsonSetter("count")
    public void setCountLowerCase(BigDecimal count) {
        this.count = count;
    }

    @JsonSetter("summa")
    public void setSummaLowerCase(BigDecimal summa) {
        this.summa = summa;
    }

    @JsonSetter("deliverysum")
    public void setdeliverysum(BigDecimal deliverysum) {
        this.deliverySum = deliverysum;
    }

    public void setOrdNo(Integer ordNo) {
        this.ordNo = ordNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCatalogCode(String catalogCode) {
        this.catalogCode = catalogCode != null ? catalogCode.trim() : null;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode != null ? barcode.trim() : null;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setCount(BigDecimal count) {
        this.count = count;
    }

    public void setSumma(BigDecimal summa) {
        this.summa = summa;
    }

    public void setDeliverySum(BigDecimal deliverySum) {
        this.deliverySum = deliverySum;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public void setMarks(Set<String> marks) {
        this.marks = marks;
    }

    public void setAggregations(Set<String> aggregations) {
        this.aggregations = aggregations;
    }

    public void setBlocks(Set<String> blocks) {
        this.blocks = blocks;
    }
}
