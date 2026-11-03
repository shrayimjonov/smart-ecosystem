package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillUnloadingEmpowermentDTO implements Serializable {
    @JsonProperty("EmpowermentId")
    String empowermentId;
    @JsonProperty("EmpowermentNo")
    String empowermentNo;
    @JsonProperty("EmpowermentDateOfIssue")
    LocalDate empowermentDateOfIssue;
    @JsonProperty("EmpowermentDateOfExpire")
    LocalDate empowermentDateOfExpire;

    @JsonSetter("empowermentid")
    public void setempowermentidToLowerCase(String empowermentId) {
        this.empowermentId = empowermentId;
    }

    @JsonSetter("empowermentno")
    public void setempowermentnoToLowerCase(String empowermentNo) {
        this.empowermentNo = empowermentNo;
    }

    @JsonSetter("empowermentdateofissue")
    public void setempowermentdateofissueToLowerCase(LocalDate empowermentDateOfIssue) {
        this.empowermentDateOfIssue = empowermentDateOfIssue;
    }

    @JsonSetter("empowermentdateofexpire")
    public void setempowermentdateofexpireToLowerCase(LocalDate empowermentDateOfExpire) {
        this.empowermentDateOfExpire = empowermentDateOfExpire;
    }
}
