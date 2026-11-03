package uz.uzkassa.smartpossupply.dto.waybill;


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
public class WaybillProductGroupDTO implements Serializable {
    private Long id;
    @JsonProperty("LoadingTrustee")
    private WaybillPersonDTO loadingTrustee;

    @JsonProperty("LoadingPoint")
    private WaybillPointDTO loadingPoint;

    @JsonProperty("UnloadingTrustee")
    private WaybillPersonDTO unloadingTrustee;

    @JsonProperty("UnloadingPoint")
    private WaybillPointDTO unloadingPoint;

    @JsonProperty("UnloadingEmpowerment")
    private WaybillUnloadingEmpowermentDTO unloadingEmpowermentDTO;

    @JsonProperty("ProductInfo")
    private WaybillProductInfoDTO productInfoDTO;

    @JsonSetter("loadingtrustee")
    public void setloadingtrusteeToLowerCase(WaybillPersonDTO loadingtrustee) {
        this.loadingTrustee = loadingtrustee;
    }

    @JsonSetter("loadingpoint")
    public void setloadingpointToLowerCase(WaybillPointDTO loadingpoint) {
        this.loadingPoint = loadingpoint;
    }

    @JsonSetter("unloadingtrustee")
    public void setunloadingtrusteeToLowerCase(WaybillPersonDTO unloadingtrustee) {
        this.unloadingTrustee = unloadingtrustee;
    }

    @JsonSetter("unloadingpoint")
    public void setunloadingpointToLowerCase(WaybillPointDTO unloadingpoint) {
        this.unloadingPoint = unloadingpoint;
    }

    @JsonSetter("unloadingempowerment")
    public void setunloadingempowermentToLowerCase(WaybillUnloadingEmpowermentDTO unloadingempowerment) {
        this.unloadingEmpowermentDTO = unloadingempowerment;
    }

    @JsonSetter("productinfo")
    public void setproductinfoToLowerCase(WaybillProductInfoDTO productInfoDTO) {
        this.productInfoDTO = productInfoDTO;
    }
}
