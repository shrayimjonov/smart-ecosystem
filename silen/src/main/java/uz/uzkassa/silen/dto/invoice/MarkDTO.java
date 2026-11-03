package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@ToString
public class MarkDTO implements Serializable {
    @JsonProperty("ProductType")
    private Integer productType;
    @JsonProperty("KIZ")
    private Set<String> kiz;
    @JsonProperty("NomUpak")
    private Set<String> nomUpak;
    @JsonProperty("IdentTransUpak")
    private Set<String> identTransUpak;

    @JsonSetter("productype")
    public void setproducttype(Integer producttype) {
        this.productType = producttype;
    }

    public void setProductType(Integer productType) {
        this.productType = productType;
    }

    @JsonProperty("kiz")
    public void setKizLowerCase(Set<String> kiz) {
        this.kiz = kiz;
    }

    public void setKiz(Set<String> kiz) {
        this.kiz = kiz;
    }

    @JsonProperty("nomupak")
    public void setnomupak(Set<String> nomupak) {
        this.nomUpak = nomupak;
    }

    public void setNomUpak(Set<String> nomUpak) {
        this.nomUpak = nomUpak;
    }

    @JsonProperty("identtransupak")
    public void setidenttransupak(Set<String> identtransupak) {
        this.identTransUpak = identtransupak;
    }

    public void setIdentTransUpak(Set<String> identTransUpak) {
        this.identTransUpak = identTransUpak;
    }
}
