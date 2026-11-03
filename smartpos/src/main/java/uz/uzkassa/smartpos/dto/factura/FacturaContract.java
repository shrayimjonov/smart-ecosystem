package uz.uzkassa.smartpos.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FacturaContract implements Serializable {
    @JsonProperty("ContractNo")
    private String contractNo;
    @JsonProperty("ContractDate")
    private LocalDate contractDate;
}
