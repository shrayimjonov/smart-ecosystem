package uz.uzkassa.smartpos.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}
