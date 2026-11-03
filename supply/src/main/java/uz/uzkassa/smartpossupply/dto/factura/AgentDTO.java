package uz.uzkassa.smartpossupply.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO implements Serializable {

    @JsonProperty("AgentEmpowermentId")
    private String agentEmpowermentId;

    @JsonProperty("AgentTin")
    private String agentTin;

    @JsonProperty("JobTitle")
    private String jobTitle;

    @JsonProperty("Fio")
    private String fio;

    @JsonProperty("Passport")
    private PassportDTO passport;
}
