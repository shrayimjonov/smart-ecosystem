package uz.uzkassa.cts.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class FacturaEmpowermentDTO implements Serializable {
    @JsonProperty("AgentFacturaId")
    private String agentFacturaId;
    @JsonProperty("EmpowermentNo")
    private String empowermentNo;
    @JsonProperty("EmpowermentDateOfIssue")
    private LocalDate empowermentDateOfIssue;
    @JsonProperty("AgentTin")
    private String agentTin;
    @JsonProperty("AgentPinfl")
    private String agentPinfl;
    @JsonProperty("AgentFio")
    private String agentFio;
}
