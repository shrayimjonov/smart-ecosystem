package uz.uzkassa.silen.dto.acceptancetransferact;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonProperty("Summa")
    BigDecimal summa;

    @JsonProperty("DeliverySum")
    BigDecimal deliverySum;

    BigDecimal price;

    String serial;

    Set<String> marks = new HashSet<>();

    Set<String> aggregations = new HashSet<>();

    Set<String> blocks = new HashSet<>();

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
        this.catalogCode = catalogcode;
    }

    @JsonSetter("catalogname")
    public void setcatalogname(String catalogname) {
        this.catalogName = catalogname;
    }

    @JsonSetter("barcode")
    public void setBarcodeLowerCase(String barcode) {
        this.barcode = barcode;
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
        this.catalogCode = catalogCode;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public void setMarks(Set<String> marks) {
        this.marks = marks;
    }

    public void setAggregations(Set<String> aggregations) {
        this.aggregations = aggregations;
    }

    public void setBlocks(Set<String> blocks) {
        this.blocks = blocks;
    }

    @JsonIgnore
    public void addMark(String mark) {
        marks.add(mark);
    }

    @JsonIgnore
    public void addAggregation(String aggregation) {
        aggregations.add(aggregation);
    }

    @JsonIgnore
    public void addBlock(String block) {
        blocks.add(block);
    }
}
