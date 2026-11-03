package uz.uzkassa.silen.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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
}
