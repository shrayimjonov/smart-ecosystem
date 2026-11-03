package uz.uzkassa.silen.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnloadingEmpowermentDTO implements Serializable {
    @JsonProperty("EmpowermentId")
    String empowermentId;
    @JsonProperty("EmpowermentNo")
    String empowermentNo;
    @JsonProperty("EmpowermentDateOfIssue")
    LocalDate empowermentDateOfIssue;
    @JsonProperty("EmpowermentDateOfExpire")
    LocalDate empowermentDateOfExpire;

    @JsonSetter("empowermentid")
    public void setempowermentidLowerCase(String empowermentId) {
        this.empowermentId = empowermentId;
    }

    @JsonSetter("empowermentno")
    public void setempowermentnoLowerCase(String empowermentNo) {
        this.empowermentNo = empowermentNo;
    }

    @JsonSetter("empowermentdateofissue")
    public void setempowermentdateofissueLowerCase(LocalDate empowermentDateOfIssue) {
        this.empowermentDateOfIssue = empowermentDateOfIssue;
    }

    @JsonSetter("empowermentdateofexpire")
    public void setempowermentdateofexpireLowerCase(LocalDate empowermentDateOfExpire) {
        this.empowermentDateOfExpire = empowermentDateOfExpire;
    }
}
