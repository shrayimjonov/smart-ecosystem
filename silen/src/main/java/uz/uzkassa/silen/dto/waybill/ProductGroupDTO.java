package uz.uzkassa.silen.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductGroupDTO implements Serializable {
    private String id;
    @JsonProperty("LoadingTrustee")
    private PersonDTO loadingTrustee;

    /**
     * Point or Station
     */
    @JsonProperty("LoadingPoint")
    private Point loadingPoint;

    @JsonProperty("UnloadingTrustee")
    private PersonDTO unloadingTrustee;

    /**
     * Point or Station
     */
    @JsonProperty("UnloadingPoint")
    private Point unloadingPoint;

    @JsonProperty("UnloadingEmpowerment")
    private UnloadingEmpowermentDTO unloadingEmpowermentDTO;

    @JsonProperty("ProductInfo")
    private ProductInfoDTO productInfoDTO;

    @JsonSetter("loadingtrustee")
    public void setloadingtrusteeLowerCase(PersonDTO loadingtrustee) {
        this.loadingTrustee = loadingtrustee;
    }

    @JsonSetter("loadingpoint")
    public void setloadingpointLowerCase(Point loadingpoint) {
        this.loadingPoint = loadingpoint;
    }

    @JsonSetter("unloadingtrustee")
    public void setunloadingtrusteeLowerCase(PersonDTO unloadingtrustee) {
        this.unloadingTrustee = unloadingtrustee;
    }

    @JsonSetter("unloadingpoint")
    public void setunloadingpointLowerCase(Point unloadingpoint) {
        this.unloadingPoint = unloadingpoint;
    }

    @JsonSetter("unloadingempowerment")
    public void setunloadingempowermentLowerCase(UnloadingEmpowermentDTO unloadingempowerment) {
        this.unloadingEmpowermentDTO = unloadingempowerment;
    }

    @JsonSetter("productinfo")
    public void setproductinfoLowerCase(ProductInfoDTO productInfoDTO) {
        this.productInfoDTO = productInfoDTO;
    }
}
