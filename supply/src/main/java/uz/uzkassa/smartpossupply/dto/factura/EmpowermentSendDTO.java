package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmpowermentSendDTO implements Serializable {

    @JsonProperty("Sign")
    private String sign;

    @JsonProperty("ClientIp")
    private String clientIp;

    @JsonProperty("Action")
    private String action;

    @JsonProperty("EmpowermentId")
    private String empowermentId;

    @JsonProperty("AgentEmpowermentId")
    private String agentEmpowermentId;

    @JsonProperty("Notes")
    private String notes;
}
