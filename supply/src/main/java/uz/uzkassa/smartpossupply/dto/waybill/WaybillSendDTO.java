package uz.uzkassa.smartpossupply.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * {
 * "sign": "string",
 * "clientIP": "127.0.0.1"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class WaybillSendDTO implements Serializable {

    @JsonProperty("Sign")
    private String sign;
    @JsonProperty("ClientIp")
    private String clientIp;
    @JsonProperty("WaybillLocalId")
    private String waybillLocalId;
    @JsonProperty("WaybillLocalSignType")
    private String waybillLocalSignType;
    @JsonProperty("ConsignorTinOrPinfl")
    private String consignorTinOrPinfl;
    @JsonProperty("Notes")
    private String notes;

    @JsonSetter("sign")
    public void setsignLowerCase(String sign) {
        this.sign = sign;
    }

    @JsonSetter("clientip")
    public void setclientipLowerCase(String clientip) {
        this.clientIp = clientip;
    }

    @JsonSetter("waybilllocalid")
    public void setwaybilllocalidLowerCase(String waybilllocalid) {
        this.waybillLocalId = waybilllocalid;
    }

    @JsonSetter("waybilllocalsigntype")
    public void setwaybilllocalsigntypeLowerCase(String waybilllocalsigntype) {
        this.waybillLocalSignType = waybilllocalsigntype;
    }

    @JsonSetter("consignortinorpinfl")
    public void setconsignortinorpinflLowerCase(String consignortinorpinfl) {
        this.consignorTinOrPinfl = consignortinorpinfl;
    }

    @JsonSetter("notes")
    public void setnotesLowerCase(String notes) {
        this.notes = notes;
    }
}
