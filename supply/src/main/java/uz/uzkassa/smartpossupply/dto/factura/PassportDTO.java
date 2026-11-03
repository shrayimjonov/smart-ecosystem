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
public class PassportDTO implements Serializable {

    @JsonProperty("Number")
    private String number;

    @JsonProperty("DateOfIssue")
    private String dateOfIssue;

    @JsonProperty("IssuedBy")
    private String issuedBy;
}
