package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class WaybillContractDocDTO implements Serializable {
    @JsonProperty("ContractNo")
    private String contractNo;
    @JsonProperty("ContractDate")
    private LocalDate contractDate;

    @JsonSetter("contractno")
    public void setcontractnoToLowerCase(String contractno) {
        this.contractNo = contractno;
    }

    @JsonSetter("contractdate")
    public void setcontractdateToLowerCase(LocalDate contractdate) {
        this.contractDate = contractdate;
    }
}
