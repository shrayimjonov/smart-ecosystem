package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class EmpowermentRejectDTO implements Serializable {

    @JsonProperty("Empowerment")
    private EmpowermentDTO empowerment;

    @JsonProperty("Notes")
    private String notes;
}
