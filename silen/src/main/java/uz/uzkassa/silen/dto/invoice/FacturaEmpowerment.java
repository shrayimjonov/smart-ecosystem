package uz.uzkassa.silen.dto.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
public class FacturaEmpowerment implements Serializable {
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
    public void setempowermentdateofissue(LocalDate empowermentdateofissue) {
        this.empowermentDateOfIssue = empowermentdateofissue;
    }

    public void setEmpowermentDateOfIssue(LocalDate empowermentDateOfIssue) {
        this.empowermentDateOfIssue = empowermentDateOfIssue;
    }

    @JsonSetter("agenttin")
    public void setagenttin(String agenttin) {
        this.agentTin = agenttin;
    }

    public void setAgentTin(String agentTin) {
        this.agentTin = agentTin;
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
