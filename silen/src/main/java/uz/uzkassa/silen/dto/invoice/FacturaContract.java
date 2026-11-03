package uz.uzkassa.silen.dto.invoice;

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
public class FacturaContract implements Serializable {
    @JsonProperty("ContractNo")
    private String contractNo;
    @JsonProperty("ContractDate")
    private LocalDate contractDate;

    @JsonSetter("contractno")
    public void setcontractno(String contractno) {
        this.contractNo = contractno;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    @JsonSetter("contractdate")
    public void setcontractdate(LocalDate contractdate) {
        this.contractDate = contractdate;
    }

    public void setContractDate(LocalDate contractDate) {
        this.contractDate = contractDate;
    }
}
