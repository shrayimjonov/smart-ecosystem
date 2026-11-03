package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class EmpowermentDocDTO implements Serializable {

    @JsonProperty("EmpowermentNo")
    private String empowermentNo;

    @JsonProperty("EmpowermentDateOfIssue")
    private String empowermentDateOfIssue;

    @JsonProperty("EmpowermentDateOfExpire")
    private String empowermentDateOfExpire;

    public EmpowermentDocDTO(String empowermentNo, LocalDate empowermentDateOfIssue, LocalDate empowermentDateOfExpire) {
        this.empowermentNo = empowermentNo;
        this.empowermentDateOfIssue = empowermentDateOfIssue.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.empowermentDateOfExpire = empowermentDateOfExpire.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
