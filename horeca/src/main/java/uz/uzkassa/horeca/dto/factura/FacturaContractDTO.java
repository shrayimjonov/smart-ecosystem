package uz.uzkassa.horeca.dto.factura;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FacturaContractDTO implements Serializable {

    @JsonProperty("ContractNo")
    private String contractNo;

    @JsonProperty("ContractDate")
    private String contractDate;

    @JsonSetter("contractno")
    public void setcontractno(String contractno) {
        this.contractNo = contractno;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    @JsonSetter("contractdate")
    public void setcontractdate(String contractdate) {
        this.contractDate = contractdate;
    }

    public void setContractDate(String contractDate) {
        this.contractDate = contractDate;
    }
}
