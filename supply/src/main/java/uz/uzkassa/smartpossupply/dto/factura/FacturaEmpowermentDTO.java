package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class FacturaEmpowermentDTO implements Serializable {

    @JsonProperty("AgentFacturaId")
    private String agentFacturaId;

    @JsonProperty("EmpowermentNo")
    private String empowermentNo;

    @JsonProperty("EmpowermentDateOfIssue")
    private String empowermentDateOfIssue;

    @JsonProperty("AgentPinfl")
    private String agentPinfl;

    @JsonProperty("AgentFio")
    private String agentFio;

    @JsonSetter("agentfacturaid")
    public void setagentfacturaid(String agentfacturaid) {
        this.agentFacturaId = agentfacturaid;
    }

    public void setAgentFacturaId(String agentFacturaId) {
        this.agentFacturaId = agentFacturaId;
    }

    @JsonSetter("empowermentno")
    public void setempowermentno(String empowermentno) {
        this.empowermentNo = empowermentno;
    }

    public void setEmpowermentNo(String empowermentNo) {
        this.empowermentNo = empowermentNo;
    }

    @JsonSetter("empowermentdateofissue")
    public void setempowermentdateofissue(String empowermentdateofissue) {
        this.empowermentDateOfIssue = empowermentdateofissue;
    }

    public void setEmpowermentDateOfIssue(String empowermentDateOfIssue) {
        this.empowermentDateOfIssue = empowermentDateOfIssue;
    }

    @JsonSetter("agentpinfl")
    public void setagentpinfl(String agentpinfl) {
        this.agentPinfl = agentpinfl;
    }

    public void setAgentPinfl(String agentPinfl) {
        this.agentPinfl = agentPinfl;
    }

    @JsonSetter("agentfio")
    public void setagentfio(String agentfio) {
        this.agentFio = agentfio;
    }

    public void setAgentFio(String agentFio) {
        this.agentFio = agentFio;
    }
}
