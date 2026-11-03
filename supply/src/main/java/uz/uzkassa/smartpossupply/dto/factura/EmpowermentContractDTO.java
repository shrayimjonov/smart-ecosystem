package uz.uzkassa.smartpossupply.dto.factura;

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
public class EmpowermentContractDTO implements Serializable {

    @JsonProperty("ContractNo")
    private String contractNo;

    @JsonProperty("ContractDate")
    private String contractDate;

    public EmpowermentContractDTO(String contractNo, LocalDate contractDate) {
        this.contractNo = contractNo;
        this.contractDate = contractDate != null ? contractDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : null;
    }
}
